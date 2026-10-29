package tests.productdetail;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import pages.HeaderComponent;
import pages.InventoryPage;
import pages.LoginPage;
import pages.ProductDetailPage;
import tests.BaseTest;
import utils.ConfigReader;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("TC_PDP_02: Add a product to the cart from the Product Detail page")
public class TC_PDP_02 extends BaseTest {
    static LoginPage loginPage;
    static InventoryPage inventoryPage;
    static ProductDetailPage productDetailPage;
    static HeaderComponent header;

    private static final String PRODUCT_NAME = "Sauce Labs Backpack";
    private static final String REMOVE_BTN_TXT = "Remove";
    private static final String EXPECTED_CART_BADGE = "1";

    @BeforeAll
    static void setupPrecondition() {
        loginPage = new LoginPage(page());
        inventoryPage = new InventoryPage(page());
        productDetailPage = new ProductDetailPage(page());
        header = new HeaderComponent(page());
        loginPage.goToSauceDemoPage();
        loginPage.loginAs(ConfigReader.get("username_standard_user"), ConfigReader.get("password"));
        inventoryPage.clickProductName(PRODUCT_NAME);
    }

    @Test
    @Order(1)
    @DisplayName("Step 1: On the Product Detail page, click the [Add to cart] button")
    public void clickAddToCart() {
        productDetailPage.clickAddToCartButton();
    }

    @Test
    @Order(2)
    @DisplayName("Expected Results:</br>" +
            "- Button label changes to [Remove];</br>" +
            "- Cart badge shows [1]")
    public void verifyProductAddedToCart() {
        assertAll(
                () -> {
                    final String actualButtonText = productDetailPage.getRemoveButtonText();
                    assertEquals(
                            REMOVE_BTN_TXT,
                            actualButtonText,
                            "The button label is not as expected. Expected: ["
                                    + REMOVE_BTN_TXT
                                    + "], Actual: ["
                                    + actualButtonText
                                    + "]"
                    );
                },
                () -> {
                    final String actualCartBadge = header.getCartBadgeText();
                    assertEquals(
                            EXPECTED_CART_BADGE,
                            actualCartBadge,
                            "The cart badge is not as expected. Expected: ["
                                    + EXPECTED_CART_BADGE
                                    + "], Actual: ["
                                    + actualCartBadge
                                    + "]"
                    );
                }
        );
    }
}
