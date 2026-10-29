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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("TC_E2E_06: E2E - Cancel the order mid-flow (Cancel on Overview) and purchase again")
public class TC_E2E_06 extends BaseTest {
    static LoginPage loginPage;
    static InventoryPage inventoryPage;
    static HeaderComponent header;
    static CartPage cartPage;
    static CheckoutInformationPage checkoutInformationPage;
    static CheckoutOverviewPage checkoutOverviewPage;
    static CheckoutCompletePage checkoutCompletePage;

    private static final String BASE_URL = ConfigReader.get("baseUrl");
    private static final String PRODUCT_NAME = "Sauce Labs Bolt T-Shirt";
    private static final String EXPECTED_CART_BADGE = "1";
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
    @DisplayName("Step 2: Add [Sauce Labs Bolt T-Shirt] to the cart, go to Checkout, enter valid information, click Continue</br>" +
            "Expected Results: App navigates to https://www.saucedemo.com/checkout-step-two.html")
    public void checkoutFirstAttempt() {
        inventoryPage.addProductToCart(PRODUCT_NAME);
        header.clickCartIcon();
        cartPage.clickCheckout();
        checkoutInformationPage.fillInformationAndContinue("Bear", "Nguyen", "700000");
        verifyCurrentUrl(BASE_URL + "checkout-step-two.html");
    }

    @Test
    @Order(3)
    @DisplayName("Step 3: On the Overview page, click [Cancel]")
    public void cancelOnOverview() {
        checkoutOverviewPage.clickCancel();
    }

    @Test
    @Order(4)
    @DisplayName("Step 4: Confirm navigation to Inventory and that the product STILL remains in the cart</br>" +
            "Expected Results: Returns to Inventory; the cart data is NOT lost (badge [1])")
    public void verifyCartKeptAfterCancel() {
        assertAll(
                () -> verifyCurrentUrl(BASE_URL + "inventory.html"),
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

    @Test
    @Order(5)
    @DisplayName("Step 5: Go back to Cart -> Checkout -> enter information -> Continue -> Finish")
    public void checkoutSecondAttempt() {
        header.clickCartIcon();
        cartPage.clickCheckout();
        checkoutInformationPage.fillInformationAndContinue("Bear", "Nguyen", "700000");
        checkoutOverviewPage.clickFinish();
    }

    @Test
    @Order(6)
    @DisplayName("Step 6: Confirm the order completes successfully</br>" +
            "Expected Results: The order completes successfully on the second attempt")
    public void verifyOrderCompleted() {
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
