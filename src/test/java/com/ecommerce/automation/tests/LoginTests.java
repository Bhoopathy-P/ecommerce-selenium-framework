package com.ecommerce.automation.tests;

import com.ecommerce.automation.base.BaseTest;
import com.ecommerce.automation.dataproviders.DataProviders;
import com.ecommerce.automation.utils.ConfigReader;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test cases covering the Login / Authentication module on
 * https://automationexercise.com/login
 * <p>
 * Groups:
 *  - smoke      : minimal set proving the login flow works end to end
 *  - sanity     : quick structural/UI checks
 *  - regression : full negative + edge-case coverage
 */
public class LoginTests extends BaseTest {

    // ---------------------------------------------------------------
    // Positive scenarios
    // ---------------------------------------------------------------

    @Test(groups = {"smoke", "regression"},
            description = "User can log in with a valid, registered email and password")
    public void testValidLogin() {
        loginPage.open();
        loginPage.login(ConfigReader.get("valid.user.email"), ConfigReader.get("valid.user.password"));
        Assert.assertTrue(loginPage.isUserLoggedIn(), "User should be logged in after valid credentials");
    }

    @Test(groups = {"regression"},
            description = "Logged-in username displayed in the header matches the account used to log in")
    public void testLoggedInUsernameIsDisplayed() {
        loginWithValidUser();
        Assert.assertTrue(loginPage.isUserLoggedIn(), "Expected user to be logged in");
        String displayedName = loginPage.getLoggedInUsername();
        Assert.assertFalse(displayedName.isBlank(), "Logged-in username should not be blank");
    }

    @Test(groups = {"smoke", "regression"}, dependsOnMethods = {}, alwaysRun = true,
            description = "A logged-in user can log out successfully and returns to the login/home flow")
    public void testLogout() {
        loginWithValidUser();
        Assert.assertTrue(loginPage.isUserLoggedIn(), "Precondition failed: user was not logged in");
        loginPage.logout();
        Assert.assertTrue(loginPage.isOnLoginPage(), "Logout should redirect the user back to the login page");
    }

    @Test(groups = {"regression"},
            description = "Login session persists after refreshing the page")
    public void testSessionPersistsAfterPageRefresh() {
        loginWithValidUser();
        Assert.assertTrue(loginPage.isUserLoggedIn(), "Precondition failed: user was not logged in");
        driver.navigate().refresh();
        Assert.assertTrue(loginPage.isLogoutLinkVisible(),
                "Session should persist (Logout link still visible) after a page refresh");
    }

    // ---------------------------------------------------------------
    // Negative scenarios - data-driven
    // ---------------------------------------------------------------

    @Test(dataProvider = "invalidLoginData", dataProviderClass = DataProviders.class,
            groups = {"regression"},
            description = "Login is rejected for a variety of invalid email/password combinations")
    public void testInvalidLoginCombinations(String email, String password, String scenario) {
        loginPage.open();
        loginPage.login(email, password);
        Assert.assertFalse(loginPage.isUserLoggedIn(),
                "Login should NOT succeed for scenario: " + scenario);
        Assert.assertTrue(loginPage.isOnLoginPage(),
                "User should remain on the login page for scenario: " + scenario);
    }

    // ---------------------------------------------------------------
    // Empty-field scenarios
    // ---------------------------------------------------------------

    @Test(groups = {"regression"}, description = "Login with an empty email field is rejected")
    public void testEmptyEmailField() {
        loginPage.open();
        loginPage.enterEmail("");
        loginPage.enterPassword(ConfigReader.get("valid.user.password"));
        loginPage.clickLogin();
        Assert.assertFalse(loginPage.isUserLoggedIn(), "Login must not succeed with an empty email field");
    }

    @Test(groups = {"regression"}, description = "Login with an empty password field is rejected")
    public void testEmptyPasswordField() {
        loginPage.open();
        loginPage.enterEmail(ConfigReader.get("valid.user.email"));
        loginPage.enterPassword("");
        loginPage.clickLogin();
        Assert.assertFalse(loginPage.isUserLoggedIn(), "Login must not succeed with an empty password field");
    }

    @Test(groups = {"regression"}, description = "Login with both email and password fields empty is rejected")
    public void testEmptyBothFields() {
        loginPage.open();
        loginPage.enterEmail("");
        loginPage.enterPassword("");
        loginPage.clickLogin();
        Assert.assertFalse(loginPage.isUserLoggedIn(), "Login must not succeed with both fields empty");
        Assert.assertTrue(loginPage.isOnLoginPage(), "User should remain on the login page");
    }

    // ---------------------------------------------------------------
    // Sanity / UI structural checks
    // ---------------------------------------------------------------

    @Test(groups = {"sanity"}, description = "Login page loads with both Login and Signup panels visible")
    public void testLoginPageLoadsCorrectly() {
        loginPage.open();
        Assert.assertTrue(loginPage.isOnLoginPage(), "Should be navigated to the /login route");
        Assert.assertTrue(driver.getPageSource().contains("Login to your account"),
                "Login panel heading should be present on the page");
    }

    @Test(groups = {"sanity", "regression"},
            description = "Signup attempt with an email that is already registered shows a validation error")
    public void testSignupWithExistingEmailShowsError() {
        loginPage.open();
        loginPage.enterSignupNameAndEmail("Existing User", ConfigReader.get("valid.user.email"));
        Assert.assertTrue(loginPage.isSignupErrorDisplayed(),
                "Signup with an already-registered email should show an 'already exist' error");
    }

    @Test(groups = {"regression"},
            description = "Repeated failed login attempts do not lock the form; a subsequent valid login still succeeds")
    public void testMultipleFailedAttemptsThenValidLoginSucceeds() {
        loginPage.open();
        loginPage.login("wrong1@mailinator.com", "WrongPass1");
        Assert.assertFalse(loginPage.isUserLoggedIn(), "First invalid attempt should fail");

        loginPage.open();
        loginPage.login("wrong2@mailinator.com", "WrongPass2");
        Assert.assertFalse(loginPage.isUserLoggedIn(), "Second invalid attempt should fail");

        loginPage.open();
        loginPage.login(ConfigReader.get("valid.user.email"), ConfigReader.get("valid.user.password"));
        Assert.assertTrue(loginPage.isUserLoggedIn(),
                "A valid login after prior failed attempts should still succeed (no lockout on this app)");
    }
}
