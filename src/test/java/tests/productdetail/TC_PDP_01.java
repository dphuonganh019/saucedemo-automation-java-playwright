package tests.productdetail;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import pages.InventoryPage;
import pages.LoginPage;
import pages.ProductDetailPage;
import tests.BaseTest;
import utils.ConfigReader;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TC_PDP_01: Detailed verification of the Product Detail page information")
public class TC_PDP_01 extends BaseTest {
    static LoginPage loginPage;
    static InventoryPage inventoryPage;
    static ProductDetailPage productDetailPage;

    private static final String PRODUCT_NAME = "Sauce Labs Backpack";
    private static final String EXPECTED_URL = ConfigReader.get("baseUrl") + "inventory-item.html?id=4";
    private static final String EXPECTED_DESCRIPTION = "carry.allTheThings() with the sleek, streamlined Sly Pack that melds"
            + " uncompromising style with unequaled laptop and tablet protection.";
    private static final String EXPECTED_PRICE = "$29.99";
    private static final String ADD_TO_CART_BTN_TXT = "Add to cart";

    @BeforeAll
    static void setupPrecondition() {
        loginPage = new LoginPage(page());
        inventoryPage = new InventoryPage(page());
        productDetailPage = new ProductDetailPage(page());
        loginPage.goToSauceDemoPage();
        loginPage.loginAs(ConfigReader.get("username_standard_user"), ConfigReader.get("password"));
        inventoryPage.clickProductName(PRODUCT_NAME);
    }

    @Test
    @Order(1)
    @DisplayName("Step 1: Confirm the URL is https://www.saucedemo.com/inventory-item.html?id=4</br>" +
            "Expected Results: URL matches the inventory-item.html?id=4 pattern")
    public void verifyUrl() {
        // Chờ trang detail render xong rồi mới đọc URL
        productDetailPage.getProductName();
        final String actualUrl = browserManager().getCurrentTabUrl();
        assertEquals(
                EXPECTED_URL,
                actualUrl,
                "The current URL is not as expected. Expected: ["
                        + EXPECTED_URL
                        + "], Actual: ["
                        + actualUrl
                        + "]"
        );
    }

    @Test
    @Order(2)
    @DisplayName("Step 2: Verify the product name is displayed correctly</br>" +
            "Expected Results: Name = [Sauce Labs Backpack]")
    public void verifyProductName() {
        final String actualName = productDetailPage.getProductName();
        assertEquals(
                PRODUCT_NAME,
                actualName,
                "The product name is not as expected. Expected: ["
                        + PRODUCT_NAME
                        + "], Actual: ["
                        + actualName
                        + "]"
        );
    }

    @Test
    @Order(3)
    @DisplayName("Step 3: Verify the full product description is displayed</br>" +
            "Expected Results: Description = [carry.allTheThings() with the sleek, streamlined Sly Pack...] (full text)")
    public void verifyProductDescription() {
        final String actualDescription = productDetailPage.getProductDescription();
        assertEquals(
                EXPECTED_DESCRIPTION,
                actualDescription,
                "The product description is not as expected. Expected: ["
                        + EXPECTED_DESCRIPTION
                        + "], Actual: ["
                        + actualDescription
                        + "]"
        );
    }

    @Test
    @Order(4)
    @DisplayName("Step 4: Verify the product price is displayed correctly</br>" +
            "Expected Results: Price = [$29.99]")
    public void verifyProductPrice() {
        final String actualPrice = productDetailPage.getProductPrice();
        assertEquals(
                EXPECTED_PRICE,
                actualPrice,
                "The product price is not as expected. Expected: ["
                        + EXPECTED_PRICE
                        + "], Actual: ["
                        + actualPrice
                        + "]"
        );
    }

    @Test
    @Order(5)
    @DisplayName("Step 5: Verify the product image is displayed and not broken</br>" +
            "Expected Results: Image renders correctly, not a broken/placeholder image")
    public void verifyProductImage() {
        assertTrue(
                productDetailPage.isProductImageLoaded(),
                "The product image is broken or not displayed"
        );
    }

    @Test
    @Order(6)
    @DisplayName("Step 6: Verify the [Add to cart] button is displayed and clickable</br>" +
            "Expected Results: [Add to cart] button is displayed, not disabled")
    public void verifyAddToCartButton() {
        assertAll(
                () -> assertTrue(
                        productDetailPage.isAddToCartButtonVisible(),
                        "The Add to cart button is not displayed as expected"
                ),
                () -> assertTrue(
                        productDetailPage.isAddToCartButtonEnabled(),
                        "The Add to cart button is disabled"
                ),
                () -> {
                    final String actualButtonText = productDetailPage.getAddToCartButtonText();
                    assertEquals(
                            ADD_TO_CART_BTN_TXT,
                            actualButtonText,
                            "The Add to cart button text is not as expected. Expected: ["
                                    + ADD_TO_CART_BTN_TXT
                                    + "], Actual: ["
                                    + actualButtonText
                                    + "]"
                    );
                }
        );
    }

    @Test
    @Order(7)
    @DisplayName("Step 7: Verify the [Back to products] button is displayed and clickable</br>" +
            "Expected Results: [Back to products] button is displayed and clickable")
    public void verifyBackToProductsButton() {
        assertAll(
                () -> assertTrue(
                        productDetailPage.isBackToProductsButtonVisible(),
                        "The Back to products button is not displayed as expected"
                ),
                () -> assertTrue(
                        productDetailPage.isBackToProductsButtonEnabled(),
                        "The Back to products button is not clickable as expected"
                )
        );
    }
}
