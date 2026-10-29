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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("TC_PDP_05: Cart badge updates immediately after adding from the Detail page")
public class TC_PDP_05 extends BaseTest {
    static LoginPage loginPage;
    static InventoryPage inventoryPage;
    static ProductDetailPage productDetailPage;
    static HeaderComponent header;

    private static final String EXPECTED_CART_BADGE = "1";

    @BeforeAll
    static void setupPrecondition() {
        loginPage = new LoginPage(page());
        inventoryPage = new InventoryPage(page());
        productDetailPage = new ProductDetailPage(page());
        header = new HeaderComponent(page());
        loginPage.goToSauceDemoPage();
        loginPage.loginAs(ConfigReader.get("username_standard_user"), ConfigReader.get("password"));
        inventoryPage.clickProductName("Sauce Labs Bike Light");
    }

    @Test
    @Order(1)
    @DisplayName("Step 1: Confirm the cart badge does not exist (cart is empty)</br>" +
            "Expected Results: Cart badge does not exist in the DOM")
    public void verifyCartBadgeNotExist() {
        assertTrue(
                header.isCartBadgeRemoved(),
                "The cart badge exists although the cart is empty"
        );
    }

    @Test
    @Order(2)
    @DisplayName("Step 2: Click [Add to cart]")
    public void clickAddToCart() {
        productDetailPage.clickAddToCartButton();
    }

    @Test
    @Order(3)
    @DisplayName("Step 3: Check the badge immediately, without reloading the page</br>" +
            "Expected Results: Cart badge updates to [1] without a page reload")
    public void verifyCartBadgeUpdated() {
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
}
