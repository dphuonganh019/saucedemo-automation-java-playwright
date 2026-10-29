package tests.checkout;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import pages.CartPage;
import pages.CheckoutCompletePage;
import pages.CheckoutInformationPage;
import pages.CheckoutOverviewPage;
import pages.HeaderComponent;
import pages.InventoryPage;
import pages.LoginPage;
import tests.BaseTest;
import utils.ConfigReader;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TC_CO_10: Checkout Complete - [Back Home] button returns to Inventory and resets the cart")
public class TC_CO_10 extends BaseTest {
    static LoginPage loginPage;
    static InventoryPage inventoryPage;
    static HeaderComponent header;
    static CartPage cartPage;
    static CheckoutInformationPage checkoutInformationPage;
    static CheckoutOverviewPage checkoutOverviewPage;
    static CheckoutCompletePage checkoutCompletePage;

    private static final String EXPECTED_URL = ConfigReader.get("baseUrl") + "inventory.html";

    @BeforeAll
    static void setupPrecondition() {
        loginPage = new LoginPage(page());
        inventoryPage = new InventoryPage(page());
        header = new HeaderComponent(page());
        cartPage = new CartPage(page());
        checkoutInformationPage = new CheckoutInformationPage(page());
        checkoutOverviewPage = new CheckoutOverviewPage(page());
        checkoutCompletePage = new CheckoutCompletePage(page());
        loginPage.goToSauceDemoPage();
        loginPage.loginAs(ConfigReader.get("username_standard_user"), ConfigReader.get("password"));
        inventoryPage.addProductToCart("Sauce Labs Backpack");
        header.clickCartIcon();
        cartPage.clickCheckout();
        checkoutInformationPage.fillInformationAndContinue("Bear", "Nguyen", "700000");
        checkoutOverviewPage.clickFinish();
    }

    @Test
    @Order(1)
    @DisplayName("Step 1: Click the [Back Home] button")
    public void clickBackHome() {
        checkoutCompletePage.clickBackHome();
    }

    @Test
    @Order(2)
    @DisplayName("Expected Results:</br>" +
            "- System navigates to https://www.saucedemo.com/inventory.html;</br>" +
            "- Cart is reset to empty (badge no longer displayed)")
    public void verifyBackToInventoryWithEmptyCart() {
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
                () -> assertTrue(
                        header.isCartBadgeRemoved(),
                        "The cart badge is still displayed after completing the order"
                )
        );
    }
}
