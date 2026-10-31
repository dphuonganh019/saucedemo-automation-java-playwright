import argparse
import glob
import hashlib
import json
import os
import re
import sys
import xml.etree.ElementTree as ET
from datetime import datetime, timedelta, timezone

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

MODEL = "claude-sonnet-5-5"
FALLBACK_BETA = "server-side-fallback-2026-07-01"

REPORTS_DIR = "target/surefire-reports"
SCREENSHOTS_DIR = "test-output/screenshots"
TEST_SOURCE_DIR = "src/test/java"
CONFIG_FILE = "src/test/resources/config.properties"

SEVERITY_FIELD_ID = "customfield_10043"

SEVERITY_TO_PRIORITY = {
    "Blocker": "Highest",
    "Critical": "High",
    "Major": "Medium",
    "Minor": "Low",
    "Trivial": "Lowest",
}
DEFAULT_SEVERITY = "Major"

BASE_LABELS = ["automation", "claude-generated"]
REGRESSION_LABEL = "regression"
FINGERPRINT_PREFIX = "afp-"

# A bug closed with one of these resolutions was a human decision, so a recurrence must not reopen it
NO_REOPEN_RESOLUTIONS = {"won't fix", "won't do", "not a bug", "works as designed", "duplicate", "declined"}
DEFAULT_REOPEN_TRANSITIONS = ["Reopen", "Reopen Issue", "Reopened", "To Do", "Open", "Backlog"]
DEFAULT_REOPEN_WINDOW_DAYS = 14

ISSUE_FIELDS = ["summary", "status", "resolution", "resolutiondate", "fixVersions", "labels", SEVERITY_FIELD_ID]

STRING_LITERAL = r'"((?:[^"\\]|\\.)*)"'
DISPLAY_NAME = r'@DisplayName\(\s*((?:"(?:[^"\\]|\\.)*"\s*\+?\s*)+)\)(?:\s*@\w+(?:\([^)]*\))?)*'
SUB_FAILURE_START = re.compile(r"^\s*[\w.$]+(?:Error|Exception|Failure):\s?(.*)$")

TRIAGE_SCHEMA = {
    "type": "object",
    "properties": {
        "not_product_bugs": {
            "type": "array",
            "items": {
                "type": "object",
                "properties": {
                    "point_ids": {"type": "array", "items": {"type": "string"}},
                    "reason": {"type": "string"},
                },
                "required": ["point_ids", "reason"],
                "additionalProperties": False,
            },
        },
        "new_bugs": {
            "type": "array",
            "items": {
                "type": "object",
                "properties": {
                    "point_ids": {"type": "array", "items": {"type": "string"}},
                    "summary": {"type": "string"},
                    "overview": {"type": "string"},
                    "steps_to_reproduce": {"type": "string"},
                    "expected_result": {"type": "string"},
                    "actual_result": {"type": "string"},
                    "severity": {"type": "string", "enum": list(SEVERITY_TO_PRIORITY)},
                },
                "required": [
                    "point_ids",
                    "summary",
                    "overview",
                    "steps_to_reproduce",
                    "expected_result",
                    "actual_result",
                    "severity",
                ],
                "additionalProperties": False,
            },
        },
        "attach_to_open_issues": {
            "type": "array",
            "items": {
                "type": "object",
                "properties": {
                    "issue_key": {"type": "string"},
                    "point_ids": {"type": "array", "items": {"type": "string"}},
                },
                "required": ["issue_key", "point_ids"],
                "additionalProperties": False,
            },
        },
        "recurrences": {
            "type": "array",
            "items": {
                "type": "object",
                "properties": {
                    "issue_key": {"type": "string"},
                    "point_ids": {"type": "array", "items": {"type": "string"}},
                    "steps_to_reproduce": {"type": "string"},
                    "actual_result": {"type": "string"},
                },
                "required": ["issue_key", "point_ids", "steps_to_reproduce", "actual_result"],
                "additionalProperties": False,
            },
        },
    },
    "required": ["not_product_bugs", "new_bugs", "attach_to_open_issues", "recurrences"],
    "additionalProperties": False,
}

