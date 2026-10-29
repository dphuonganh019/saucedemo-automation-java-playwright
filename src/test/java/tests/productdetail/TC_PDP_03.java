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

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TC_PDP_03: Remove a product from the cart on the Product Detail page")
public class TC_PDP_03 extends BaseTest {
    static LoginPage loginPage;
    static InventoryPage inventoryPage;
    static ProductDetailPage productDetailPage;
    static HeaderComponent header;

    private static final String PRODUCT_NAME = "Sauce Labs Backpack";
    private static final String REMOVE_BTN_TXT = "Remove";
    private static final String ADD_TO_CART_BTN_TXT = "Add to cart";

    @BeforeAll
    static void setupPrecondition() {
        loginPage = new LoginPage(page());
        inventoryPage = new InventoryPage(page());
        productDetailPage = new ProductDetailPage(page());
        header = new HeaderComponent(page());
        loginPage.goToSauceDemoPage();
        loginPage.loginAs(ConfigReader.get("username_standard_user"), ConfigReader.get("password"));
        inventoryPage.clickProductName(PRODUCT_NAME);
        productDetailPage.clickAddToCartButton();
    }

    @Test
    @Order(1)
    @DisplayName("Step 1: Confirm the button currently shows [Remove]</br>" +
            "Expected Results: Button shows [Remove]")
    public void verifyRemoveButtonDisplayed() {
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
    }

    @Test
    @Order(2)
    @DisplayName("Step 2: Click the [Remove] button")
    public void clickRemoveButton() {
        productDetailPage.clickRemoveButton();
    }

    @Test
    @Order(3)
    @DisplayName("Expected Results:</br>" +
            "- Button label reverts to [Add to cart];</br>" +
            "- Cart badge disappears (this was the only item)")
    public void verifyProductRemovedFromCart() {
        assertAll(
                () -> {
                    final String actualButtonText = productDetailPage.getAddToCartButtonText();
                    assertEquals(
                            ADD_TO_CART_BTN_TXT,
                            actualButtonText,
                            "The button label is not as expected. Expected: ["
                                    + ADD_TO_CART_BTN_TXT
                                    + "], Actual: ["
                                    + actualButtonText
                                    + "]"
                    );
                },
                () -> assertTrue(
                        header.isCartBadgeRemoved(),
                        "The cart badge is still displayed although the cart is empty"
                )
        );
    }
}
