package tests.flow;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import pages.CartPage;
import pages.CheckoutCompletePage;
import pages.CheckoutInformationPage;
import pages.CheckoutOverviewPage;
import pages.HeaderComponent;
import utils.ConfigReader;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Order(4)
@DisplayName("TC_FLOW_04: User Journey - Phase 4: Check out the cart kept from the previous phases")
public class TC_FLOW_04 extends FlowBaseTest {
    static CartPage cartPage;
    static HeaderComponent header;
    static CheckoutInformationPage checkoutInformationPage;
    static CheckoutOverviewPage checkoutOverviewPage;
    static CheckoutCompletePage checkoutCompletePage;

    private static final String BASE_URL = ConfigReader.get("baseUrl");
    private static final List<String> EXPECTED_ITEM_NAMES = List.of("Sauce Labs Backpack", "Sauce Labs Bike Light");
    private static final String EXPECTED_SUBTOTAL = "Item total: $39.98";
    private static final String EXPECTED_TAX = "Tax: $3.20";
    private static final String EXPECTED_TOTAL = "Total: $43.18";
    private static final String EXPECTED_COMPLETE_HEADER = "Thank you for your order!";
    private static final String SESSION_COOKIE = "session-username";

    @BeforeAll
    static void setupPrecondition() {
        continueFromPhase(3);
        cartPage = new CartPage(page());
        header = new HeaderComponent(page());
        checkoutInformationPage = new CheckoutInformationPage(page());
        checkoutOverviewPage = new CheckoutOverviewPage(page());
        checkoutCompletePage = new CheckoutCompletePage(page());
    }

    @Test
    @Order(1)
    @DisplayName("Step 1: Open the Cart page, click [Checkout], enter valid information, click [Continue]</br>" +
            "Expected Results: App navigates to https://www.saucedemo.com/checkout-step-two.html, " +
            "the Overview lists [Sauce Labs Backpack] and [Sauce Labs Bike Light]")
    public void checkoutWithValidInformation() {
        cartPage.goToCartPageByUrl();
        cartPage.clickCheckout();
        checkoutInformationPage.fillInformationAndContinue("Bear", "Nguyen", "700000");
        final List<String> actualItemNames = checkoutOverviewPage.getOverviewItemNames();
        assertAll(
                () -> verifyCurrentUrl(BASE_URL + "checkout-step-two.html"),
                () -> assertEquals(
                        EXPECTED_ITEM_NAMES,
                        actualItemNames,
                        "The overview items are not as expected. Expected: ["
                                + EXPECTED_ITEM_NAMES
                                + "], Actual: ["
                                + actualItemNames
                                + "]"
                )
        );
    }

    @Test
    @Order(2)
    @DisplayName("Step 2: Verify the order amounts on the Overview page</br>" +
            "Expected Results: [Item total: $39.98], [Tax: $3.20], [Total: $43.18]")
    public void verifyOrderAmounts() {
        final String actualSubtotal = checkoutOverviewPage.getSubtotalText();
        final String actualTax = checkoutOverviewPage.getTaxText();
        final String actualTotal = checkoutOverviewPage.getTotalText();
        assertAll(
                () -> assertEquals(
                        EXPECTED_SUBTOTAL,
                        actualSubtotal,
                        "The Item total is not as expected. Expected: ["
                                + EXPECTED_SUBTOTAL
                                + "], Actual: ["
                                + actualSubtotal
                                + "]"
                ),
                () -> assertEquals(
                        EXPECTED_TAX,
                        actualTax,
                        "The Tax is not as expected. Expected: ["
                                + EXPECTED_TAX
                                + "], Actual: ["
                                + actualTax
                                + "]"
                ),
                () -> assertEquals(
                        EXPECTED_TOTAL,
                        actualTotal,
                        "The Total is not as expected. Expected: ["
                                + EXPECTED_TOTAL
                                + "], Actual: ["
                                + actualTotal
                                + "]"
                )
        );
    }

    @Test
    @Order(3)
    @DisplayName("Step 3: Click [Finish]</br>" +
            "Expected Results: Checkout Complete page shows [Thank you for your order!], the cart badge disappears")
    public void finishOrder() {
        checkoutOverviewPage.clickFinish();
        final String actualCompleteHeader = checkoutCompletePage.getCompleteHeader();
        final boolean isCartBadgeRemoved = header.isCartBadgeRemoved();
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
                        isCartBadgeRemoved,
                        "The cart badge is still displayed after the order is completed"
                )
        );
    }

    @Test
    @Order(4)
    @DisplayName("Step 4: Save the browser session for Phase 5</br>" +
            "Expected Results: The saved session still contains the login cookie [session-username]")
    public void saveSessionAfterOrder() {
        final String savedSession = saveSessionForNextPhase(4);
        assertTrue(
                savedSession.contains(SESSION_COOKIE),
                "The saved session does not contain the login cookie [" + SESSION_COOKIE + "]"
        );
    }
}
