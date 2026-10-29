package tests.checkout;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import pages.CartPage;
import pages.CheckoutInformationPage;
import pages.CheckoutOverviewPage;
import pages.HeaderComponent;
import pages.InventoryPage;
import pages.LoginPage;
import tests.BaseTest;
import utils.ConfigReader;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("TC_CO_07: Checkout Step Two - Cancel button returns to the Inventory page")
public class TC_CO_07 extends BaseTest {
    static LoginPage loginPage;
    static InventoryPage inventoryPage;
    static HeaderComponent header;
    static CartPage cartPage;
    static CheckoutInformationPage checkoutInformationPage;
    static CheckoutOverviewPage checkoutOverviewPage;

    private static final String EXPECTED_URL = ConfigReader.get("baseUrl") + "inventory.html";
    private static final String EXPECTED_CART_BADGE = "1";

    @BeforeAll
    static void setupPrecondition() {
        loginPage = new LoginPage(page());
        inventoryPage = new InventoryPage(page());
        header = new HeaderComponent(page());
        cartPage = new CartPage(page());
        checkoutInformationPage = new CheckoutInformationPage(page());
        checkoutOverviewPage = new CheckoutOverviewPage(page());
        loginPage.goToSauceDemoPage();
        loginPage.loginAs(ConfigReader.get("username_standard_user"), ConfigReader.get("password"));
        inventoryPage.addProductToCart("Sauce Labs Backpack");
        header.clickCartIcon();
        cartPage.clickCheckout();
        checkoutInformationPage.fillInformationAndContinue("Bear", "Nguyen", "700000");
    }

    @Test
    @Order(1)
    @DisplayName("Step 1: Click the [Cancel] button")
    public void clickCancel() {
        checkoutOverviewPage.clickCancel();
    }

    @Test
    @Order(2)
    @DisplayName("Expected Results:</br>" +
            "- System navigates to https://www.saucedemo.com/inventory.html;</br>" +
            "- The order is NOT completed, products remain in the cart (badge [1])")
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
