package com.ecommerce.automation.utils;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Captures a screenshot of the current browser state and saves it under the
 * configured screenshot directory. Used by the TestNG listener to attach
 * failure evidence to the ExtentReports HTML report.
 */
public final class ScreenshotUtils {

    private ScreenshotUtils() {
        // utility class - no instantiation
    }

    /**
     * Takes a screenshot and saves it as {testName_yyyyMMdd_HHmmss.png}.
     *
     * @param driver   active WebDriver instance
     * @param testName name of the failing test, used to build the file name
     * @return absolute path to the saved screenshot file, or null if capture failed
     */
    public static String captureScreenshot(WebDriver driver, String testName) {
        if (driver == null) {
            return null;
        }
        try {
            String screenshotDir = ConfigReader.get("screenshot.dir");
            Path dirPath = Paths.get(screenshotDir);
            Files.createDirectories(dirPath);

            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String fileName = testName + "_" + timestamp + ".png";
            Path destination = dirPath.resolve(fileName);

            File sourceFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(sourceFile.toPath(), destination);

            return destination.toAbsolutePath().toString();
        } catch (IOException e) {
            System.err.println("Failed to capture screenshot for test [" + testName + "]: " + e.getMessage());
            return null;
        }
    }
}
