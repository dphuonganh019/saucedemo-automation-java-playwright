import glob
import os
import xml.etree.ElementTree as ET

import anthropic
import requests
from dotenv import load_dotenv

load_dotenv()

REQUIRED_ENV_VARS = [
    "ANTHROPIC_API_KEY",
    "JIRA_BASE_URL",
    "JIRA_EMAIL",
    "JIRA_API_TOKEN",
    "JIRA_PROJECT_KEY",
]

SEVERITY_FIELD_ID = "customfield_10043"

SEVERITY_TO_PRIORITY = {
    "Blocker": "Highest",
    "Critical": "High",
    "Major": "Medium",
    "Minor": "Low",
    "Trivial": "Lowest",
}

BUG_REPORT_TOOL = {
    "name": "file_bug_report",
    "description": "File a structured bug report drafted from an automation test failure",
    "input_schema": {
        "type": "object",
        "properties": {
            "summary": {
                "type": "string",
                "description": "One short line summarizing the bug, used as the JIRA issue title",
            },
            "overview": {
                "type": "string",
                "description": "One to two sentences describing the bug in plain language, used as an introductory paragraph before the reproduction steps. Do not mention the automated test class, method name, or the word 'automation' here.",
            },
            "steps_to_reproduce": {
                "type": "string",
                "description": "Numbered steps a human QA tester would follow by hand in the browser to reproduce the issue (e.g. 'Navigate to ...', 'Click ...', 'Observe ...'). Write it exactly as a manual test case step list. Do not mention the automated test, script, class, or method name anywhere in these steps.",
            },
            "expected_result": {
                "type": "string",
                "description": "What should have happened",
            },
            "actual_result": {
                "type": "string",
                "description": "Plain-language description of what actually happened. Do not paste the raw stack trace here, just describe the failure.",
            },
            "severity": {
                "type": "string",
                "enum": ["Blocker", "Critical", "Major", "Minor", "Trivial"],
                "description": "Severity assessed from the impact of the failure",
            },
        },
        "required": [
            "summary",
            "overview",
            "steps_to_reproduce",
            "expected_result",
            "actual_result",
            "severity",
        ],
    },
}


def require_env(name):
    value = os.environ.get(name)
    if not value:
        raise RuntimeError(f"Missing required environment variable: {name}")
    return value


def parse_failures(reports_dir="target/surefire-reports"):
    failures = []
    for xml_file in glob.glob(os.path.join(reports_dir, "*.xml")):
        root = ET.parse(xml_file).getroot()
        for testcase in root.findall("testcase"):
            failure_node = testcase.find("failure")
            if failure_node is None:
                failure_node = testcase.find("error")
            if failure_node is not None:
                failures.append({
                    "class": testcase.get("classname"),
                    "test": testcase.get("name"),
                    "message": failure_node.get("message") or "",
                    "stacktrace": failure_node.text or "",
                })
    return failures


def draft_bug_report(client, failure):
    prompt = f"""You are a QA lead. Based on the automated test failure below, file a bug report by calling the file_bug_report tool. Write everything in English. Do not use markdown syntax (no #, *, **) anywhere in the field values, they will be rendered as plain text.

Use the test class/method and stack trace only to understand what broke. The "overview" field must not mention the test name (it is appended separately by the caller). The "steps_to_reproduce" field must read like a manual QA test case performed by hand in the browser, not a description of running an automated script.

Test class: {failure['class']}
Test method: {failure['test']}
Error message: {failure['message']}
Stack trace:
{failure['stacktrace']}
"""
    message = client.messages.create(
        model="claude-sonnet-5",
        max_tokens=1536,
        tools=[BUG_REPORT_TOOL],
        tool_choice={"type": "tool", "name": "file_bug_report"},
        messages=[{"role": "user", "content": prompt}],
    )
    for block in message.content:
        if block.type == "tool_use":
            return block.input
    raise RuntimeError("Anthropic response has no tool_use block")


def build_description(report, failure):
    def heading(text):
        return {"type": "heading", "attrs": {"level": 3}, "content": [{"type": "text", "text": text}]}

    def paragraph(text):
        return {"type": "paragraph", "content": [{"type": "text", "text": text}]}

    def code_block(text):
        return {"type": "codeBlock", "content": [{"type": "text", "text": text}]}

    detected_by = f"{failure['class']}.{failure['test']}"
    overview = f"{report['overview']} Detected by automated test: {detected_by}."

    raw_lines = [
        line.strip()
        for line in (failure["message"] or failure["stacktrace"] or "").splitlines()
        if line.strip()
    ]
    error_log = "\n".join(raw_lines[:2]) or "(no error message captured)"

    return {
        "type": "doc",
        "version": 1,
        "content": [
            paragraph(overview),
            heading("Steps to Reproduce"),
            paragraph(report["steps_to_reproduce"]),
            heading("Expected Result"),
            paragraph(report["expected_result"]),
            heading("Actual Result"),
            paragraph(report["actual_result"]),
            heading("Error Log"),
            code_block(error_log),
        ],
    }


def create_jira_bug(base_url, auth, project_key, report, description):
    url = f"{base_url}/rest/api/3/issue"
    priority = SEVERITY_TO_PRIORITY[report["severity"]]
    payload = {
        "fields": {
            "project": {"key": project_key},
            "summary": report["summary"],
            "description": description,
            "issuetype": {"name": "Bug"},
            "labels": ["automation", "claude-generated"],
            "priority": {"name": priority},
            SEVERITY_FIELD_ID: {"value": report["severity"]},
        }
    }
    response = requests.post(url, json=payload, auth=auth)
    if not response.ok:
        raise RuntimeError(f"JIRA API {response.status_code}: {response.text}")
    return response.json()["key"]


def find_screenshot(failure, screenshots_dir="test-output/screenshots"):
    class_simple = failure["class"].split(".")[-1]
    method = failure["test"].split("(")[0]
    pattern = os.path.join(screenshots_dir, f"FAILURE_{class_simple}_{method}_*.png")
    matches = glob.glob(pattern)
    if not matches:
        return None
    return max(matches, key=os.path.getmtime)


def attach_screenshot(base_url, auth, issue_key, filepath):
    url = f"{base_url}/rest/api/3/issue/{issue_key}/attachments"
    headers = {"X-Atlassian-Token": "no-check"}
    with open(filepath, "rb") as f:
        response = requests.post(url, auth=auth, headers=headers, files={"file": f})
    response.raise_for_status()


def main():
    for name in REQUIRED_ENV_VARS:
        require_env(name)

    failures = parse_failures()
    if not failures:
        print("No failed tests found, nothing to report.")
        return

    client = anthropic.Anthropic(api_key=os.environ["ANTHROPIC_API_KEY"])
    auth = (os.environ["JIRA_EMAIL"], os.environ["JIRA_API_TOKEN"])
    base_url = os.environ["JIRA_BASE_URL"].rstrip("/")
    project_key = os.environ["JIRA_PROJECT_KEY"]

    for failure in failures:
        report = draft_bug_report(client, failure)
        description = build_description(report, failure)
        issue_key = create_jira_bug(base_url, auth, project_key, report, description)
        print(f"Created {issue_key} ({report['severity']}) for {failure['class']}.{failure['test']}")

        screenshot = find_screenshot(failure)
        if screenshot:
            attach_screenshot(base_url, auth, issue_key, screenshot)
            print(f"  Attached screenshot: {screenshot}")


if __name__ == "__main__":
    main()
