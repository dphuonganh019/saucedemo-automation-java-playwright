# SauceDemo Test Automation Framework

![CI](https://github.com/dphuonganh019/PHUONGANH_CAPSTONE_AUTOTEST_JAVA_PLAYWRIGHT/actions/workflows/ci.yml/badge.svg)

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
│   ├── pages/          # Page Object classes (BasePage, LoginPage, ...)
│   └── utils/          # ConfigReader, BrowserManager, LogUtil
└── test/java/
    ├── config/         # @FlowSuite / @ParallelSuite execution mode annotations
    ├── report/         # ExtentReports setup (ReportManager, ExtentReportExtension)
    └── tests/
        ├── BaseTest.java              # ThreadLocal-based Playwright browser/page lifecycle
        ├── TC_LOGIN_*.java            # Individual test cases
        └── EndToEndSystemTestSuite.java  # Suite that runs the TC_LOGIN_* classes together
```

Each `TC_LOGIN_*` class manages its own isolated `ThreadLocal` browser/page via `BaseTest`, so test classes have no dependency on each other and can run concurrently under `@ParallelSuite`.

## Getting started

**Prerequisites:** JDK 21, Maven.

1. Clone the repo and install dependencies:
   ```bash
   git clone https://github.com/dphuonganh019/PHUONGANH_CAPSTONE_AUTOTEST_JAVA_PLAYWRIGHT.git
   cd PHUONGANH_CAPSTONE_AUTOTEST_JAVA_PLAYWRIGHT
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

Tests run headless in CI; locally, set `headless=false` in `config.properties` to watch the browser. Surefire is configured to discover `*Suite.java` classes only — run a suite (e.g. `EndToEndSystemTestSuite`), not individual `TC_*` classes directly.

## Reporting

Each run generates an ExtentReports HTML report under `test-output/`, including step-by-step logs and failure screenshots. On every push/PR, GitHub Actions runs the suite headless and uploads the report as a workflow artifact — see the **Actions** tab.
