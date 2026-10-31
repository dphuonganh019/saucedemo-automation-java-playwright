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
        ├── flow/                      # TC_FLOW_* user journey phases that hand a saved session to the next phase
        └── suites/                    # RegressionSuite (all modules), UserJourneySuite (flow) + one suite per module
```

Each `TC_*` class manages its own isolated `ThreadLocal` browser/page via `BaseTest`, so test classes have no dependency on each other and can run concurrently under `@ParallelSuite`. The only exception is `tests/flow`, which is deliberately sequential (see below).

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

Tests run headless in CI; locally, set `headless=false` in `config.properties` to watch the browser. `mvn test` runs `RegressionSuite` (every module in parallel) followed by `UserJourneySuite` (the sequential flow). To run a single suite, pass its name: `mvn test -Dtest=LoginSuite`, `mvn test -Dtest=UserJourneySuite`.

## Parallel vs sequential execution

| Annotation | Behaviour | Used by |
|---|---|---|
| `@ParallelSuite` | Test classes run concurrently; the steps inside a class stay in `@Order` on one thread | `RegressionSuite` and the module suites |
| `@FlowSuite` | Parallel disabled; test classes run one after another in class-level `@Order` | `UserJourneySuite` |

`UserJourneySuite` models a returning customer across five phases. Each phase is its own `TC_FLOW_*` class running in a **fresh browser** that restores the session (cookies + local storage) saved by the previous phase via Playwright `storageState`:

1. `TC_FLOW_01`: log in and save the session
2. `TC_FLOW_02`: reopen the site without logging in, add two products
3. `TC_FLOW_03`: restart the browser, the cart is still there
4. `TC_FLOW_04`: check out the kept cart (item total, tax, total)
5. `TC_FLOW_05`: log out, direct access to an internal page is denied

Because every phase depends on the file written by the one before it, these classes cannot run in parallel. If they do, or if a phase fails to save its session, every step of the later phases is reported as **Skipped (Blocked)** instead of failing with misleading errors. For the same reason `RegressionSuite` excludes `tests.flow`.

## Reporting

Each run generates an ExtentReports HTML report under `test-output/`, including step-by-step logs and failure screenshots. On every push/PR, GitHub Actions runs the suite headless and uploads the report as a workflow artifact — see the **Actions** tab.

## AI-assisted bug reporting

When a test fails in CI, `scripts/report_failures.py` splits the Surefire XML results into individual failed checks (one per failed expected result) and gives each a stable fingerprint. It then:

- **groups by root cause**: one Claude (Anthropic API) call triages the new failed checks, so several test cases broken by the same defect become **one** JIRA bug listing every affected test case, while one test case with two unrelated failures becomes two bugs;
- **avoids duplicates**: a check already tracked by an open bug only adds a "still failing" comment;
- **handles regressions**: a check whose bug was closed reopens it (fix not released yet, or closed recently) or files a linked `[Regression]` bug, with fresh reproduction steps; bugs closed as Won't Fix / Not a Bug are never reopened;
- **skips noise**: flaky or environment failures (timeouts, network) are listed in the run summary instead of being filed.

Bugs are written as manual QA reproduction steps, carry the failure screenshots, and get `Severity`/`Priority` from the triage.

This only runs in CI (on real, pushed failures), not on local test runs, to avoid filing bugs for work-in-progress code. See `scripts/report_failures.py` and the `File JIRA bug reports for failed tests` step in `.github/workflows/ci.yml`.
