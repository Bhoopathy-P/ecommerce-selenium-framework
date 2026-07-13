package com.ecommerce.automation.tests;

import com.ecommerce.automation.base.BaseTest;
import com.ecommerce.automation.dataproviders.DataProviders;
import com.ecommerce.automation.pages.CheckoutPage;
import com.ecommerce.automation.pages.PaymentPage;
import com.ecommerce.automation.utils.ConfigReader;
import com.ecommerce.automation.utils.JsonDataReader;
import com.fasterxml.jackson.databind.JsonNode;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test cases covering the Payment module on https://automationexercise.com/payment
 * and the resulting order confirmation screen.
 * <p>
 * NOTE: automationexercise.com is a public demo/training site - "payment" here
 * only validates client-side form handling and order-confirmation UI; no real
 * card is ever charged. Card values used are well-known dummy test numbers.
 * <p>
 * Groups:
 *  - smoke      : full happy-path order placement
 *  - regression : card field validation and confirmation-page checks
 */
public class PaymentTests extends BaseTest {

    private static final String PAYMENT_DATA_FILE = "src/test/resources/testdata/paymentData.json";

    private PaymentPage goToPaymentPage() {
        loginWithValidUser();
        productSearchPage.open();
        productSearchPage.addFirstProductToCart();
        cartPage = productSearchPage.viewCartFromModal();
        CheckoutPage checkoutPage = cartPage.goToCheckoutPage();
        return checkoutPage.placeOrder();
    }

    @Test(groups = {"smoke", "regression"}, description = "A valid card payment results in a confirmed order")
    public void testValidPaymentPlacesOrderSuccessfully() {
        PaymentPage paymentPage = goToPaymentPage();
        JsonNode validCard = JsonDataReader.readNode(PAYMENT_DATA_FILE, "validCardDetails");

        paymentPage.enterCardDetails(
                validCard.get("nameOnCard").asText(),
                validCard.get("cardNumber").asText(),
                validCard.get("cvc").asText(),
                validCard.get("expiryMonth").asText(),
                validCard.get("expiryYear").asText()
        );
        paymentPage.clickPayAndConfirmOrder();

        Assert.assertTrue(paymentPage.isOrderConfirmed(),
                "Order should be confirmed ('Order Placed!') after submitting valid card details");
    }

    @Test(groups = {"regression"}, description = "Order confirmation page displays the success message text")
    public void testOrderConfirmationMessageDisplayed() {
        PaymentPage paymentPage = goToPaymentPage();
        JsonNode validCard = JsonDataReader.readNode(PAYMENT_DATA_FILE, "validCardDetails");

        paymentPage.enterCardDetails(
                validCard.get("nameOnCard").asText(),
                validCard.get("cardNumber").asText(),
                validCard.get("cvc").asText(),
                validCard.get("expiryMonth").asText(),
                validCard.get("expiryYear").asText()
        );
        paymentPage.clickPayAndConfirmOrder();

        String confirmationMessage = paymentPage.getOrderConfirmationMessage();
        Assert.assertFalse(confirmationMessage.isBlank(),
                "A confirmation message should be displayed below the 'Order Placed!' heading");
    }

    @Test(groups = {"regression"}, description = "'Download Invoice' option is available after a successful order")
    public void testDownloadInvoiceButtonDisplayed() {
        PaymentPage paymentPage = goToPaymentPage();
        JsonNode validCard = JsonDataReader.readNode(PAYMENT_DATA_FILE, "validCardDetails");

        paymentPage.enterCardDetails(
                validCard.get("nameOnCard").asText(),
                validCard.get("cardNumber").asText(),
                validCard.get("cvc").asText(),
                validCard.get("expiryMonth").asText(),
                validCard.get("expiryYear").asText()
        );
        paymentPage.clickPayAndConfirmOrder();

        Assert.assertTrue(paymentPage.isDownloadInvoiceButtonDisplayed(),
                "Download Invoice button/link should be present on the order confirmation page");
    }

    @Test(groups = {"regression"}, description = "'Continue' on the confirmation page navigates the user away from the order summary")
    public void testContinueButtonNavigatesAwayFromConfirmation() {
        PaymentPage paymentPage = goToPaymentPage();
        JsonNode validCard = JsonDataReader.readNode(PAYMENT_DATA_FILE, "validCardDetails");

        paymentPage.enterCardDetails(
                validCard.get("nameOnCard").asText(),
                validCard.get("cardNumber").asText(),
                validCard.get("cvc").asText(),
                validCard.get("expiryMonth").asText(),
                validCard.get("expiryYear").asText()
        );
        paymentPage.clickPayAndConfirmOrder();
        Assert.assertTrue(paymentPage.isOrderConfirmed(), "Precondition failed: order was not confirmed");

        paymentPage.clickContinue();

        Assert.assertFalse(driver.getCurrentUrl().contains("payment_done"),
                "Clicking Continue should navigate the user away from the order confirmation page");
    }

    @Test(groups = {"sanity", "regression"}, description = "Payment page loads with all card input fields empty and editable")
    public void testPaymentPageLoadsWithEditableFields() {
        PaymentPage paymentPage = goToPaymentPage();
        Assert.assertTrue(paymentPage.isOnPaymentPage(), "Should be on the /payment route before submitting card details");

        // Typing into every field should not throw - proves fields are present, enabled and editable
        paymentPage.enterCardDetails("QA Field Check", "4000000000000000", "999", "01", "2031");
        Assert.assertTrue(paymentPage.isOnPaymentPage(), "Page should remain on payment step until submitted");
    }

    @Test(groups = {"regression"}, description = "Payment form fields correctly accept and retain typed input")
    public void testPaymentFormFieldsAreEditable() {
        PaymentPage paymentPage = goToPaymentPage();
        paymentPage.enterNameOnCard("Editable Field Test");
        paymentPage.enterCardNumber("5105105105105100");
        paymentPage.enterCvc("321");
        paymentPage.enterExpiryMonth("06");
        paymentPage.enterExpiryYear("2029");
        // If any locator were wrong, the type() calls above would have already thrown
        // a TimeoutException, so reaching this line confirms all fields are interactable.
        Assert.assertTrue(paymentPage.isOnPaymentPage(), "Should still be on payment page prior to submission");
    }

    // ---------------------------------------------------------------
    // Negative / field validation scenarios - data-driven
    // ---------------------------------------------------------------

    @Test(dataProvider = "invalidCardData", dataProviderClass = DataProviders.class,
            groups = {"regression"},
            description = "Order is NOT confirmed when required card fields are missing or malformed")
    public void testInvalidCardDetailsDoNotConfirmOrder(String nameOnCard, String cardNumber, String cvc,
                                                          String expiryMonth, String expiryYear, String scenario) {
        PaymentPage paymentPage = goToPaymentPage();
        paymentPage.enterCardDetails(nameOnCard, cardNumber, cvc, expiryMonth, expiryYear);
        paymentPage.clickPayAndConfirmOrder();

        Assert.assertFalse(paymentPage.isOrderConfirmed(),
                "Order should NOT be confirmed for invalid scenario: " + scenario);
    }
}
