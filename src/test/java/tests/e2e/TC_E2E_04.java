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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("TC_E2E_04: E2E - Sort products, then purchase the cheapest one")
public class TC_E2E_04 extends BaseTest {
    static LoginPage loginPage;
    static InventoryPage inventoryPage;
    static HeaderComponent header;
    static CartPage cartPage;
    static CheckoutInformationPage checkoutInformationPage;
    static CheckoutOverviewPage checkoutOverviewPage;
    static CheckoutCompletePage checkoutCompletePage;

    private static final String BASE_URL = ConfigReader.get("baseUrl");
    private static final String SORT_OPTION_VALUE = "lohi";
    private static final String EXPECTED_ACTIVE_OPTION = "Price (low to high)";
    private static final String EXPECTED_CHEAPEST_PRODUCT = "Sauce Labs Onesie";
    private static final String EXPECTED_CHEAPEST_PRICE = "$7.99";
    private static final String EXPECTED_SUBTOTAL = "Item total: $7.99";
    private static final String EXPECTED_COMPLETE_HEADER = "Thank you for your order!";
    // Sản phẩm đầu tiên sau khi sort, lấy ở Step 3 để dùng cho Step 4
    static String firstProductName;

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
    @DisplayName("Step 2: On the Inventory page, select the [Price (low to high)] sort option</br>" +
            "Expected Results: Active option shows [Price (low to high)]")
    public void sortByPriceLowToHigh() {
        inventoryPage.selectSortOption(SORT_OPTION_VALUE);
        final String actualActiveOption = inventoryPage.getActiveSortOption();
        assertEquals(
                EXPECTED_ACTIVE_OPTION,
                actualActiveOption,
                "The active sort option is not as expected. Expected: ["
                        + EXPECTED_ACTIVE_OPTION
                        + "], Actual: ["
                        + actualActiveOption
                        + "]"
        );
    }

    @Test
    @Order(3)
    @DisplayName("Step 3: Confirm the first product is [Sauce Labs Onesie] ($7.99)</br>" +
            "Expected Results: First product is [Sauce Labs Onesie] with price [$7.99]")
    public void verifyCheapestProductFirst() {
        firstProductName = inventoryPage.getProductNames().getFirst();
        final String actualFirstPrice = inventoryPage.getProductPrices().getFirst();
        assertAll(
                () -> assertEquals(
                        EXPECTED_CHEAPEST_PRODUCT,
                        firstProductName,
                        "The first product after sorting is not as expected. Expected: ["
                                + EXPECTED_CHEAPEST_PRODUCT
                                + "], Actual: ["
                                + firstProductName
                                + "]"
                ),
                () -> assertEquals(
                        EXPECTED_CHEAPEST_PRICE,
                        actualFirstPrice,
                        "The price of the first product is not as expected. Expected: ["
                                + EXPECTED_CHEAPEST_PRICE
                                + "], Actual: ["
                                + actualFirstPrice
                                + "]"
                )
        );
    }

    @Test
    @Order(4)
    @DisplayName("Step 4: Click [Add to cart] for the first product in the sorted list")
    public void addFirstProductToCart() {
        inventoryPage.addProductToCart(firstProductName);
    }

    @Test
    @Order(5)
    @DisplayName("Step 5: Go to Cart -> Checkout -> enter valid information -> Continue</br>" +
            "Expected Results: App navigates to https://www.saucedemo.com/checkout-step-two.html")
    public void checkoutWithValidInformation() {
        header.clickCartIcon();
        cartPage.clickCheckout();
        checkoutInformationPage.fillInformationAndContinue("Bear", "Nguyen", "700000");
        verifyCurrentUrl(BASE_URL + "checkout-step-two.html");
    }

    @Test
    @Order(6)
    @DisplayName("Step 6: Confirm the Overview page shows [Sauce Labs Onesie] correctly, Item total = $7.99</br>" +
            "Expected Results: [Sauce Labs Onesie] is purchased at the correct price of [$7.99]")
    public void verifyOverview() {
        final List<String> actualItemNames = checkoutOverviewPage.getOverviewItemNames();
        final String actualSubtotal = checkoutOverviewPage.getSubtotalText();
        assertAll(
                () -> assertEquals(
                        List.of(EXPECTED_CHEAPEST_PRODUCT),
                        actualItemNames,
                        "The overview items are not as expected. Expected: ["
                                + List.of(EXPECTED_CHEAPEST_PRODUCT)
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
    }

    @Test
    @Order(7)
    @DisplayName("Step 7: Click [Finish]</br>" +
            "Expected Results: Order completes, Checkout Complete page is displayed")
    public void finishOrder() {
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