TRIAGE_PROMPT = """You are a QA lead triaging the automated UI test failures of one CI run against a web application.

Each item in FAILURE POINTS is one failed check (one expected result) inside one step of one test case. A single test case can contain several failure points with different root causes, and several test cases can fail because of the same defect.

Do the following and return the result in the required JSON format:

1. Classify every failure point. Put a point in "not_product_bugs" only when the evidence clearly shows a flaky test, an infrastructure or environment problem (timeouts, network errors, browser crashes, slow page loads) or a defect in the test script itself. A check where the application shows the wrong content, data or state is a product bug.
2. Group the NEW product-bug points by root cause into "new_bugs". Points caused by the same underlying defect go into ONE bug, even when they come from different test cases or their messages are worded differently. Points with different root causes go into separate bugs, even when they come from the same test case.
3. If a NEW point has the same root cause as one of the OPEN ISSUES, put it in "attach_to_open_issues" with that issue key instead of creating a new bug.
4. RECURRING points belong to a bug that was closed before and now fails again; their issue key is given. Unless they are not product bugs, put them in "recurrences" under that issue key with fresh steps to reproduce and the actual result observed in this run.
5. Every point id must appear exactly once across not_product_bugs, new_bugs, attach_to_open_issues and recurrences.

Writing rules: write everything in English and do not use markdown syntax (no #, *, **), the text is rendered as plain text. "summary" is one short line used as the Jira title. "overview" is one or two plain sentences and must not mention test names, classes or the word automation. "steps_to_reproduce" must read like numbered manual QA steps performed by hand in the browser (Navigate to ..., Click ..., Observe ...), never a description of running a script. "actual_result" describes the observed behaviour in plain language without stack traces.

FAILURE POINTS (NEW):
{new_points}

FAILURE POINTS (RECURRING):
{recurring_points}

OPEN ISSUES:
{open_issues}
"""


def require_env(name):
    value = os.environ.get(name)
    if not value:
        raise RuntimeError(f"Missing required environment variable: {name}")
    return value


def read_config_value(key, config_file=CONFIG_FILE):
    if not os.path.exists(config_file):
        return None
    with open(config_file, encoding="utf-8") as f:
        for line in f:
            name, sep, value = line.partition("=")
            if sep and name.strip() == key:
                return value.strip()
    return None


def parse_failures(reports_dir=REPORTS_DIR):
    failures = {}
    for xml_file in glob.glob(os.path.join(reports_dir, "*.xml")):
        root = ET.parse(xml_file).getroot()
        for testcase in root.iter("testcase"):
            failure_node = testcase.find("failure")
            if failure_node is None:
                failure_node = testcase.find("error")
            if failure_node is not None:
                key = (testcase.get("classname"), testcase.get("name"))
                failures.setdefault(key, {
                    "class": key[0],
                    "test": key[1],
                    "message": failure_node.get("message") or "",
                    "stacktrace": failure_node.text or "",
                })
    return list(failures.values())


def read_display_names(class_name, cache):
    if class_name in cache:
        return cache[class_name]

    def join_literals(args):
        text = "".join(re.findall(STRING_LITERAL, args)).replace('\\"', '"')
        return re.sub(r"\s*</br>\s*", "\n", text).strip()

    class_title, step_titles = None, {}
    path = os.path.join(TEST_SOURCE_DIR, *class_name.split(".")) + ".java"
    if os.path.exists(path):
        with open(path, encoding="utf-8") as f:
            source = f.read()
        class_match = re.search(DISPLAY_NAME + r"\s*public\s+class", source)
        if class_match:
            class_title = join_literals(class_match.group(1))
        for match in re.finditer(DISPLAY_NAME + r"\s*public\s+void\s+(\w+)", source):
            step_titles[match.group(2)] = join_literals(match.group(1))
    cache[class_name] = (class_title, step_titles)
    return cache[class_name]


