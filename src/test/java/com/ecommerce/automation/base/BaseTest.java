package com.ecommerce.automation.base;

import com.ecommerce.automation.pages.CartPage;
import com.ecommerce.automation.pages.LoginPage;
import com.ecommerce.automation.pages.ProductSearchPage;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/**
 * Parent class for every TestNG test class in the framework.
 * <p>
 * Responsibilities:
 * <ul>
 *   <li>Spins up a fresh, isolated WebDriver instance before each test method</li>
 *   <li>Quits the WebDriver after each test method, regardless of outcome</li>
 *   <li>Exposes ready-to-use Page Object instances to subclasses</li>
 * </ul>
 * Screenshot capture and ExtentReports logging on failure are handled centrally
 * by {@link com.ecommerce.automation.listeners.TestListener}, keeping this
 * class focused purely on browser lifecycle management.
 */
public abstract class BaseTest {

    protected WebDriver driver;
    protected LoginPage loginPage;
    protected ProductSearchPage productSearchPage;
    protected CartPage cartPage;

    @BeforeMethod(alwaysRun = true)
    public void setUp() {
        DriverFactory.initDriver();
        driver = DriverFactory.getDriver();
        loginPage = new LoginPage(driver);
        productSearchPage = new ProductSearchPage(driver);
        cartPage = new CartPage(driver);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        DriverFactory.quitDriver();
    }

    /**
     * Helper used by tests that need to start each scenario already logged in.
     * Kept in the base class since Login, Cart, Checkout and Payment tests all
     * rely on it.
     */
    protected void loginWithValidUser() {
        loginPage.open();
        loginPage.login(
                com.ecommerce.automation.utils.ConfigReader.get("valid.user.email"),
                com.ecommerce.automation.utils.ConfigReader.get("valid.user.password")
        );
    }
}
