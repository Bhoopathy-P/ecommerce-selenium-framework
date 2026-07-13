package com.ecommerce.automation.pages;

import com.ecommerce.automation.utils.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object for https://automationexercise.com/login
 * Encapsulates the "New User Signup!" and "Login to your account" panels.
 */
public class LoginPage extends BasePage {

    // ---------- Locators ----------
    private final By loginEmailInput = By.cssSelector("input[data-qa='login-email']");
    private final By loginPasswordInput = By.cssSelector("input[data-qa='login-password']");
    private final By loginButton = By.cssSelector("button[data-qa='login-button']");
    private final By loginErrorMessage = By.xpath("//form[contains(@action,'login')]//p[contains(text(),'incorrect')]");

    private final By signupNameInput = By.cssSelector("input[data-qa='signup-name']");
    private final By signupEmailInput = By.cssSelector("input[data-qa='signup-email']");
    private final By signupButton = By.cssSelector("button[data-qa='signup-button']");
    private final By signupErrorMessage = By.xpath("//form[contains(@action,'signup')]//p[contains(text(),'already exist')]");

    private final By loggedInAsIndicator = By.xpath("//a[contains(text(),'Logged in as')]");
    private final By logoutLink = By.linkText("Logout");
    private final By deleteAccountLink = By.linkText("Delete Account");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    // ---------- Navigation ----------
    public LoginPage open() {
        driver.get(ConfigReader.baseUrl() + "/login");
        waitForVisible(loginEmailInput);
        return this;
    }

    // ---------- Actions ----------
    public LoginPage enterEmail(String email) {
        type(loginEmailInput, email);
        return this;
    }

    public LoginPage enterPassword(String password) {
        type(loginPasswordInput, password);
        return this;
    }

    public void clickLogin() {
        click(loginButton);
    }

    /**
     * Convenience method performing a full login flow in one call.
     */
    public void login(String email, String password) {
        enterEmail(email);
        enterPassword(password);
        clickLogin();
    }

    public void enterSignupNameAndEmail(String name, String email) {
        type(signupNameInput, name);
        type(signupEmailInput, email);
        click(signupButton);
    }

    public void logout() {
        click(logoutLink);
    }

    // ---------- Assertions / State ----------
    public boolean isLoginErrorDisplayed() {
        return isElementPresent(loginErrorMessage) && isDisplayed(loginErrorMessage);
    }

    public String getLoginErrorText() {
        return getText(loginErrorMessage);
    }

    public boolean isSignupErrorDisplayed() {
        return isElementPresent(signupErrorMessage) && isDisplayed(signupErrorMessage);
    }

    public boolean isUserLoggedIn() {
        return isElementPresent(loggedInAsIndicator);
    }

    public String getLoggedInUsername() {
        String fullText = getText(loggedInAsIndicator); // "Logged in as <username>"
        return fullText.replace("Logged in as", "").trim();
    }

    public boolean isLogoutLinkVisible() {
        return isElementPresent(logoutLink);
    }

    public boolean isOnLoginPage() {
        return driver.getCurrentUrl().contains("/login");
    }
}
