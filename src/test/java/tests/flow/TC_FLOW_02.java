package tests.flow;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import pages.HeaderComponent;
import pages.InventoryPage;
import utils.ConfigReader;

import static org.junit.jupiter.api.Assertions.*;

@Order(2)
@DisplayName("TC_FLOW_02: User Journey - Phase 2: Reuse the session in a new browser and add products to the cart")
public class TC_FLOW_02 extends FlowBaseTest {
    static InventoryPage inventoryPage;
    static HeaderComponent header;

    private static final String BASE_URL = ConfigReader.get("baseUrl");
    private static final String BACKPACK = "Sauce Labs Backpack";
    private static final String BIKE_LIGHT = "Sauce Labs Bike Light";
    private static final String EXPECTED_PAGE_TITLE = "Products";
    private static final String EXPECTED_CART_BADGE = "2";
    private static final String REMOVE_BTN_TXT = "Remove";
    private static final String CART_STORAGE_KEY = "cart-contents";

    @BeforeAll
    static void setupPrecondition() {
        continueFromPhase(1);
        inventoryPage = new InventoryPage(page());
        header = new HeaderComponent(page());
    }

    @Test
    @Order(1)
    @DisplayName("Step 1: Open https://www.saucedemo.com/inventory.html directly in a new browser, without logging in</br>" +
            "Expected Results: The Inventory page is displayed (the session from Phase 1 is reused), page title is [Products]")
    public void openInventoryWithSavedSession() {
        inventoryPage.goToInventoryPageByUrl();
        final String actualPageTitle = header.getPageTitle();
        assertAll(
                () -> verifyCurrentUrl(BASE_URL + "inventory.html"),
                () -> assertEquals(
                        EXPECTED_PAGE_TITLE,
                        actualPageTitle,
                        "The page title is not as expected, the session was not reused. Expected: ["
                                + EXPECTED_PAGE_TITLE
                                + "], Actual: ["
                                + actualPageTitle
                                + "]"
                )
        );
    }

    @Test
    @Order(2)
    @DisplayName("Step 2: Add [Sauce Labs Backpack] and [Sauce Labs Bike Light] to the cart</br>" +
            "Expected Results: Cart badge shows [2], the buttons of both products change to [Remove]")
    public void addTwoProducts() {
        inventoryPage.addProductToCart(BACKPACK);
        inventoryPage.addProductToCart(BIKE_LIGHT);
        final String actualCartBadge = header.getCartBadgeText();
        final String actualBackpackButton = inventoryPage.getProductButtonText(BACKPACK);
        final String actualBikeLightButton = inventoryPage.getProductButtonText(BIKE_LIGHT);
        assertAll(
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
                        REMOVE_BTN_TXT,
                        actualBackpackButton,
                        "The button of [" + BACKPACK + "] is not as expected. Expected: ["
                                + REMOVE_BTN_TXT
                                + "], Actual: ["
                                + actualBackpackButton
                                + "]"
                ),
                () -> assertEquals(
                        REMOVE_BTN_TXT,
                        actualBikeLightButton,
                        "The button of [" + BIKE_LIGHT + "] is not as expected. Expected: ["
                                + REMOVE_BTN_TXT
                                + "], Actual: ["
                                + actualBikeLightButton
                                + "]"
                )
        );
    }

    @Test
    @Order(3)
    @DisplayName("Step 3: Save the browser session for Phase 3</br>" +
            "Expected Results: The saved session contains the cart data [cart-contents] in local storage")
    public void saveSessionWithCart() {
        final String savedSession = saveSessionForNextPhase(2);
        assertTrue(
                savedSession.contains(CART_STORAGE_KEY),
                "The saved session does not contain the cart data [" + CART_STORAGE_KEY + "]"
        );
    }
}
