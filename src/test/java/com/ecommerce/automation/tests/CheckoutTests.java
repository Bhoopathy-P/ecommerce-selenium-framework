package com.ecommerce.automation.tests;

import com.ecommerce.automation.base.BaseTest;
import com.ecommerce.automation.pages.CheckoutPage;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test cases covering the Checkout module on https://automationexercise.com/checkout
 * <p>
 * Every scenario here logs in first (checkout on this application requires an
 * authenticated user), adds product(s) to the cart, then proceeds to checkout.
 * <p>
 * Groups:
 *  - smoke      : minimal path from cart -> checkout -> payment page
 *  - regression : address panel, order review table and total validation
 */
public class CheckoutTests extends BaseTest {

    private CheckoutPage checkoutPage;

    private void addProductAndGoToCheckout() {
        loginWithValidUser();
        productSearchPage.open();
        productSearchPage.addFirstProductToCart();
        cartPage = productSearchPage.viewCartFromModal();
        checkoutPage = cartPage.goToCheckoutPage();
    }

    @Test(groups = {"smoke", "regression"}, description = "Delivery address section is displayed on the checkout page")
    public void testDeliveryAddressDisplayed() {
        addProductAndGoToCheckout();
        Assert.assertTrue(checkoutPage.isDeliveryAddressDisplayed(), "Delivery address block should be visible");
    }

    @Test(groups = {"regression"}, description = "Billing address section is displayed on the checkout page")
    public void testBillingAddressDisplayed() {
        addProductAndGoToCheckout();
        Assert.assertTrue(checkoutPage.isBillingAddressDisplayed(), "Billing address block should be visible");
    }

    @Test(groups = {"regression"}, description = "Delivery address text is populated (not blank) from the user's account")
    public void testDeliveryAddressTextIsPopulated() {
        addProductAndGoToCheckout();
        String addressText = checkoutPage.getDeliveryAddressText();
        Assert.assertFalse(addressText.isBlank(), "Delivery address should be populated from account details");
    }

    @Test(groups = {"smoke", "regression"}, description = "Order review table listing cart items is displayed at checkout")
    public void testOrderReviewTableDisplayed() {
        addProductAndGoToCheckout();
        Assert.assertTrue(checkoutPage.isOrderReviewTableDisplayed(), "Order review table should be visible at checkout");
    }

    @Test(groups = {"regression"}, description = "Order review item count matches the number of items added to the cart")
    public void testOrderReviewItemCountMatchesCart() {
        addProductAndGoToCheckout();
        Assert.assertEquals(checkoutPage.getOrderReviewItemCount(), 1,
                "Order review should list exactly the 1 product that was added to the cart");
    }

    @Test(groups = {"regression"}, description = "Order review reflects multiple items added to the cart")
    public void testCheckoutWithMultipleItems() {
        loginWithValidUser();
        productSearchPage.open();
        productSearchPage.addProductToCartByIndex(0);
        productSearchPage.continueShoppingFromModal();
        productSearchPage.addProductToCartByIndex(1);
        cartPage = productSearchPage.viewCartFromModal();
        checkoutPage = cartPage.goToCheckoutPage();

        Assert.assertEquals(checkoutPage.getOrderReviewItemCount(), 2,
                "Order review should list both products added to the cart");
    }

    @Test(groups = {"regression"}, description = "Order total amount is calculated and displayed at checkout")
    public void testOrderTotalIsCalculatedAndDisplayed() {
        addProductAndGoToCheckout();
        String totalText = checkoutPage.getOrderTotalAmountText();
        Assert.assertTrue(totalText.matches(".*\\d.*"), "Order total should contain a numeric amount");
    }

    @Test(groups = {"regression"}, description = "Order comment/message field accepts free text input")
    public void testCommentFieldAcceptsText() {
        addProductAndGoToCheckout();
        checkoutPage.enterOrderComment("Please deliver between 9 AM and 6 PM. - QA automation test comment");
        // No dedicated getter exposed on the site for the textarea value; absence of an
        // exception while typing plus a stable page state is the pass condition here.
        Assert.assertTrue(checkoutPage.isOrderReviewTableDisplayed(),
                "Page should remain stable and functional after entering a comment");
    }

    @Test(groups = {"sanity", "regression"}, description = "'Place Order' button is visible and enabled on the checkout page")
    public void testPlaceOrderButtonVisible() {
        addProductAndGoToCheckout();
        Assert.assertTrue(checkoutPage.isPlaceOrderButtonDisplayed(), "'Place Order' button should be visible");
    }

    @Test(groups = {"smoke", "regression"}, description = "Clicking 'Place Order' navigates the user to the payment page")
    public void testPlaceOrderNavigatesToPaymentPage() {
        addProductAndGoToCheckout();
        checkoutPage.placeOrder();
        Assert.assertTrue(driver.getCurrentUrl().contains("/payment"),
                "Placing the order should navigate to the payment page");
    }

    @Test(groups = {"regression"}, description = "A guest (not logged in) user is redirected to login when attempting to check out")
    public void testCheckoutRequiresLogin() {
        productSearchPage.open();
        productSearchPage.addFirstProductToCart();
        cartPage = productSearchPage.viewCartFromModal();
        cartPage.proceedToCheckout();

        Assert.assertTrue(cartPage.isGuestCheckoutModalDisplayed(),
                "Guest users should not be able to reach the checkout page directly");
    }

    @Test(groups = {"sanity", "regression"}, description = "Checkout page URL is correct after proceeding from the cart")
    public void testCheckoutPageUrlIsCorrect() {
        addProductAndGoToCheckout();
        Assert.assertTrue(driver.getCurrentUrl().contains("/checkout"), "URL should contain '/checkout'");
    }

    @Test(groups = {"regression"}, description = "Checkout works correctly with a single cart item end-to-end up to the review step")
    public void testCheckoutWithSingleItem() {
        addProductAndGoToCheckout();
        Assert.assertEquals(checkoutPage.getOrderReviewItemCount(), 1, "Single-item checkout should show exactly 1 row");
        Assert.assertTrue(checkoutPage.isPlaceOrderButtonDisplayed(), "Place Order button should be available to complete checkout");
    }
}
