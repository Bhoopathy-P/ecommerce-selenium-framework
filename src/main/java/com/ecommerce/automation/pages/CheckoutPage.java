package com.ecommerce.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Page Object for https://automationexercise.com/checkout
 * Covers the delivery/billing address panel, the order review table and the
 * "Place Order" action that transitions into the payment step.
 */
public class CheckoutPage extends BasePage {

    // ---------- Locators ----------
    private final By deliveryAddressBlock = By.id("address_delivery");
    private final By billingAddressBlock = By.id("address_invoice");
    private final By orderReviewTable = By.cssSelector("table#cart_info, #cart_info table, table#cart_info_table, #cart_info_table");
    private final By orderReviewRows = By.cssSelector("#cart_info tbody tr, #cart_info_table tbody tr");
    private final By orderTotalAmount = By.xpath("(//tr[contains(@class,'total') or contains(.,'Total Amount')]//td)[last()]");
    private final By commentTextArea = By.name("message");
    private final By placeOrderButton = By.xpath("//a[text()='Place Order']");

    public CheckoutPage(WebDriver driver) {
        super(driver);
    }

    // ---------- Address panel ----------
    public boolean isDeliveryAddressDisplayed() {
        return isDisplayed(deliveryAddressBlock);
    }

    public boolean isBillingAddressDisplayed() {
        return isDisplayed(billingAddressBlock);
    }

    public String getDeliveryAddressText() {
        return getText(deliveryAddressBlock);
    }

    // ---------- Order review ----------
    public boolean isOrderReviewTableDisplayed() {
        return isDisplayed(orderReviewTable);
    }

    public int getOrderReviewItemCount() {
        return driver.findElements(orderReviewRows).size();
    }

    public List<WebElement> getOrderReviewRows() {
        return driver.findElements(orderReviewRows);
    }

    public String getOrderTotalAmountText() {
        return getText(orderTotalAmount);
    }

    // ---------- Comment ----------
    public CheckoutPage enterOrderComment(String comment) {
        type(commentTextArea, comment);
        return this;
    }

    // ---------- Navigation ----------
    public PaymentPage placeOrder() {
        click(placeOrderButton);
        waitForUrlContains("/payment");
        return new PaymentPage(driver);
    }

    public boolean isPlaceOrderButtonDisplayed() {
        return isDisplayed(placeOrderButton);
    }
}
