package com.ecommerce.automation.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Singleton wrapper around ExtentReports. Creates a single time-stamped HTML
 * report file per test run under the configured report directory, containing
 * pass/fail/skip statistics, per-test logs and failure screenshots.
 */
public final class ExtentManager {

    private static ExtentReports extentReports;

    private ExtentManager() {
        // utility class - no instantiation
    }

    public static synchronized ExtentReports getInstance() {
        if (extentReports == null) {
            extentReports = createInstance();
        }
        return extentReports;
    }

    private static ExtentReports createInstance() {
        String reportDir = ConfigReader.get("report.dir");
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String reportPath = reportDir + "/ExtentReport_" + timestamp + ".html";

        ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportPath);
        sparkReporter.config().setTheme(Theme.STANDARD);
        sparkReporter.config().setDocumentTitle("E-Commerce Automation Test Report");
        sparkReporter.config().setReportName("Selenium + TestNG Regression Suite - automationexercise.com");

        ExtentReports extent = new ExtentReports();
        extent.attachReporter(sparkReporter);
        extent.setSystemInfo("Application Under Test", ConfigReader.baseUrl());
        extent.setSystemInfo("Browser", ConfigReader.browser());
        extent.setSystemInfo("OS", System.getProperty("os.name"));
        extent.setSystemInfo("Java Version", System.getProperty("java.version"));
        extent.setSystemInfo("Executed By", System.getProperty("user.name"));

        System.out.println("ExtentReport will be generated at: " + reportPath);
        return extent;
    }
}