def split_failed_checks(failure):
    text = failure["message"] or next(iter(failure["stacktrace"].strip().splitlines()), "")
    lines = text.splitlines()
    if lines and lines[0].startswith("Multiple Failures"):
        checks, current = [], None
        for line in lines[1:]:
            match = SUB_FAILURE_START.match(line)
            if match:
                current = [match.group(1)]
                checks.append(current)
            elif current is not None and line.strip():
                current.append(line.strip())
        if checks:
            return [" ".join(check).strip() for check in checks]
    return [text.strip() or "(no error message captured)"]


def normalize_check(check):
    first_line = next(iter(check.strip().splitlines()), "")
    first_line = re.sub(r"^[\w.$]+(?:Error|Exception):\s*", "", first_line)
    first_line = re.split(r"\s==>\s|,?\s*Actual:", first_line, maxsplit=1, flags=re.IGNORECASE)[0]
    first_line = re.sub(r"\d+", "#", first_line)
    return re.sub(r"\s+", " ", first_line).strip().lower()


def fingerprint(failure, check):
    raw = f"{failure['class']}#{failure['test']}#{normalize_check(check)}"
    return FINGERPRINT_PREFIX + hashlib.sha1(raw.encode("utf-8")).hexdigest()[:10]


def find_screenshot(failure, screenshots_dir=SCREENSHOTS_DIR):
    class_simple = failure["class"].split(".")[-1]
    method = failure["test"].split("(")[0]
    pattern = os.path.join(screenshots_dir, f"FAILURE_{class_simple}_{method}_*.png")
    matches = glob.glob(pattern)
    if not matches:
        return None
    return max(matches, key=os.path.getmtime)


def build_failure_points(failures):
    points, seen, cache = [], set(), {}
    for failure in failures:
        class_title, step_titles = read_display_names(failure["class"], cache)
        method = re.split(r"[(\[]", failure["test"])[0]
        screenshot = find_screenshot(failure)
        stack_lines = [line.rstrip() for line in failure["stacktrace"].splitlines() if line.strip()]
        for check in split_failed_checks(failure):
            point_fingerprint = fingerprint(failure, check)
            if point_fingerprint in seen:
                continue
            seen.add(point_fingerprint)
            points.append({
                "id": f"P{len(points) + 1}",
                "tc_id": failure["class"].split(".")[-1],
                "class": failure["class"],
                "test": failure["test"],
                "method": method,
                "tc_title": class_title or failure["class"].split(".")[-1],
                "step_title": step_titles.get(method, ""),
                "check": check[:500],
                "stacktrace": "\n".join(stack_lines[:15]),
                "fingerprint": point_fingerprint,
                "screenshot": screenshot,
            })
    return points


def build_run_context():
    server = os.environ.get("GITHUB_SERVER_URL", "https://github.com")
    repository = os.environ.get("GITHUB_REPOSITORY")
    run_id = os.environ.get("GITHUB_RUN_ID")
    return {
        "date": datetime.now(timezone.utc).strftime("%Y-%m-%d %H:%M UTC"),
        "run_url": f"{server}/{repository}/actions/runs/{run_id}" if repository and run_id else "local run",
        "commit": (os.environ.get("GITHUB_SHA") or "local")[:7],
        "base_url": read_config_value("baseUrl") or "unknown",
        "app_version": os.environ.get("APP_VERSION") or None,
    }


def run_line(run):
    return f"CI run {run['run_url']} (commit {run['commit']}) on {run['date']}"


