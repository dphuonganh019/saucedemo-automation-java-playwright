package tests.checkout;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import pages.CartPage;
import pages.CheckoutInformationPage;
import pages.HeaderComponent;
import pages.InventoryPage;
import pages.LoginPage;
import tests.BaseTest;
import utils.ConfigReader;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("TC_CO_05: Checkout Step One - Cancel button returns to the Cart page")
public class TC_CO_05 extends BaseTest {
    static LoginPage loginPage;
    static InventoryPage inventoryPage;
    static HeaderComponent header;
    static CartPage cartPage;
    static CheckoutInformationPage checkoutInformationPage;

    private static final String PRODUCT_NAME = "Sauce Labs Backpack";
    private static final List<String> EXPECTED_CART_ITEMS = List.of(PRODUCT_NAME);
    private static final String EXPECTED_URL = ConfigReader.get("baseUrl") + "cart.html";

    @BeforeAll
    static void setupPrecondition() {
        loginPage = new LoginPage(page());
        inventoryPage = new InventoryPage(page());
        header = new HeaderComponent(page());
        cartPage = new CartPage(page());
        checkoutInformationPage = new CheckoutInformationPage(page());
        loginPage.goToSauceDemoPage();
        loginPage.loginAs(ConfigReader.get("username_standard_user"), ConfigReader.get("password"));
        inventoryPage.addProductToCart(PRODUCT_NAME);
        header.clickCartIcon();
        cartPage.clickCheckout();
    }

    @Test
    @Order(1)
    @DisplayName("Step 1: Click the [Cancel] button")
    public void clickCancel() {
        checkoutInformationPage.clickCancel();
    }

    @Test
    @Order(2)
    @DisplayName("Expected Results:</br>" +
            "- System navigates to https://www.saucedemo.com/cart.html;</br>" +
            "- Cart contents are unchanged")
    public void verifyBackToCart() {
        final List<String> actualCartItems = cartPage.getCartItemNames();
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
                () -> assertEquals(
                        EXPECTED_CART_ITEMS,
                        actualCartItems,
                        "The cart contents are not as expected. Expected: ["
                                + EXPECTED_CART_ITEMS
                                + "], Actual: ["
                                + actualCartItems
                                + "]"
                )
        );
    }
}
