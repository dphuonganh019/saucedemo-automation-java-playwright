package tests.flow;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import pages.CartPage;
import pages.HeaderComponent;
import utils.ConfigReader;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Order(3)
@DisplayName("TC_FLOW_03: User Journey - Phase 3: The cart is kept after the browser is restarted")
public class TC_FLOW_03 extends FlowBaseTest {
    static CartPage cartPage;
    static HeaderComponent header;

    private static final String BASE_URL = ConfigReader.get("baseUrl");
    private static final String BACKPACK = "Sauce Labs Backpack";
    private static final String BIKE_LIGHT = "Sauce Labs Bike Light";
    private static final List<String> EXPECTED_ITEM_NAMES = List.of(BACKPACK, BIKE_LIGHT);
    private static final String EXPECTED_BACKPACK_PRICE = "$29.99";
    private static final String EXPECTED_BIKE_LIGHT_PRICE = "$9.99";
    private static final String EXPECTED_CART_BADGE = "2";
    private static final String CART_STORAGE_KEY = "cart-contents";

    @BeforeAll
    static void setupPrecondition() {
        continueFromPhase(2);
        cartPage = new CartPage(page());
        header = new HeaderComponent(page());
    }

    @Test
    @Order(1)
    @DisplayName("Step 1: Open https://www.saucedemo.com/cart.html directly in a new browser</br>" +
            "Expected Results:</br>" +
            "- Cart badge shows [2];</br>" +
            "- The cart still contains [Sauce Labs Backpack] ($29.99) and [Sauce Labs Bike Light] ($9.99) added in Phase 2")
    public void openCartWithSavedSession() {
        cartPage.goToCartPageByUrl();
        final List<String> actualItemNames = cartPage.getCartItemNames();
        final String actualBackpackPrice = cartPage.getCartItemPriceText(BACKPACK);
        final String actualBikeLightPrice = cartPage.getCartItemPriceText(BIKE_LIGHT);
        final String actualCartBadge = header.getCartBadgeText();
        assertAll(
                () -> verifyCurrentUrl(BASE_URL + "cart.html"),
                () -> assertEquals(
                        EXPECTED_CART_BADGE,
                        actualCartBadge,
                        "The cart badge is not as expected. Expected: ["
                                + EXPECTED_CART_BADGE
                                + "], Actual: ["
                                + actualCartBadge
                                + "]"
                ),
                () -> assertEquals(
                        EXPECTED_ITEM_NAMES,
                        actualItemNames,
                        "The products in cart are not as expected. Expected: ["
                                + EXPECTED_ITEM_NAMES
                                + "], Actual: ["
                                + actualItemNames
                                + "]"
                ),
                () -> assertEquals(
                        EXPECTED_BACKPACK_PRICE,
                        actualBackpackPrice,
                        "The price of [" + BACKPACK + "] is not as expected. Expected: ["
                                + EXPECTED_BACKPACK_PRICE
                                + "], Actual: ["
                                + actualBackpackPrice
                                + "]"
                ),
                () -> assertEquals(
                        EXPECTED_BIKE_LIGHT_PRICE,
                        actualBikeLightPrice,
                        "The price of [" + BIKE_LIGHT + "] is not as expected. Expected: ["
                                + EXPECTED_BIKE_LIGHT_PRICE
                                + "], Actual: ["
                                + actualBikeLightPrice
                                + "]"
                )
        );
    }

    @Test
    @Order(2)
    @DisplayName("Step 2: Save the browser session for Phase 4</br>" +
            "Expected Results: The saved session still contains the cart data [cart-contents]")
    public void saveSessionWithCart() {
        final String savedSession = saveSessionForNextPhase(3);
        assertTrue(
                savedSession.contains(CART_STORAGE_KEY),
                "The saved session does not contain the cart data [" + CART_STORAGE_KEY + "]"
        );
    }
}
