package com.ecommerce.automation.listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.ecommerce.automation.base.DriverFactory;
import com.ecommerce.automation.utils.ExtentManager;
import com.ecommerce.automation.utils.ScreenshotUtils;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.util.Arrays;

/**
 * TestNG listener wired via testng.xml ({@code <listeners>}) that bridges
 * TestNG execution events to ExtentReports, and attaches a screenshot to the
 * report whenever a test fails.
 * <p>
 * Registered globally so individual test classes do not need any reporting
 * boilerplate - they simply extend {@link com.ecommerce.automation.base.BaseTest}.
 */
public class TestListener implements ITestListener {

    private static final ThreadLocal<ExtentTest> extentTestThreadLocal = new ThreadLocal<>();
    private ExtentReports extent;

    @Override
    public void onStart(ITestContext context) {
        extent = ExtentManager.getInstance();
    }

    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        String description = result.getMethod().getDescription() != null
                ? result.getMethod().getDescription() : testName;

        ExtentTest extentTest = extent.createTest(testName, description);
        String[] groups = result.getMethod().getGroups();
        if (groups != null && groups.length > 0) {
            extentTest.assignCategory(groups);
        }
        extentTestThreadLocal.set(extentTest);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        extentTestThreadLocal.get().log(Status.PASS, "Test passed in "
                + (result.getEndMillis() - result.getStartMillis()) + " ms");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentTest extentTest = extentTestThreadLocal.get();
        extentTest.log(Status.FAIL, "Test failed: " + result.getThrowable());

        WebDriver driver = DriverFactory.getDriver();
        String screenshotPath = ScreenshotUtils.captureScreenshot(driver, result.getMethod().getMethodName());
        if (screenshotPath != null) {
            try {
                extentTest.fail("Screenshot on failure:",
                        com.aventstack.extentreports.MediaEntityBuilder
                                .createScreenCaptureFromPath(screenshotPath).build());
            } catch (Exception e) {
                extentTest.log(Status.WARNING, "Could not attach screenshot: " + e.getMessage());
            }
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        extentTestThreadLocal.get().log(Status.SKIP, "Test skipped: " + result.getThrowable());
    }

    @Override
    public void onFinish(ITestContext context) {
        extent.flush();
        extentTestThreadLocal.remove();
    }
}
