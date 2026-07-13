package com.ecommerce.automation.pages;

import com.ecommerce.automation.utils.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

/**
 * Page Object for https://automationexercise.com/products
 * Handles product search and "Add to Cart" interactions.
 */
public class ProductSearchPage extends BasePage {

    // ---------- Locators ----------
    private final By searchInput = By.id("search_product");
    private final By searchButton = By.id("submit_search");
    private final By searchedProductsHeader = By.xpath("//h2[@class='title text-center' and text()='Searched Products']");
    private final By productWrappers = By.cssSelector(".features_items .product-image-wrapper");
    private final By productNames = By.cssSelector(".features_items .productinfo p");
    private final By addToCartButtons = By.cssSelector(".features_items .productinfo .add-to-cart");
    private final By modalContinueShoppingButton = By.cssSelector("button.close-modal");
    private final By modalViewCartLink = By.xpath("//div[@id='cartModal']//u[text()='View Cart']");
    private final By cartInfoIcon = By.xpath("//a[@href='/view_cart']");

    public ProductSearchPage(WebDriver driver) {
        super(driver);
    }

    // ---------- Navigation ----------
    public ProductSearchPage open() {
        driver.get(ConfigReader.baseUrl() + "/products");
        waitForVisible(searchInput);
        return this;
    }

    // ---------- Search ----------
    public ProductSearchPage searchForProduct(String productName) {
        type(searchInput, productName);
        click(searchButton);
        waitForVisible(searchedProductsHeader);
        return this;
    }

    public boolean isSearchedProductsHeaderDisplayed() {
        return isDisplayed(searchedProductsHeader);
    }

    public int getVisibleProductCount() {
        return waitForAllVisible(productWrappers).size();
    }

    public List<String> getVisibleProductNames() {
        return driver.findElements(productNames).stream().map(WebElement::getText).toList();
    }

    // ---------- Add to cart ----------
    /**
     * Adds the product at the given zero-based index (as currently displayed
     * on the page) to the cart and dismisses the "added to cart" modal.
     */
    public ProductSearchPage addProductToCartByIndex(int index) {
        List<WebElement> buttons = waitForAllVisible(addToCartButtons);
        WebElement target = buttons.get(index);
        scrollIntoView(target);
        target.click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(modalContinueShoppingButton));
        return this;
    }

    public ProductSearchPage addFirstProductToCart() {
        return addProductToCartByIndex(0);
    }

    public void continueShoppingFromModal() {
        click(modalContinueShoppingButton);
        waitForInvisible(modalContinueShoppingButton);
    }

    public CartPage viewCartFromModal() {
        click(modalViewCartLink);
        return new CartPage(driver);
    }

    public CartPage goToCart() {
        click(cartInfoIcon);
        return new CartPage(driver);
    }
}
