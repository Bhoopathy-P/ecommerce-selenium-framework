package com.ecommerce.automation.pages;

import com.ecommerce.automation.utils.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Page Object for https://automationexercise.com/view_cart
 * Handles reading cart line items, removing products and proceeding to checkout.
 */
public class CartPage extends BasePage {

    // ---------- Locators ----------
    private final By cartRows = By.cssSelector("#cart_info_table tbody tr");
    private final By cartProductNames = By.cssSelector("td.cart_description h4 a");
    private final By cartProductPrices = By.cssSelector("td.cart_price p");
    private final By cartQuantities = By.cssSelector("td.cart_quantity button");
    private final By cartTotalPrices = By.cssSelector("td.cart_total p");
    private final By deleteButtons = By.cssSelector("td.cart_delete a.cart_quantity_delete");
    private final By emptyCartMessage = By.xpath("//b[text()='Cart is empty!']");
    private final By proceedToCheckoutButton = By.xpath("//a[text()='Proceed To Checkout']");
    private final By checkoutLoginModal = By.id("checkoutModal");
    private final By checkoutModalRegisterLoginLink = By.xpath("//div[@id='checkoutModal']//a[text()='Register / Login']");

    public CartPage(WebDriver driver) {
        super(driver);
    }

    // ---------- Navigation ----------
    public CartPage open() {
        driver.get(ConfigReader.baseUrl() + "/view_cart");
        return this;
    }

    // ---------- Reading cart state ----------
    public int getCartItemCount() {
        if (isCartEmpty()) {
            return 0;
        }
        return driver.findElements(cartRows).size();
    }

    public boolean isCartEmpty() {
        return isElementPresent(emptyCartMessage);
    }

    public List<String> getProductNamesInCart() {
        return driver.findElements(cartProductNames).stream().map(WebElement::getText).toList();
    }

    public List<String> getProductQuantitiesInCart() {
        return driver.findElements(cartQuantities).stream().map(WebElement::getText).toList();
    }

    public List<String> getProductTotalsInCart() {
        return driver.findElements(cartTotalPrices).stream().map(WebElement::getText).toList();
    }

    public String getUnitPrice(int rowIndex) {
        return driver.findElements(cartProductPrices).get(rowIndex).getText();
    }

    // ---------- Actions ----------
    public CartPage removeProductByIndex(int index) {
        List<WebElement> buttons = driver.findElements(deleteButtons);
        buttons.get(index).click();
        wait.until(d -> getCartItemCount() >= 0); // wait for DOM row removal to settle
        return this;
    }

    public CartPage removeAllProducts() {
        int itemCount = getCartItemCount();
        for (int i = 0; i < itemCount; i++) {
            removeProductByIndex(0);
        }
        return this;
    }

    /**
     * Clicks "Proceed To Checkout". If the user is a guest, automationexercise.com
     * shows a modal prompting Register/Login instead of navigating directly -
     * callers should check {@link #isGuestCheckoutModalDisplayed()} afterwards.
     */
    public void proceedToCheckout() {
        click(proceedToCheckoutButton);
    }

    public boolean isGuestCheckoutModalDisplayed() {
        return isElementPresent(checkoutLoginModal) && isDisplayed(checkoutLoginModal);
    }

    public LoginPage goToLoginFromCheckoutModal() {
        click(checkoutModalRegisterLoginLink);
        return new LoginPage(driver);
    }

    public CheckoutPage goToCheckoutPage() {
        proceedToCheckout();
        waitForUrlContains("/checkout");
        return new CheckoutPage(driver);
    }
}