class Jira:
    def __init__(self, base_url, auth, project_key, dry_run=False):
        self.base_url = base_url
        self.auth = auth
        self.project_key = project_key
        self.dry_run = dry_run
        self.dry_run_counter = 0

    def _request(self, method, path, **kwargs):
        response = requests.request(method, f"{self.base_url}{path}", auth=self.auth, timeout=60, **kwargs)
        if not response.ok:
            raise RuntimeError(f"JIRA API {method} {path} {response.status_code}: {response.text}")
        return response.json() if response.content else None

    def _write(self, description, method, path, **kwargs):
        if self.dry_run:
            print(f"  [dry-run] {description}")
            return None
        return self._request(method, path, **kwargs)

    def search(self, jql):
        issues, token = [], None
        while True:
            body = {"jql": jql, "fields": ISSUE_FIELDS, "maxResults": 100}
            if token:
                body["nextPageToken"] = token
            data = self._request("POST", "/rest/api/3/search/jql", json=body)
            issues.extend(data.get("issues", []))
            token = data.get("nextPageToken")
            if not token:
                return issues

    def search_by_fingerprints(self, fingerprints):
        issues = {}
        for start in range(0, len(fingerprints), 50):
            labels = ", ".join(f'"{label}"' for label in fingerprints[start:start + 50])
            for issue in self.search(f'project = "{self.project_key}" AND labels in ({labels})'):
                issues[issue["key"]] = issue
        return list(issues.values())

    def search_open_automation_bugs(self):
        return self.search(
            f'project = "{self.project_key}" AND labels = "automation" AND statusCategory != Done ORDER BY created DESC'
        )

    def create_issue(self, fields):
        if self.dry_run:
            self.dry_run_counter += 1
            key = f"DRY-{self.dry_run_counter}"
            print(f"  [dry-run] create {key}: {fields['summary']} | labels={fields['labels']}")
            return key
        payload = {"fields": {"project": {"key": self.project_key}, "issuetype": {"name": "Bug"}, **fields}}
        return self._request("POST", "/rest/api/3/issue", json=payload)["key"]

    def add_comment(self, issue_key, body):
        self._write(f"comment on {issue_key}", "POST", f"/rest/api/3/issue/{issue_key}/comment", json={"body": body})

    def add_labels(self, issue_key, labels):
        update = {"labels": [{"add": label} for label in labels]}
        self._write(f"add labels {labels} to {issue_key}", "PUT", f"/rest/api/3/issue/{issue_key}", json={"update": update})

    def find_reopen_transition(self, issue_key):
        transitions = self._request("GET", f"/rest/api/3/issue/{issue_key}/transitions")["transitions"]
        configured = os.environ.get("JIRA_REOPEN_TRANSITION")
        for name in [configured] if configured else DEFAULT_REOPEN_TRANSITIONS:
            for transition in transitions:
                if transition["name"].lower() == name.lower() and transition["to"]["statusCategory"]["key"] != "done":
                    return transition
        return None

    def transition(self, issue_key, transition):
        self._write(
            f"move {issue_key} via transition [{transition['name']}]",
            "POST",
            f"/rest/api/3/issue/{issue_key}/transitions",
            json={"transition": {"id": transition["id"]}},
        )

    def link_relates(self, issue_key, other_key):
        payload = {"type": {"name": "Relates"}, "inwardIssue": {"key": issue_key}, "outwardIssue": {"key": other_key}}
        self._write(f"link {issue_key} relates to {other_key}", "POST", "/rest/api/3/issueLink", json=payload)

    def attach(self, issue_key, filepath):
        if self.dry_run:
            print(f"  [dry-run] attach {filepath} to {issue_key}")
            return
        headers = {"X-Atlassian-Token": "no-check"}
        with open(filepath, "rb") as f:
            response = requests.post(
                f"{self.base_url}/rest/api/3/issue/{issue_key}/attachments",
                auth=self.auth,
                headers=headers,
                files={"file": f},
                timeout=60,
            )
        response.raise_for_status()


def is_done(issue):
    return issue["fields"]["status"]["statusCategory"]["key"] == "done"


def resolution_name(issue):
    return ((issue["fields"].get("resolution") or {}).get("name") or "").replace("\u2019", "'")


def parse_jira_date(value):
    if not value:
        return None
    return datetime.strptime(value, "%Y-%m-%dT%H:%M:%S.%f%z")


