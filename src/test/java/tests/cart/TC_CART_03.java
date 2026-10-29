package tests.cart;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import pages.CartPage;
import pages.HeaderComponent;
import pages.InventoryPage;
import pages.LoginPage;
import tests.BaseTest;
import utils.ConfigReader;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("TC_CART_03: [Continue Shopping] button navigates back to the Inventory page")
public class TC_CART_03 extends BaseTest {
    static LoginPage loginPage;
    static InventoryPage inventoryPage;
    static HeaderComponent header;
    static CartPage cartPage;

    private static final String EXPECTED_URL = ConfigReader.get("baseUrl") + "inventory.html";
    private static final String EXPECTED_CART_BADGE = "1";

    @BeforeAll
    static void setupPrecondition() {
        loginPage = new LoginPage(page());
        inventoryPage = new InventoryPage(page());
        header = new HeaderComponent(page());
        cartPage = new CartPage(page());
        loginPage.goToSauceDemoPage();
        loginPage.loginAs(ConfigReader.get("username_standard_user"), ConfigReader.get("password"));
        inventoryPage.addProductToCart("Sauce Labs Backpack");
        header.clickCartIcon();
    }

    @Test
    @Order(1)
    @DisplayName("Step 1: Click the [Continue Shopping] button")
    public void clickContinueShopping() {
        cartPage.clickContinueShopping();
    }

    @Test
    @Order(2)
    @DisplayName("Expected Results:</br>" +
            "- System navigates to https://www.saucedemo.com/inventory.html;</br>" +
            "- Products previously added remain in the cart (badge unchanged: [1])")
    public void verifyBackToInventory() {
        // Chờ trang Inventory render xong rồi mới đọc URL
        inventoryPage.getProductCount();
        assertAll(
                () -> {
                    final String actualUrl = browserManager().getCurrentTabUrl();
                    assertEquals(
                            EXPECTED_URL,
                            actualUrl,
                            "The system did not redirect to the expected URL. Expected: ["
                                    + EXPECTED_URL
                                    + "], Actual: ["
                                    + actualUrl
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
