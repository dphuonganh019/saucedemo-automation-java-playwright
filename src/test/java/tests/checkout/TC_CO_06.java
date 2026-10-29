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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TC_CO_06: Checkout Step Two - detailed verification of the Overview information")
public class TC_CO_06 extends BaseTest {
    static LoginPage loginPage;
    static InventoryPage inventoryPage;
    static HeaderComponent header;
    static CartPage cartPage;
    static CheckoutInformationPage checkoutInformationPage;
    static CheckoutOverviewPage checkoutOverviewPage;

    private static final String PRODUCT_NAME = "Sauce Labs Backpack";
    private static final String EXPECTED_PRICE = "$29.99";
    private static final String EXPECTED_PAYMENT_INFO = "SauceCard #31337";
    private static final String EXPECTED_SHIPPING_INFO = "Free Pony Express Delivery!";
    private static final String EXPECTED_SUBTOTAL = "Item total: $29.99";
    private static final String EXPECTED_TAX = "Tax: $2.40";
    private static final String EXPECTED_TOTAL = "Total: $32.39";
    private static final BigDecimal TAX_RATE = new BigDecimal("0.08");

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
        inventoryPage.addProductToCart(PRODUCT_NAME);
        header.clickCartIcon();
        cartPage.clickCheckout();
        checkoutInformationPage.fillInformationAndContinue("Bear", "Nguyen", "700000");
    }

    @Test
    @Order(1)
    @DisplayName("Step 1: Verify [Sauce Labs Backpack] is displayed correctly in the item list</br>" +
            "Expected Results: Product name and price [$29.99] are displayed correctly in the overview list")
    public void verifyOverviewItem() {
        final List<String> actualItemNames = checkoutOverviewPage.getOverviewItemNames();
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
                () -> {
                    final String actualPrice = checkoutOverviewPage.getOverviewItemPriceText(PRODUCT_NAME);
                    assertEquals(
                            EXPECTED_PRICE,
                            actualPrice,
                            "The price of [" + PRODUCT_NAME + "] is not as expected. Expected: ["
                                    + EXPECTED_PRICE
                                    + "], Actual: ["
                                    + actualPrice
                                    + "]"
                    );
                }
        );
    }

    @Test
    @Order(2)
    @DisplayName("Step 2: Verify Payment Information displays the correct value</br>" +
            "Expected Results: Payment Information = [SauceCard #31337]")
    public void verifyPaymentInformation() {
        final String actualPaymentInfo = checkoutOverviewPage.getPaymentInformation();
        assertEquals(
                EXPECTED_PAYMENT_INFO,
                actualPaymentInfo,
                "The Payment Information is not as expected. Expected: ["
                        + EXPECTED_PAYMENT_INFO
                        + "], Actual: ["
                        + actualPaymentInfo
                        + "]"
        );
    }

    @Test
    @Order(3)
    @DisplayName("Step 3: Verify Shipping Information displays the correct value</br>" +
            "Expected Results: Shipping Information = [Free Pony Express Delivery!]")
    public void verifyShippingInformation() {
        final String actualShippingInfo = checkoutOverviewPage.getShippingInformation();
        assertEquals(
                EXPECTED_SHIPPING_INFO,
                actualShippingInfo,
                "The Shipping Information is not as expected. Expected: ["
                        + EXPECTED_SHIPPING_INFO
                        + "], Actual: ["
                        + actualShippingInfo
                        + "]"
        );
    }

    @Test
    @Order(4)
    @DisplayName("Step 4: Verify Item total displays the correct value</br>" +
            "Expected Results: Item total = [Item total: $29.99]")
    public void verifyItemTotal() {
        final String actualSubtotal = checkoutOverviewPage.getSubtotalText();
        assertEquals(
                EXPECTED_SUBTOTAL,
                actualSubtotal,
                "The Item total is not as expected. Expected: ["
                        + EXPECTED_SUBTOTAL
                        + "], Actual: ["
                        + actualSubtotal
                        + "]"
        );
    }

    @Test
    @Order(5)
    @DisplayName("Step 5: Verify Tax is calculated correctly (8% of Item total)</br>" +
            "Expected Results: Tax = [Tax: $2.40] (29.99 x 8% ~= 2.40)")
    public void verifyTax() {
        final String actualTaxText = checkoutOverviewPage.getTaxText();
        final BigDecimal expectedTaxAmount = checkoutOverviewPage.getSubtotalAmount()
                .multiply(TAX_RATE)
                .setScale(2, RoundingMode.HALF_UP);
        final BigDecimal actualTaxAmount = checkoutOverviewPage.getTaxAmount();
        assertAll(
                () -> assertEquals(
                        EXPECTED_TAX,
                        actualTaxText,
                        "The Tax is not as expected. Expected: ["
                                + EXPECTED_TAX
                                + "], Actual: ["
                                + actualTaxText
                                + "]"
                ),
                () -> assertEquals(
                        expectedTaxAmount,
                        actualTaxAmount,
                        "The Tax is not 8% of the Item total. Expected: ["
                                + expectedTaxAmount
                                + "], Actual: ["
                                + actualTaxAmount
                                + "]"
                )
        );
    }

    @Test
    @Order(6)
    @DisplayName("Step 6: Verify Total = Item total + Tax</br>" +
            "Expected Results: Total = [Total: $32.39] (29.99 + 2.40)")
    public void verifyTotal() {
        final String actualTotalText = checkoutOverviewPage.getTotalText();
        final BigDecimal expectedTotalAmount = checkoutOverviewPage.getSubtotalAmount()
                .add(checkoutOverviewPage.getTaxAmount());
        final BigDecimal actualTotalAmount = checkoutOverviewPage.getTotalAmount();
        assertAll(
                () -> assertEquals(
                        EXPECTED_TOTAL,
                        actualTotalText,
                        "The Total is not as expected. Expected: ["
                                + EXPECTED_TOTAL
                                + "], Actual: ["
                                + actualTotalText
                                + "]"
                ),
                () -> assertEquals(
                        expectedTotalAmount,
                        actualTotalAmount,
                        "The Total is not equal to Item total + Tax. Expected: ["
                                + expectedTotalAmount
                                + "], Actual: ["
                                + actualTotalAmount
                                + "]"
                )
        );
    }

    @Test
    @Order(7)
    @DisplayName("Step 7: Verify both the [Cancel] and [Finish] buttons are displayed and clickable</br>" +
            "Expected Results: Both buttons are displayed, not disabled")
    public void verifyCancelAndFinishButtons() {
        assertAll(
                () -> assertTrue(
                        checkoutOverviewPage.isCancelButtonVisible(),
                        "The Cancel button is not displayed as expected"
                ),
                () -> assertTrue(
                        checkoutOverviewPage.isCancelButtonEnabled(),
                        "The Cancel button is disabled"
                ),
                () -> assertTrue(
                        checkoutOverviewPage.isFinishButtonVisible(),
                        "The Finish button is not displayed as expected"
                ),
                () -> assertTrue(
                        checkoutOverviewPage.isFinishButtonEnabled(),
                        "The Finish button is disabled"
                )
        );
    }
}