def match_known_issues(points, issues):
    issues_by_fingerprint = {}
    for issue in issues:
        for label in issue["fields"].get("labels", []):
            if label.startswith(FINGERPRINT_PREFIX):
                issues_by_fingerprint.setdefault(label, []).append(issue)

    known = {"still_failing": {}, "recurring": {}, "wont_fix": {}, "new": []}
    for point in points:
        candidates = issues_by_fingerprint.get(point["fingerprint"], [])
        if not candidates:
            known["new"].append(point)
            continue
        open_issues = [issue for issue in candidates if not is_done(issue)]
        if open_issues:
            bucket, issue = "still_failing", open_issues[0]
        else:
            issue = max(candidates, key=lambda i: i["fields"].get("resolutiondate") or "")
            bucket = "wont_fix" if resolution_name(issue).lower() in NO_REOPEN_RESOLUTIONS else "recurring"
        known[bucket].setdefault(issue["key"], {"issue": issue, "points": []})["points"].append(point)
    return known


def recurrence_action(issue, now=None):
    now = now or datetime.now(timezone.utc)
    fix_versions = issue["fields"].get("fixVersions") or []
    if fix_versions:
        return "regression" if any(version.get("released") for version in fix_versions) else "reopen"
    window = int(os.environ.get("REOPEN_WINDOW_DAYS") or DEFAULT_REOPEN_WINDOW_DAYS)
    resolved = parse_jira_date(issue["fields"].get("resolutiondate"))
    if resolved and now - resolved <= timedelta(days=window):
        return "reopen"
    return "regression"


def describe_point_for_triage(point, issue_key=None):
    data = {
        "id": point["id"],
        "test_case": f"{point['tc_id']}: {point['tc_title']}",
        "step": point["step_title"] or point["method"],
        "failed_check": point["check"],
        "stack_trace": point["stacktrace"],
    }
    if issue_key:
        data["issue_key"] = issue_key
    return data


def triage_with_claude(client, new_points, recurring, open_issues):
    recurring_points = [
        {**describe_point_for_triage(point, key), "previous_summary": entry["issue"]["fields"]["summary"]}
        for key, entry in recurring.items()
        for point in entry["points"]
    ]
    open_issue_list = [
        {
            "issue_key": issue["key"],
            "summary": issue["fields"]["summary"],
            "test_cases": [label for label in issue["fields"].get("labels", []) if label.startswith("TC_")],
        }
        for issue in open_issues[:50]
    ]
    prompt = TRIAGE_PROMPT.format(
        new_points=json.dumps([describe_point_for_triage(point) for point in new_points], indent=2),
        recurring_points=json.dumps(recurring_points, indent=2),
        open_issues=json.dumps(open_issue_list, indent=2),
    )
    response = client.beta.messages.create(
        model=MODEL,
        max_tokens=16000,
        betas=[FALLBACK_BETA],
        fallbacks="default",
        output_config={"format": {"type": "json_schema", "schema": TRIAGE_SCHEMA}},
        messages=[{"role": "user", "content": prompt}],
    )
    if response.stop_reason in ("refusal", "max_tokens"):
        raise RuntimeError(f"Anthropic response stopped with reason: {response.stop_reason}")
    blocks = list(response.content)
    last_fallback = max((i for i, block in enumerate(blocks) if block.type == "fallback"), default=-1)
    text = "".join(block.text for block in blocks[last_fallback + 1:] if block.type == "text")
    return json.loads(text)


def expected_from_step(step_title):
    match = re.search(r"Expected Results?:\s*(.*)", step_title or "", flags=re.IGNORECASE | re.DOTALL)
    return match.group(1).strip() if match else ""


def fallback_report(points):
    first = points[0]
    return {
        "summary": f"{first['tc_id']} ({first['method']}): {first['check'].splitlines()[0][:150]}",
        "overview": "Automatic triage was not available, so this report was built directly from the failed check.",
        "steps_to_reproduce": first["step_title"] or "(no step description available)",
        "expected_result": expected_from_step(first["step_title"]) or "(see the failed check)",
        "actual_result": first["check"],
        "severity": DEFAULT_SEVERITY,
    }


