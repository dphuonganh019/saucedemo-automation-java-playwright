# SauceDemo Test Automation Framework

![CI](https://github.com/dphuonganh019/saucedemo-automation-java-playwright/actions/workflows/ci.yml/badge.svg)

A UI test automation framework built from scratch for [saucedemo.com](https://www.saucedemo.com/). It covers every module of the site (login, menu, inventory, product detail, cart, checkout, end-to-end flows) with a Page Object Model design, parallel and sequential execution modes, step-by-step HTML reporting, and AI-assisted JIRA bug filing in CI. Built as a capstone project to practice designing an automation framework end-to-end rather than just writing individual test scripts.

**Demo video:** [MenuSuite run, ExtentReports report and AI bug reporting dry run](https://youtu.be/-Bu4YNOqy80) (2.5 minutes, Vietnamese captions).

## Tech stack

| Purpose              | Tool                               |
|----------------------|------------------------------------|
| Language             | Java 21                            |
| Browser automation   | Playwright                         |
| Test runner          | JUnit 5 (Jupiter + Platform Suite) |
| Reporting            | ExtentReports                      |
| Build tool           | Maven                              |
| CI                   | GitHub Actions                     |
| Bug reporting        | Python, Anthropic API, JIRA REST API |

## Test coverage

58 manual test cases are automated, one Java class per test case. Each step of the manual test case is one ordered `@Test` method, so the report reads like the original test case.

The manual test cases are in the [SauceDemo_Manual_TestCases](https://docs.google.com/spreadsheets/d/1hVC1AL-kN1Oc-Vr0-2_GzGlgoYPDtk0xGzFOA-pcxRc/edit) sheet: one row per step with its expected result, the latest status and a link to the automated class.

| Module         | Package               | Test cases               | Suite                |
|----------------|-----------------------|--------------------------|----------------------|
| Login          | `tests.login`         | `TC_LOGIN_01` - `TC_LOGIN_06` | `LoginSuite`         |
| Menu           | `tests.menu`          | `TC_MENU_01` - `TC_MENU_06`   | `MenuSuite`          |
| Inventory      | `tests.inventory`     | `TC_INV_01` - `TC_INV_12`     | `InventorySuite`     |
| Product detail | `tests.productdetail` | `TC_PDP_01` - `TC_PDP_05`     | `ProductDetailSuite` |
| Cart           | `tests.cart`          | `TC_CART_01` - `TC_CART_07`   | `CartSuite`          |
| Checkout       | `tests.checkout`      | `TC_CO_01` - `TC_CO_10`       | `CheckoutSuite`      |
| End-to-end     | `tests.e2e`           | `TC_E2E_01` - `TC_E2E_07`     | `E2ESuite`           |
| User journey   | `tests.flow`          | `TC_FLOW_01` - `TC_FLOW_05`   | `UserJourneySuite`   |

`TC_LOGIN_03` is data-driven: it runs once per row of `src/test/resources/testdata/login_accounts.csv`.

## Project structure

```
.
├── .github/workflows/ci.yml           # Run the tests headless on push/PR, file JIRA bugs on failures
├── scripts/
│   ├── report_failures.py             # AI-assisted JIRA bug reporter (see below)
│   ├── requirements.txt
│   └── .env.example                   # Template for running the reporter locally
├── pom.xml
└── src/
    ├── main/java/
    │   ├── pages/                     # Page Objects
    │   │   ├── BasePage.java          # Shared waits and element helpers
    │   │   ├── LoginPage, InventoryPage, ProductDetailPage, CartPage
    │   │   ├── CheckoutInformationPage, CheckoutOverviewPage, CheckoutCompletePage
    │   │   └── HeaderComponent, SideMenuComponent   # Components shared by every page
    │   └── utils/
    │       ├── ConfigReader.java      # config.properties with -D system property overrides
    │       ├── BrowserManager.java    # Current tab helpers (URL waits)
    │       └── LogUtil.java           # Step logs and screenshots into the report
    └── test/
        ├── java/
        │   ├── config/                # @ParallelSuite / @FlowSuite execution mode annotations
        │   ├── report/                # ExtentReports setup (ReportManager, ExtentReportExtension)
        │   └── tests/
        │       ├── BaseTest.java      # ThreadLocal Playwright lifecycle, session save/restore
        │       ├── login/ menu/ inventory/ productdetail/ cart/ checkout/ e2e/
        │       │                      # TC_* test cases, one class = one manual test case
        │       ├── flow/              # FlowBaseTest + TC_FLOW_* user journey phases
        │       └── suites/            # RegressionSuite, UserJourneySuite, KnownBugSuite and one suite per module
        └── resources/
            ├── config.properties.example
            └── testdata/login_accounts.csv
```

Each `TC_*` class gets its own isolated `ThreadLocal` browser and page from `BaseTest`, so test classes do not depend on each other and can run concurrently under `@ParallelSuite`. The only exception is `tests/flow`, which is deliberately sequential (see below).

## Getting started

**Prerequisites:** JDK 21, Maven.

1. Clone the repo and install dependencies (tests are skipped here because they need the config file from step 3):
   ```bash
   git clone https://github.com/dphuonganh019/saucedemo-automation-java-playwright.git
   cd saucedemo-automation-java-playwright
   mvn install -DskipTests
   ```
2. Install the Playwright browsers:
   ```bash
   mvn exec:java -e -D exec.mainClass=com.microsoft.playwright.CLI -D exec.args="install --with-deps chromium"
   ```
3. Copy the config template and fill in real test data:
   ```bash
   cp src/test/resources/config.properties.example src/test/resources/config.properties
   ```
   `config.properties` is git-ignored, never commit it with real values.

## Running tests

```bash
mvn test
```

`mvn test` runs `RegressionSuite` (every module in parallel), `UserJourneySuite` (the sequential flow) and `KnownBugSuite` (test cases that catch a known defect, see [Known issue](#known-issue)). It ends with **BUILD FAILURE** because of the 2 known-bug tests, not because of the framework. For a green run, which is what CI checks:

```bash
mvn test -Dtest=RegressionSuite,UserJourneySuite
```

To run a single suite, pass its name:

```bash
mvn test -Dtest=LoginSuite
mvn test -Dtest=UserJourneySuite
```

Any key in `config.properties` can be overridden from the command line, for example `mvn test -Dheadless=true`. Tests run headless in CI; locally, set `headless=false` to watch the browser.

## Parallel vs sequential execution

| Annotation       | Behaviour                                                                             | Used by                                 |
|------------------|---------------------------------------------------------------------------------------|-----------------------------------------|
| `@ParallelSuite` | Test classes run concurrently; the steps inside a class stay in `@Order` on one thread | `RegressionSuite`, `KnownBugSuite` and the module suites |
| `@FlowSuite`     | Parallel disabled; test classes run one after another in class-level `@Order`          | `UserJourneySuite`                      |

`UserJourneySuite` models a returning customer across five phases. Each phase is its own `TC_FLOW_*` class running in a **fresh browser** that restores the session (cookies + local storage) saved by the previous phase through Playwright `storageState`:

1. `TC_FLOW_01`: log in and save the session
2. `TC_FLOW_02`: reopen the site without logging in, add two products
3. `TC_FLOW_03`: restart the browser, the cart is still there
4. `TC_FLOW_04`: check out the kept cart (item total, tax, total)
5. `TC_FLOW_05`: log out, direct access to an internal page is denied

Because every phase depends on the file written by the one before it, these classes cannot run in parallel. If they do, or if a phase fails to save its session, every step of the later phases is reported as **Skipped (Blocked)** instead of failing with misleading errors. For the same reason `RegressionSuite` excludes `tests.flow`.

## Reporting

Each run generates an ExtentReports HTML report under `test-output/`: one node per test case, one child node per step, with the actions logged by the Page Objects and a screenshot on every failed step. On every push and pull request, GitHub Actions runs the tests headless and uploads the report as a workflow artifact, see the **Actions** tab.

## Known issue

`TC_MENU_05` and `TC_E2E_05` fail because they catch a real defect of saucedemo.com, tracked in JIRA as SDM-10. The tests themselves are correct. After **Reset App State**, the cart badge is cleared but the product buttons keep showing **Remove** instead of **Add to cart**. The tests keep the correct expected result so the defect stays visible.

Both classes are tagged `@Tag("known-bug")` and quarantined:

- `RegressionSuite` excludes the tag, so a defect that is already tracked does not turn every CI run red and does not hide new failures.
- `KnownBugSuite` runs only the tagged classes. In CI it runs after the JIRA step and is allowed to fail, so the known defect still has a report on every run but never gets a new JIRA comment on every push.
- If every known-bug test passes, CI shows a **Known bug may be fixed** warning: check the JIRA bug and remove the tag so the test case goes back to `RegressionSuite`.

## AI-assisted bug reporting

When a test fails in CI, `scripts/report_failures.py` splits the Surefire XML results into individual failed checks (one per failed expected result) and gives each a stable fingerprint. It then:

- **groups by root cause**: one Claude (Anthropic API) call triages the new failed checks, so several test cases broken by the same defect become **one** JIRA bug listing every affected test case, while one test case with two unrelated failures becomes two bugs;
- **avoids duplicates**: a check already tracked by an open bug only adds a "still failing" comment;
- **handles regressions**: a check whose bug was closed reopens it (fix not released yet, or closed recently) or files a linked `[Regression]` bug with fresh reproduction steps; bugs closed as Won't Fix / Not a Bug are never reopened;
- **skips noise**: flaky or environment failures (timeouts, network) are listed in the run summary instead of being filed.

Bugs are written as manual QA reproduction steps, carry the failure screenshots, and get `Severity` / `Priority` from the triage. Each bug is labelled with the affected test case IDs, so they can be filtered in JIRA.

This only runs in CI on pushes to `master`, not on pull requests or local test runs, to avoid filing bugs for work-in-progress code. Run `python scripts/report_failures.py --dry-run` to preview the triage without writing to JIRA.

### CI configuration

| Name                     | Type     | Purpose                                                              |
|--------------------------|----------|----------------------------------------------------------------------|
| `ANTHROPIC_API_KEY`      | Secret   | Anthropic API key used for the triage                                |
| `JIRA_BASE_URL`, `JIRA_EMAIL`, `JIRA_API_TOKEN`, `JIRA_PROJECT_KEY` | Secret | JIRA connection                      |
| `JIRA_REOPEN_TRANSITION` | Variable | Optional: name of the workflow transition used to reopen a bug       |
| `REOPEN_WINDOW_DAYS`     | Variable | Optional: reopen a bug closed within this many days (default 14), otherwise file a regression bug |
| `APP_VERSION`            | Variable | Optional: application version written into the bug's environment section |

The test data secrets (`BASE_URL`, `USERNAME_*`, `SAUCE_PASSWORD`) are used to generate `config.properties` in CI.
