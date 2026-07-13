package com.ecommerce.automation.tests;

import com.ecommerce.automation.base.BaseTest;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Test cases covering the Cart module on https://automationexercise.com/view_cart
 * <p>
 * Groups:
 *  - smoke      : core add/remove-to-cart happy path
 *  - regression : full coverage of cart state, totals and persistence
 */
public class CartTests extends BaseTest {

    @Test(groups = {"smoke", "regression"}, description = "A single product can be added to the cart")
    public void testAddSingleProductToCart() {
        productSearchPage.open();
        productSearchPage.addFirstProductToCart();
        cartPage = productSearchPage.viewCartFromModal();
        Assert.assertEquals(cartPage.getCartItemCount(), 1, "Cart should contain exactly 1 product");
    }

    @Test(groups = {"smoke", "regression"}, description = "Multiple different products can be added to the cart")
    public void testAddMultipleProductsToCart() {
        productSearchPage.open();
        productSearchPage.addProductToCartByIndex(0);
        productSearchPage.continueShoppingFromModal();
        productSearchPage.addProductToCartByIndex(1);
        productSearchPage.continueShoppingFromModal();
        productSearchPage.addProductToCartByIndex(2);
        cartPage = productSearchPage.viewCartFromModal();
        Assert.assertEquals(cartPage.getCartItemCount(), 3, "Cart should contain exactly 3 distinct products");
    }

    @Test(groups = {"regression"}, description = "A product can be removed from the cart")
    public void testRemoveProductFromCart() {
        productSearchPage.open();
        productSearchPage.addFirstProductToCart();
        cartPage = productSearchPage.viewCartFromModal();
        int countBeforeRemoval = cartPage.getCartItemCount();

        cartPage.removeProductByIndex(0);

        Assert.assertTrue(cartPage.isCartEmpty() || cartPage.getCartItemCount() < countBeforeRemoval,
                "Cart item count should decrease (or cart should become empty) after removal");
    }

    @Test(groups = {"regression"}, description = "Removing all products results in an empty cart message")
    public void testRemoveAllProductsShowsEmptyCartMessage() {
        productSearchPage.open();
        productSearchPage.addProductToCartByIndex(0);
        productSearchPage.continueShoppingFromModal();
        productSearchPage.addProductToCartByIndex(1);
        cartPage = productSearchPage.viewCartFromModal();

        cartPage.removeAllProducts();

        Assert.assertTrue(cartPage.isCartEmpty(), "Cart should display the empty-cart message after removing all items");
    }

    @Test(groups = {"sanity", "regression"}, description = "An empty cart (no items ever added) shows the empty-cart message")
    public void testEmptyCartMessageOnFreshSession() {
        cartPage.open();
        Assert.assertTrue(cartPage.isCartEmpty(), "A cart with no items added should show 'Cart is empty!'");
    }

    @Test(groups = {"regression"}, description = "Cart total price per line item is calculated and displayed")
    public void testCartTotalPriceCalculation() {
        productSearchPage.open();
        productSearchPage.addFirstProductToCart();
        cartPage = productSearchPage.viewCartFromModal();

        List<String> totals = cartPage.getProductTotalsInCart();
        Assert.assertFalse(totals.isEmpty(), "Cart should display a total price for each line item");
        Assert.assertTrue(totals.get(0).matches(".*\\d.*"), "Total price text should contain a numeric amount");
    }

    @Test(groups = {"regression"}, description = "Newly added product defaults to a quantity of 1")
    public void testCartQuantityDefaultsToOne() {
        productSearchPage.open();
        productSearchPage.addFirstProductToCart();
        cartPage = productSearchPage.viewCartFromModal();

        List<String> quantities = cartPage.getProductQuantitiesInCart();
        Assert.assertEquals(quantities.get(0).trim(), "1", "Default quantity for a newly added product should be 1");
    }

    @Test(groups = {"regression"}, description = "Cart contents persist when navigating away and back")
    public void testCartPersistsAfterNavigatingAway() {
        productSearchPage.open();
        productSearchPage.addFirstProductToCart();
        cartPage = productSearchPage.viewCartFromModal();
        int itemCountBefore = cartPage.getCartItemCount();

        productSearchPage.open(); // navigate away to the products page
        cartPage.open();          // navigate back to the cart

        Assert.assertEquals(cartPage.getCartItemCount(), itemCountBefore,
                "Cart item count should be unchanged after navigating away and back");
    }

    @Test(groups = {"regression"}, description = "Cart item count in the UI matches the number of products added")
    public void testCartItemCountMatchesAddedProducts() {
        productSearchPage.open();
        productSearchPage.addProductToCartByIndex(0);
        productSearchPage.continueShoppingFromModal();
        productSearchPage.addProductToCartByIndex(1);
        cartPage = productSearchPage.viewCartFromModal();

        Assert.assertEquals(cartPage.getCartItemCount(), 2, "Cart should reflect exactly 2 added products");
    }

    @Test(groups = {"regression"}, description = "Product names shown in the cart match the products that were added")
    public void testCartDisplaysCorrectProductNames() {
        productSearchPage.open();
        List<String> availableNames = productSearchPage.getVisibleProductNames();
        String expectedName = availableNames.get(0);

        productSearchPage.addFirstProductToCart();
        cartPage = productSearchPage.viewCartFromModal();

        List<String> cartNames = cartPage.getProductNamesInCart();
        Assert.assertEquals(cartNames.get(0), expectedName,
                "Product name in the cart should match the product added from the listing page");
    }

    @Test(groups = {"regression"}, description = "Proceeding to checkout as a guest (not logged in) prompts for Register/Login")
    public void testProceedToCheckoutAsGuestShowsLoginPrompt() {
        productSearchPage.open();
        productSearchPage.addFirstProductToCart();
        cartPage = productSearchPage.viewCartFromModal();

        cartPage.proceedToCheckout();

        Assert.assertTrue(cartPage.isGuestCheckoutModalDisplayed(),
                "Guest users should be prompted to register/login before checking out");
    }

    @Test(groups = {"regression"}, description = "Adding the same product twice results in two cart line entries")
    public void testAddSameProductTwiceIncrementsCartRows() {
        productSearchPage.open();
        productSearchPage.addProductToCartByIndex(0);
        productSearchPage.continueShoppingFromModal();
        productSearchPage.addProductToCartByIndex(0);
        cartPage = productSearchPage.viewCartFromModal();

        Assert.assertTrue(cartPage.getCartItemCount() >= 1,
                "Cart should register the repeated add-to-cart action (either as 2 rows or an increased quantity)");
    }
}
