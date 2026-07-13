# E-Commerce Selenium Automation Framework

A Selenium WebDriver + TestNG + Maven test automation framework for an
e-commerce web application, built with the **Page Object Model (POM)**
design pattern and professional **ExtentReports** HTML reporting.

**Application Under Test:** [https://automationexercise.com](https://automationexercise.com)
(a free, publicly available demo e-commerce site built specifically for
automation practice — no real payments are processed).

---

## 1. Tech Stack

| Tool                | Purpose                                             |
|---------------------|------------------------------------------------------|
| Java 17              | Language                                            |
| Maven                | Build & dependency management                       |
| Selenium WebDriver 4 | Browser automation                                   |
| TestNG               | Test framework (assertions, annotations, suites)     |
| WebDriverManager     | Automatic browser driver binary management           |
| ExtentReports 5      | HTML test execution report with screenshots          |
| Jackson Databind     | JSON test-data parsing for data-driven tests          |
| Maven Surefire       | Test execution + native Surefire XML/HTML reports    |
| Log4j2               | Framework logging                                    |

---

## 2. Project Structure

```
ecommerce-selenium-framework/
├── pom.xml                        # Maven build file (all dependencies + Surefire config)
├── testng.xml                     # Main suite: all 4 modules, all groups
├── testng-smoke.xml                # Smoke-only suite (fast subset)
├── testng-regression.xml           # Regression-only suite (full coverage)
├── README.md
│
├── src/main/java/com/ecommerce/automation/
│   ├── base/
│   │   └── DriverFactory.java      # Thread-safe WebDriver creation (Chrome/Firefox/Edge)
│   ├── pages/                      # Page Object Model classes
│   │   ├── BasePage.java           # Shared explicit-wait helper methods
│   │   ├── LoginPage.java
│   │   ├── ProductSearchPage.java
│   │   ├── CartPage.java
│   │   ├── CheckoutPage.java
│   │   └── PaymentPage.java
│   └── utils/
│       ├── ConfigReader.java       # Reads config.properties (+ -D overrides)
│       ├── ScreenshotUtils.java    # Captures screenshots on failure
│       ├── ExtentManager.java      # ExtentReports singleton/report setup
│       └── JsonDataReader.java     # Generic JSON test-data loader
│
├── src/test/java/com/ecommerce/automation/
│   ├── base/
│   │   └── BaseTest.java           # @BeforeMethod/@AfterMethod browser setup-teardown
│   ├── listeners/
│   │   └── TestListener.java       # ITestListener -> ExtentReports + failure screenshots
│   ├── dataproviders/
│   │   └── DataProviders.java      # @DataProvider methods sourced from JSON
│   └── tests/
│       ├── LoginTests.java         # 16 test cases (Login module)
│       ├── CartTests.java          # 12 test cases (Cart module)
│       ├── CheckoutTests.java      # 13 test cases (Checkout module)
│       └── PaymentTests.java       # 13 test cases (Payment module)
│
└── src/test/resources/
    ├── config.properties           # Base URL, browser, timeouts, credentials (not hardcoded)
    ├── log4j2.xml
    └── testdata/
        ├── loginData.json          # Data-driven invalid-login scenarios
        └── paymentData.json        # Data-driven invalid-card scenarios
```

**Total: 54 `@Test` executions** (including data-provider–driven rows) across
the Login, Cart, Checkout and Payment modules — see section 5 for the breakdown.

---

## 3. Design Highlights

- **Page Object Model**: every page has its own class with private `By`
  locators and public action/assertion methods. Test classes never touch a
  locator directly.
- **No `Thread.sleep()` anywhere** — every wait is an explicit
  `WebDriverWait` + `ExpectedConditions` call, centralized in `BasePage`.
- **Thread-safe `DriverFactory`** using `ThreadLocal<WebDriver>`, so the
  suite is safe to run with `parallel="classes"` in `testng.xml`.
- **Externalized configuration** — base URL, browser, timeouts and test
  credentials all live in `config.properties`, overridable per run with
  `-Dkey=value` (nothing is hardcoded in Java code).
- **Data-driven testing** via TestNG `@DataProvider`, fed from JSON files
  under `src/test/resources/testdata`, keeping test data separate from
  test logic.
- **TestNG groups** (`smoke`, `sanity`, `regression`) let you run a fast
  subset or the full suite on demand.
- **Automatic screenshot-on-failure** attached directly into the
  ExtentReports HTML report via a global `ITestListener`.

---

## 4. Prerequisites

- Java 17+ (`java -version`)
- Maven 3.8+ (`mvn -version`)
- Google Chrome installed (default browser; Firefox/Edge also supported)
- Internet access (WebDriverManager downloads the matching driver binary
  automatically on first run — no manual driver setup needed)

---

## 5. How to Run the Tests

### Run everything (default `testng.xml`, all groups)
```bash
mvn test
```

### Run only the smoke suite
```bash
mvn test -Dgroups=smoke
# or, using the dedicated suite file:
mvn test -DsuiteXmlFile=testng-smoke.xml
```

### Run only the regression suite
```bash
mvn test -Dgroups=regression
# or:
mvn test -DsuiteXmlFile=testng-regression.xml
```

### Run only the sanity checks
```bash
mvn test -Dgroups=sanity
```

### Run on a different browser
```bash
mvn test -Dbrowser=firefox
mvn test -Dbrowser=edge
```

### Run headless (e.g. for CI pipelines)
```bash
mvn test -Dheadless=true
```

### Run a single test class
```bash
mvn test -Dtest=LoginTests
```

### Test count per module

| Module    | Test class          | Test cases (incl. data-driven rows) |
|-----------|----------------------|:------------------------------------:|
| Login     | `LoginTests.java`     | 16                                    |
| Cart      | `CartTests.java`      | 12                                    |
| Checkout  | `CheckoutTests.java`  | 13                                    |
| Payment   | `PaymentTests.java`   | 13                                    |
| **Total** |                        | **54**                                |

---

## 6. Viewing the Reports

After a run finishes, two reports are generated under `test-output/`:

1. **ExtentReports HTML report** (primary, richest report):
   ```
   test-output/ExtentReport/ExtentReport_<timestamp>.html
   ```
   Open this file directly in any browser. It includes:
   - Overall pass/fail/skip dashboard and pie chart
   - Per-test logs, duration and TestNG group tags
   - Embedded screenshots automatically attached to every failed test

2. **Failure screenshots** (also embedded in the ExtentReport above):
   ```
   test-output/screenshots/<testName>_<timestamp>.png
   ```

3. **Maven Surefire reports** (native, CI-friendly XML/TXT summary):
   ```
   target/surefire-reports/
   ```

---

## 7. Notes & Assumptions

- `automationexercise.com` is a public training site — no real card is
  charged and no real order is fulfilled. Card numbers used in test data
  (`4111 1111 1111 1111`, etc.) are standard, publicly documented dummy
  test card numbers.
- The site does not expose an "account-locked" state, so the "locked
  user" scenario from the original requirements is covered instead by
  `testMultipleFailedAttemptsThenValidLoginSucceeds`, which verifies the
  application does **not** incorrectly lock out a user after repeated
  failed attempts.
- Checkout on this application requires an authenticated user (guest
  checkout is intentionally blocked by the site with a Register/Login
  modal) — this behavior itself is asserted by
  `testCheckoutRequiresLogin` / `testProceedToCheckoutAsGuestShowsLoginPrompt`.
- Before your first run, replace the demo values for `valid.user.email`
  and `valid.user.password` in `config.properties` with a real account
  you've registered on automationexercise.com (Signup is free and instant).

---

## 8. Author

Built by **Bhoopathy P (King)** as a QA Automation portfolio project,
demonstrating Selenium WebDriver, TestNG, Maven, Page Object Model,
data-driven testing and HTML reporting for Software Test Engineer roles.
