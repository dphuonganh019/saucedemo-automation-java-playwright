package tests.menu;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import pages.HeaderComponent;
import pages.InventoryPage;
import pages.LoginPage;
import pages.SideMenuComponent;
import tests.BaseTest;
import utils.ConfigReader;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TC_MENU_05: [Reset App State] clears the cart and resets state")
public class TC_MENU_05 extends BaseTest {
    static LoginPage loginPage;
    static InventoryPage inventoryPage;
    static HeaderComponent header;
    static SideMenuComponent sideMenu;

    private static final String EXPECTED_CART_BADGE = "2";
    private static final String ADD_TO_CART_BTN_TXT = "Add to cart";
    private static final String EXPECTED_URL = ConfigReader.get("baseUrl") + "inventory.html";

    @BeforeAll
    static void setupPrecondition() {
        loginPage = new LoginPage(page());
        inventoryPage = new InventoryPage(page());
        header = new HeaderComponent(page());
        sideMenu = new SideMenuComponent(page());
        loginPage.goToSauceDemoPage();
        loginPage.loginAs(ConfigReader.get("username_standard_user"), ConfigReader.get("password"));
        inventoryPage.addProductToCart("Sauce Labs Backpack");
        inventoryPage.addProductToCart("Sauce Labs Bike Light");
    }

    @Test
    @Order(1)
    @DisplayName("Step 1: Confirm the cart badge currently shows [2]</br>" +
            "Expected Results: Cart badge shows [2]")
    public void verifyCartBadgeBeforeReset() {
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
    @Order(2)
    @DisplayName("Step 2: Click the menu icon")
    public void clickMenuIcon() {
        header.clickMenuButton();
    }

    @Test
    @Order(3)
    @DisplayName("Step 3: Click [Reset App State]")
    public void clickResetAppState() {
        sideMenu.clickResetAppState();
    }

    @Test
    @Order(4)
    @DisplayName("Step 4: Verify the cart badge and the state of the Add to cart buttons</br>" +
            "Expected Results:</br>" +
            "- The cart badge disappears (no longer exists in the DOM);</br>" +
            "- All [Add to cart] buttons return to their initial state (no longer showing [Remove]);</br>" +
            "- The page stays on Inventory, user is not logged out")
    public void verifyAppStateReset() {
        assertAll(
                () -> assertTrue(
                        header.isCartBadgeRemoved(),
                        "The cart badge is still displayed after Reset App State"
                ),
                () -> {
                    final List<String> actualButtonTexts = inventoryPage.getAllProductButtonTexts();
                    assertTrue(
                            actualButtonTexts.stream().allMatch(ADD_TO_CART_BTN_TXT::equals),
                            "Not all product buttons returned to [" + ADD_TO_CART_BTN_TXT + "]. Actual: ["
                                    + actualButtonTexts
                                    + "]"
                    );
                },
                () -> {
                    final String actualUrl = browserManager().getCurrentTabUrl();
                    assertEquals(
                            EXPECTED_URL,
                            actualUrl,
                            "The page did not stay on Inventory. Expected: ["
                                    + EXPECTED_URL
                                    + "], Actual: ["
                                    + actualUrl
                                    + "]"
                    );
                }
        );
    }
}
