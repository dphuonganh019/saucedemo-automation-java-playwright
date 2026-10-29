# SauceDemo Test Automation Framework

![CI](https://github.com/dphuonganh019/saucedemo-automation-java-playwright/actions/workflows/ci.yml/badge.svg)

A UI test automation framework built from scratch for [saucedemo.com](https://www.saucedemo.com/), covering the login flow with a Page Object Model design, parallel/sequential test execution, and automated HTML reporting. Built as a capstone project to practice designing an automation framework end-to-end rather than just writing individual test scripts.

## Tech stack

| Purpose            | Tool                          |
|---------------------|--------------------------------|
| Language             | Java 21                        |
| Browser automation   | Playwright                     |
| Test runner          | JUnit 5 (Jupiter + Platform Suite) |
| Reporting            | ExtentReports                  |
| Build tool           | Maven                          |
| CI                    | GitHub Actions                 |

## Project structure

```
src/
├── main/java/
│   ├── pages/          # Page Objects (BasePage, LoginPage, InventoryPage, CartPage, Checkout*Page, ...) + HeaderComponent / SideMenuComponent
│   └── utils/          # ConfigReader, BrowserManager, LogUtil
└── test/java/
    ├── config/         # @FlowSuite / @ParallelSuite execution mode annotations
    ├── report/         # ExtentReports setup (ReportManager, ExtentReportExtension)
    └── tests/
        ├── BaseTest.java              # ThreadLocal-based Playwright browser/page lifecycle
        ├── login/ menu/ inventory/ productdetail/ cart/ checkout/ e2e/
        │                              # TC_* test cases per module (one class = one manual testcase)
        └── suites/                    # RegressionSuite (all modules) + one suite per module (LoginSuite, CartSuite, E2ESuite, ...)
```

Each `TC_*` class manages its own isolated `ThreadLocal` browser/page via `BaseTest`, so test classes have no dependency on each other and can run concurrently under `@ParallelSuite`.

## Getting started

**Prerequisites:** JDK 21, Maven.

1. Clone the repo and install dependencies:
   ```bash
   git clone https://github.com/dphuonganh019/saucedemo-automation-java-playwright.git
   cd saucedemo-automation-java-playwright
   mvn install
   ```
2. Install the Playwright browsers:
   ```bash
   mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install --with-deps chromium"
   ```
3. Copy the config template and fill in real test data:
   ```bash
   cp src/test/resources/config.properties.example src/test/resources/config.properties
   ```
   `config.properties` is git-ignored — never commit it with real values.

## Running tests

```bash
mvn test
```

Tests run headless in CI; locally, set `headless=false` in `config.properties` to watch the browser. `mvn test` runs `RegressionSuite` (every `TC_*` class under `tests`). To run a single module, pass its suite: `mvn test -Dtest=LoginSuite`.

## Reporting

Each run generates an ExtentReports HTML report under `test-output/`, including step-by-step logs and failure screenshots. On every push/PR, GitHub Actions runs the suite headless and uploads the report as a workflow artifact — see the **Actions** tab.

## AI-assisted bug reporting

When a test fails in CI, `scripts/report_failures.py` parses the Surefire XML results and sends each failure (test class/method, error message, stack trace) to **Claude (Anthropic API)**. Claude drafts a structured bug report — written as manual QA reproduction steps rather than a description of the automated script — and the script files it straight to JIRA via the REST API, with the matching failure screenshot attached and `Severity`/`Priority` set from Claude's assessment.

This only runs in CI (on real, pushed failures), not on local test runs, to avoid filing bugs for work-in-progress code. See `scripts/report_failures.py` and the `File JIRA bug reports for failed tests` step in `.github/workflows/ci.yml`.