def resolve_triage(triage, new_points, recurring, open_issue_keys):
    new_ids = {point["id"] for point in new_points}
    recurring_key_of = {point["id"]: key for key, entry in recurring.items() for point in entry["points"]}
    points_by_id = {point["id"]: point for point in new_points}
    points_by_id.update({point["id"]: point for entry in recurring.values() for point in entry["points"]})
    assigned = set()

    def take(ids, allowed):
        taken = []
        for point_id in ids:
            if point_id in allowed and point_id not in assigned:
                assigned.add(point_id)
                taken.append(points_by_id[point_id])
        return taken

    triage = triage or {}
    resolved = {"not_product_bugs": [], "new_bugs": [], "attachments": {}, "recurrences": {}}

    for entry in triage.get("not_product_bugs", []):
        points = take(entry["point_ids"], points_by_id)
        if points:
            resolved["not_product_bugs"].append({"points": points, "reason": entry["reason"]})

    for entry in triage.get("recurrences", []):
        for point in take(entry["point_ids"], recurring_key_of):
            recurrence = resolved["recurrences"].setdefault(recurring_key_of[point["id"]], {
                "points": [],
                "steps_to_reproduce": entry["steps_to_reproduce"],
                "actual_result": entry["actual_result"],
            })
            recurrence["points"].append(point)

    for entry in triage.get("attach_to_open_issues", []):
        if entry["issue_key"] in open_issue_keys:
            points = take(entry["point_ids"], new_ids)
            if points:
                resolved["attachments"].setdefault(entry["issue_key"], []).extend(points)

    for entry in triage.get("new_bugs", []):
        points = take(entry["point_ids"], new_ids)
        if points:
            report = {name: entry[name] for name in fallback_report(points)}
            resolved["new_bugs"].append({"report": report, "points": points})

    # Points the model skipped are still reported so that no failure is ever lost
    for point in new_points:
        if point["id"] not in assigned:
            resolved["new_bugs"].append({"report": fallback_report([point]), "points": [point]})
    for point_id, key in recurring_key_of.items():
        if point_id not in assigned:
            point = points_by_id[point_id]
            recurrence = resolved["recurrences"].setdefault(key, {
                "points": [],
                "steps_to_reproduce": point["step_title"] or "(no step description available)",
                "actual_result": point["check"],
            })
            recurrence["points"].append(point)
    return resolved


def text_nodes(text):
    nodes = []
    for index, line in enumerate((text or "").splitlines() or [""]):
        if index:
            nodes.append({"type": "hardBreak"})
        if line:
            nodes.append({"type": "text", "text": line})
    return nodes


def paragraph(text):
    return {"type": "paragraph", "content": text_nodes(text)}


def heading(text):
    return {"type": "heading", "attrs": {"level": 3}, "content": [{"type": "text", "text": text}]}


def bullet_list(items):
    return {
        "type": "bulletList",
        "content": [{"type": "listItem", "content": [paragraph(item)]} for item in items],
    }


def code_block(text):
    return {"type": "codeBlock", "content": [{"type": "text", "text": text}]}


def doc(*content):
    return {"type": "doc", "version": 1, "content": list(content)}


def affected_items(points):
    items = []
    for point in points:
        step = point["step_title"].replace("\n", " ") if point["step_title"] else point["method"]
        items.append(
            f"{point['tc_id']}: {point['tc_title']}\n"
            f"Step [{point['method']}]: {step}\n"
            f"Failed check: {point['check'].splitlines()[0]}\n"
            f"Fingerprint: {point['fingerprint']}"
        )
    return items


def environment_items(run):
    items = [
        f"Date: {run['date']}",
        f"CI run: {run['run_url']}",
        f"Test commit: {run['commit']}",
        f"Application URL: {run['base_url']}",
    ]
    if run["app_version"]:
        items.append(f"Application version: {run['app_version']}")
    return items


def build_description(report, points, run, intro=None):
    error_log = "\n".join(f"[{point['tc_id']}] {point['check']}" for point in points)
    content = [paragraph(intro)] if intro else []
    content += [
        paragraph(report["overview"]),
        heading("Steps to Reproduce"),
        paragraph(report["steps_to_reproduce"]),
        heading("Expected Result"),
        paragraph(report["expected_result"]),
        heading("Actual Result"),
        paragraph(report["actual_result"]),
        heading(f"Affected Test Cases ({len({point['tc_id'] for point in points})})"),
        bullet_list(affected_items(points)),
        heading("Environment"),
        bullet_list(environment_items(run)),
        heading("Error Log"),
        code_block(error_log),
    ]
    return doc(*content)


