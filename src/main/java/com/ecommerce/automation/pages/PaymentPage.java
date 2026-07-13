package com.ecommerce.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Page Object for https://automationexercise.com/payment and the resulting
 * order confirmation page (/payment_done/{orderId}).
 * <p>
 * NOTE: automationexercise.com is a public demo/training site - no real
 * payment gateway is invoked. Card fields accept any well-formed dummy data,
 * which makes it ideal for exercising both "happy path" and field-validation
 * automation scenarios without any real financial risk.
 */
public class PaymentPage extends BasePage {

    // ---------- Locators ----------
    private final By nameOnCardInput = By.name("name_on_card");
    private final By cardNumberInput = By.name("card_number");
    private final By cvcInput = By.name("cvc");
    private final By expiryMonthInput = By.name("expiry_month");
    private final By expiryYearInput = By.name("expiry_year");
    private final By payAndConfirmButton = By.id("submit");

    private final By orderPlacedHeader = By.xpath("//h2[@class='title text-center' or contains(@class,'title')][contains(text(),'Order Placed')]");
    private final By orderConfirmationMessage = By.xpath("//h2[contains(text(),'Order Placed')]/following::p[1]");
    private final By downloadInvoiceButton = By.linkText("Download Invoice");
    private final By continueButton = By.xpath("//a[text()='Continue']");

    public PaymentPage(WebDriver driver) {
        super(driver);
    }

    // ---------- Fill card details ----------
    public PaymentPage enterNameOnCard(String name) {
        type(nameOnCardInput, name);
        return this;
    }

    public PaymentPage enterCardNumber(String cardNumber) {
        type(cardNumberInput, cardNumber);
        return this;
    }

    public PaymentPage enterCvc(String cvc) {
        type(cvcInput, cvc);
        return this;
    }

    public PaymentPage enterExpiryMonth(String month) {
        type(expiryMonthInput, month);
        return this;
    }

    public PaymentPage enterExpiryYear(String year) {
        type(expiryYearInput, year);
        return this;
    }

    /**
     * Fills every card field in one call - convenient for data-driven tests
     * where any of the fields may intentionally be blank/invalid.
     */
    public PaymentPage enterCardDetails(String nameOnCard, String cardNumber, String cvc,
                                         String expiryMonth, String expiryYear) {
        enterNameOnCard(nameOnCard);
        enterCardNumber(cardNumber);
        enterCvc(cvc);
        enterExpiryMonth(expiryMonth);
        enterExpiryYear(expiryYear);
        return this;
    }

    public void clickPayAndConfirmOrder() {
        click(payAndConfirmButton);
    }

    // ---------- Order confirmation ----------
    public boolean isOrderConfirmed() {
        return isElementPresent(orderPlacedHeader) && isDisplayed(orderPlacedHeader);
    }

    public String getOrderConfirmationMessage() {
        return getText(orderConfirmationMessage);
    }

    public boolean isDownloadInvoiceButtonDisplayed() {
        return isDisplayed(downloadInvoiceButton);
    }

    public void clickContinue() {
        click(continueButton);
    }

    public boolean isOnPaymentPage() {
        return driver.getCurrentUrl().contains("/payment") && !driver.getCurrentUrl().contains("payment_done");
    }
}
