package tests.e2e;

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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TC_E2E_01: E2E - Successfully purchase 1 product, from Login to Complete")
public class TC_E2E_01 extends BaseTest {
    static LoginPage loginPage;
    static InventoryPage inventoryPage;
    static HeaderComponent header;
    static CartPage cartPage;
    static CheckoutInformationPage checkoutInformationPage;
    static CheckoutOverviewPage checkoutOverviewPage;
    static CheckoutCompletePage checkoutCompletePage;

    private static final String BASE_URL = ConfigReader.get("baseUrl");
    private static final String PRODUCT_NAME = "Sauce Labs Backpack";
    private static final String EXPECTED_CART_BADGE = "1";
    private static final String EXPECTED_SUBTOTAL = "Item total: $29.99";
    private static final String EXPECTED_COMPLETE_HEADER = "Thank you for your order!";

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
    }

    @Test
    @Order(1)
    @DisplayName("Step 1: Log in with standard_user/secret_sauce</br>" +
            "Expected Results: App navigates to https://www.saucedemo.com/inventory.html")
    public void login() {
        loginPage.loginAs(ConfigReader.get("username_standard_user"), ConfigReader.get("password"));
        verifyCurrentUrl(BASE_URL + "inventory.html");
    }

    @Test
    @Order(2)
    @DisplayName("Step 2: On the Inventory page, click [Add to cart] for [Sauce Labs Backpack]</br>" +
            "Expected Results: Cart badge shows [1]")
    public void addProductToCart() {
        inventoryPage.addProductToCart(PRODUCT_NAME);
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

    @Test
    @Order(3)
    @DisplayName("Step 3: Click the cart icon</br>" +
            "Expected Results: App navigates to https://www.saucedemo.com/cart.html")
    public void goToCart() {
        header.clickCartIcon();
        verifyCurrentUrl(BASE_URL + "cart.html");
    }

    @Test
    @Order(4)
    @DisplayName("Step 4: On the Cart page, click [Checkout]</br>" +
            "Expected Results: App navigates to https://www.saucedemo.com/checkout-step-one.html")
    public void clickCheckout() {
        cartPage.clickCheckout();
        verifyCurrentUrl(BASE_URL + "checkout-step-one.html");
    }

    @Test
    @Order(5)
    @DisplayName("Step 5: Enter First Name=[Bear], Last Name=[Nguyen], Postal Code=[700000], click [Continue]</br>" +
            "Expected Results: App navigates to https://www.saucedemo.com/checkout-step-two.html")
    public void enterInformationAndContinue() {
        checkoutInformationPage.fillInformationAndContinue("Bear", "Nguyen", "700000");
        verifyCurrentUrl(BASE_URL + "checkout-step-two.html");
    }

    @Test
    @Order(6)
    @DisplayName("Step 6: On the Overview page, verify the order details, then click [Finish]</br>" +
            "Expected Results:</br>" +
            "- Overview shows [Sauce Labs Backpack], [Item total: $29.99];</br>" +
            "- App navigates to https://www.saucedemo.com/checkout-complete.html;</br>" +
            "- Checkout Complete page shows [Thank you for your order!];</br>" +
            "- Cart badge disappears after completion")
    public void verifyOrderAndFinish() {
        final List<String> actualItemNames = checkoutOverviewPage.getOverviewItemNames();
        final String actualSubtotal = checkoutOverviewPage.getSubtotalText();
        assertAll(
                () -> assertEquals(
                        List.of(PRODUCT_NAME),
                        actualItemNames,
                        "The overview items are not as expected. Expected: ["
                                + List.of(PRODUCT_NAME)
                                + "], Actual: ["
                                + actualItemNames
                                + "]"
                ),
                () -> assertEquals(
                        EXPECTED_SUBTOTAL,
                        actualSubtotal,
                        "The Item total is not as expected. Expected: ["
                                + EXPECTED_SUBTOTAL
                                + "], Actual: ["
                                + actualSubtotal
                                + "]"
                )
        );

        checkoutOverviewPage.clickFinish();
        final String actualCompleteHeader = checkoutCompletePage.getCompleteHeader();
        assertAll(
                () -> verifyCurrentUrl(BASE_URL + "checkout-complete.html"),
                () -> assertEquals(
                        EXPECTED_COMPLETE_HEADER,
                        actualCompleteHeader,
                        "The complete header is not as expected. Expected: ["
                                + EXPECTED_COMPLETE_HEADER
                                + "], Actual: ["
                                + actualCompleteHeader
                                + "]"
                ),
                () -> assertTrue(
                        header.isCartBadgeRemoved(),
                        "The cart badge is still displayed after completing the order"
                )
        );
    }

    private void verifyCurrentUrl(String expectedUrl) {
        final String actualUrl = browserManager().getCurrentTabUrl(expectedUrl);
        assertEquals(
                expectedUrl,
                actualUrl,
                "The app did not navigate to the expected page. Expected: ["
                        + expectedUrl
                        + "], Actual: ["
                        + actualUrl
                        + "]"
        );
    }
}