def labels_for(points, *extra):
    labels = list(BASE_LABELS) + list(extra)
    for point in points:
        for label in (point["tc_id"], point["fingerprint"]):
            if label not in labels:
                labels.append(label)
    return labels


def attach_screenshots(jira, issue_key, points):
    for screenshot in dict.fromkeys(point["screenshot"] for point in points if point["screenshot"]):
        jira.attach(issue_key, screenshot)


def create_bug(jira, report, points, run, extra_labels=(), intro=None):
    severity = report["severity"] if report["severity"] in SEVERITY_TO_PRIORITY else DEFAULT_SEVERITY
    fields = {
        "summary": report["summary"],
        "description": build_description(report, points, run, intro),
        "labels": labels_for(points, *extra_labels),
        "priority": {"name": SEVERITY_TO_PRIORITY[severity]},
        SEVERITY_FIELD_ID: {"value": severity},
    }
    issue_key = jira.create_issue(fields)
    attach_screenshots(jira, issue_key, points)
    return issue_key


def handle_recurrence(jira, issue, recurrence, run):
    points = recurrence["points"]
    old_key = issue["key"]
    action = recurrence_action(issue)
    transition = jira.find_reopen_transition(old_key) if action == "reopen" else None
    if action == "reopen" and transition is None:
        print(f"  No reopen transition available on {old_key}, filing a regression bug instead")
        action = "regression"

    if action == "reopen":
        jira.transition(old_key, transition)
        jira.add_labels(old_key, labels_for(points, REGRESSION_LABEL))
        jira.add_comment(old_key, doc(
            paragraph(f"Regression detected: this bug was closed but has reappeared in {run_line(run)}."),
            heading("Steps to Reproduce"),
            paragraph(recurrence["steps_to_reproduce"]),
            heading("Actual Result"),
            paragraph(recurrence["actual_result"]),
            heading("Affected Test Cases"),
            bullet_list(affected_items(points)),
            heading("Environment"),
            bullet_list(environment_items(run)),
        ))
        attach_screenshots(jira, old_key, points)
        return f"Reopened {old_key}"

    old_fields = issue["fields"]
    old_summary = re.sub(r"^\[Regression\]\s*", "", old_fields["summary"])
    report = {
        "summary": f"[Regression] {old_summary}",
        "overview": f"Previously reported as: {old_summary}",
        "steps_to_reproduce": recurrence["steps_to_reproduce"],
        "expected_result": "\n".join(dict.fromkeys(
            expected_from_step(point["step_title"]) or point["check"] for point in points
        )),
        "actual_result": recurrence["actual_result"],
        "severity": (old_fields.get(SEVERITY_FIELD_ID) or {}).get("value") or DEFAULT_SEVERITY,
    }
    closed_on = (old_fields.get("resolutiondate") or "")[:10] or "an earlier date"
    intro = (
        f"Regression of {old_key} ({resolution_name(issue) or 'closed'} on {closed_on}). "
        f"The defect has reappeared in {run_line(run)}."
    )
    new_key = create_bug(jira, report, points, run, extra_labels=[REGRESSION_LABEL], intro=intro)
    jira.link_relates(new_key, old_key)
    return f"Created regression bug {new_key} (relates to {old_key})"


def write_step_summary(lines):
    print("\n".join(lines))
    summary_file = os.environ.get("GITHUB_STEP_SUMMARY")
    if summary_file:
        with open(summary_file, "a", encoding="utf-8") as f:
            f.write("\n".join(lines) + "\n")


def point_refs(points):
    return ", ".join(f"{point['tc_id']} [{point['method']}]" for point in points)


