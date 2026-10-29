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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TC_E2E_02: E2E - Purchase multiple products, verify the total amount is accurate")
public class TC_E2E_02 extends BaseTest {
    static LoginPage loginPage;
    static InventoryPage inventoryPage;
    static HeaderComponent header;
    static CartPage cartPage;
    static CheckoutInformationPage checkoutInformationPage;
    static CheckoutOverviewPage checkoutOverviewPage;
    static CheckoutCompletePage checkoutCompletePage;

    private static final String BASE_URL = ConfigReader.get("baseUrl");
    private static final List<String> PRODUCTS = List.of(
            "Sauce Labs Backpack",
            "Sauce Labs Bike Light",
            "Sauce Labs Onesie"
    );
    private static final String EXPECTED_CART_BADGE = "3";
    private static final BigDecimal EXPECTED_ITEM_TOTAL = new BigDecimal("47.97");
    private static final BigDecimal TAX_RATE = new BigDecimal("0.08");
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
    @DisplayName("Step 2: Add 3 products to the cart: [Sauce Labs Backpack] ($29.99), [Sauce Labs Bike Light] ($9.99), [Sauce Labs Onesie] ($7.99)")
    public void addThreeProducts() {
        PRODUCTS.forEach(inventoryPage::addProductToCart);
    }

    @Test
    @Order(3)
    @DisplayName("Step 3: Confirm the cart badge shows [3]</br>" +
            "Expected Results: Cart badge shows [3]")
    public void verifyCartBadge() {
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
    @Order(4)
    @DisplayName("Step 4: Go to the Cart page, confirm all 3 products are displayed</br>" +
            "Expected Results: App navigates to https://www.saucedemo.com/cart.html, all 3 products are displayed")
    public void verifyCart() {
        header.clickCartIcon();
        final List<String> actualCartItems = cartPage.getCartItemNames();
        assertAll(
                () -> verifyCurrentUrl(BASE_URL + "cart.html"),
                () -> assertEquals(
                        PRODUCTS,
                        actualCartItems,
                        "The cart items are not as expected. Expected: ["
                                + PRODUCTS
                                + "], Actual: ["
                                + actualCartItems
                                + "]"
                )
        );
    }

    @Test
    @Order(5)
    @DisplayName("Step 5: Click [Checkout], enter valid information, click [Continue]</br>" +
            "Expected Results: App navigates to https://www.saucedemo.com/checkout-step-two.html")
    public void checkoutWithValidInformation() {
        cartPage.clickCheckout();
        checkoutInformationPage.fillInformationAndContinue("Bear", "Nguyen", "700000");
        verifyCurrentUrl(BASE_URL + "checkout-step-two.html");
    }

    @Test
    @Order(6)
    @DisplayName("Step 6: On the Overview page, compute the expected Item total = 29.99 + 9.99 + 7.99</br>" +
            "Expected Results: Sum of the item prices on the Overview page = [$47.97]")
    public void computeExpectedItemTotal() {
        final BigDecimal actualSumOfPrices = PRODUCTS.stream()
                .map(checkoutOverviewPage::getOverviewItemPriceText)
                .map(price -> new BigDecimal(price.replace("$", "")))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        assertEquals(
                EXPECTED_ITEM_TOTAL,
                actualSumOfPrices,
                "The sum of the item prices is not as expected. Expected: ["
                        + EXPECTED_ITEM_TOTAL
                        + "], Actual: ["
                        + actualSumOfPrices
                        + "]"
        );
    }

    @Test
    @Order(7)
    @DisplayName("Step 7: Compare against the Item total; verify Tax and Total = Item total + Tax</br>" +
            "Expected Results: Item total correctly shows [$47.97]; Total = Item total + Tax, calculated accurately (8% tax)")
    public void verifyTotals() {
        final BigDecimal actualItemTotal = checkoutOverviewPage.getSubtotalAmount();
        final BigDecimal actualTax = checkoutOverviewPage.getTaxAmount();
        final BigDecimal actualTotal = checkoutOverviewPage.getTotalAmount();
        final BigDecimal expectedTax = EXPECTED_ITEM_TOTAL.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
        final BigDecimal expectedTotal = EXPECTED_ITEM_TOTAL.add(expectedTax);
        assertAll(
                () -> assertEquals(
                        EXPECTED_ITEM_TOTAL,
                        actualItemTotal,
                        "The Item total is not as expected. Expected: ["
                                + EXPECTED_ITEM_TOTAL
                                + "], Actual: ["
                                + actualItemTotal
                                + "]"
                ),
                () -> assertEquals(
                        expectedTax,
                        actualTax,
                        "The Tax is not 8% of the Item total. Expected: ["
                                + expectedTax
                                + "], Actual: ["
                                + actualTax
                                + "]"
                ),
                () -> assertEquals(
                        expectedTotal,
                        actualTotal,
                        "The Total is not equal to Item total + Tax. Expected: ["
                                + expectedTotal
                                + "], Actual: ["
                                + actualTotal
                                + "]"
                )
        );
    }

    @Test
    @Order(8)
    @DisplayName("Step 8: Click [Finish]</br>" +
            "Expected Results: Order completes successfully on the Checkout Complete page")
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