def report(points, jira, triage_fn, run):
    known = match_known_issues(points, jira.search_by_fingerprints([point["fingerprint"] for point in points]))
    open_issues = jira.search_open_automation_bugs()
    open_issue_keys = {issue["key"] for issue in open_issues}

    triage = None
    if known["new"] or known["recurring"]:
        try:
            triage = triage_fn(known["new"], known["recurring"], open_issues)
        except (anthropic.APIError, RuntimeError, ValueError, KeyError) as error:
            print(f"Automatic triage failed, falling back to one bug per failed check: {error}")
    resolved = resolve_triage(triage, known["new"], known["recurring"], open_issue_keys)

    summary = ["## JIRA bug report", f"{len(points)} failed check(s) in {run_line(run)}", ""]
    errors = []

    def attempt(description, action):
        try:
            result = action()
            summary.append(f"- {result or description}")
        except Exception as error:
            errors.append(f"{description}: {error}")
            summary.append(f"- FAILED {description}: {error}")

    for bug in resolved["new_bugs"]:
        attempt(
            f"create bug for {point_refs(bug['points'])}",
            lambda bug=bug: f"Created {create_bug(jira, bug['report'], bug['points'], run)} "
                            f"({bug['report']['severity']}) for {point_refs(bug['points'])}",
        )

    for issue_key, attached in resolved["attachments"].items():
        def attach_to_open(issue_key=issue_key, attached=attached):
            jira.add_labels(issue_key, labels_for(attached)[len(BASE_LABELS):])
            jira.add_comment(issue_key, doc(
                paragraph(f"New failed check(s) with the same root cause found in {run_line(run)}:"),
                bullet_list(affected_items(attached)),
            ))
            attach_screenshots(jira, issue_key, attached)
            return f"Linked {point_refs(attached)} to open bug {issue_key}"
        attempt(f"link {point_refs(attached)} to {issue_key}", attach_to_open)

    for issue_key, entry in known["still_failing"].items():
        def comment_still_failing(issue_key=issue_key, entry=entry):
            jira.add_comment(issue_key, doc(
                paragraph(f"Still failing in {run_line(run)}:"),
                bullet_list(affected_items(entry["points"])),
            ))
            return f"Commented on open bug {issue_key}: still failing ({point_refs(entry['points'])})"
        attempt(f"comment on {issue_key}", comment_still_failing)

    for issue_key, recurrence in resolved["recurrences"].items():
        issue = known["recurring"][issue_key]["issue"]
        attempt(
            f"handle recurrence of {issue_key}",
            lambda issue=issue, recurrence=recurrence: handle_recurrence(jira, issue, recurrence, run)
                                                       + f" for {point_refs(recurrence['points'])}",
        )

    for issue_key, entry in known["wont_fix"].items():
        summary.append(
            f"- WARNING {point_refs(entry['points'])} still fail(s) but {issue_key} was closed as "
            f"[{resolution_name(entry['issue'])}]: update the expected result or disable the test"
        )

    for entry in resolved["not_product_bugs"]:
        summary.append(f"- Not filed (not a product bug) {point_refs(entry['points'])}: {entry['reason']}")

    write_step_summary(summary)
    return errors


def main():
    parser = argparse.ArgumentParser(description="File JIRA bugs for failed automation tests")
    parser.add_argument("--dry-run", action="store_true", help="read JIRA and triage, but do not write to JIRA")
    args = parser.parse_args()

    for name in REQUIRED_ENV_VARS:
        require_env(name)

    points = build_failure_points(parse_failures())
    if not points:
        print("No failed tests found, nothing to report.")
        return

    client = anthropic.Anthropic(api_key=os.environ["ANTHROPIC_API_KEY"])
    jira = Jira(
        os.environ["JIRA_BASE_URL"].rstrip("/"),
        (os.environ["JIRA_EMAIL"], os.environ["JIRA_API_TOKEN"]),
        os.environ["JIRA_PROJECT_KEY"],
        dry_run=args.dry_run,
    )

    def triage_fn(new_points, recurring, open_issues):
        return triage_with_claude(client, new_points, recurring, open_issues)

    errors = report(points, jira, triage_fn, build_run_context())
    if errors:
        sys.exit(f"{len(errors)} JIRA action(s) failed")


if __name__ == "__main__":
    main()
