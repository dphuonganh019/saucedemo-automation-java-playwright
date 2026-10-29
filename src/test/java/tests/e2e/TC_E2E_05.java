package tests.e2e;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import pages.CartPage;
import pages.HeaderComponent;
import pages.InventoryPage;
import pages.LoginPage;
import pages.SideMenuComponent;
import tests.BaseTest;
import utils.ConfigReader;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TC_E2E_05: E2E - Add products then Reset App State, confirm the cart is cleared")
public class TC_E2E_05 extends BaseTest {
    static LoginPage loginPage;
    static InventoryPage inventoryPage;
    static HeaderComponent header;
    static CartPage cartPage;
    static SideMenuComponent sideMenu;

    private static final String BASE_URL = ConfigReader.get("baseUrl");
    private static final String EXPECTED_CART_BADGE = "2";
    private static final String ADD_TO_CART_BTN_TXT = "Add to cart";
    private static final int EXPECTED_ITEM_COUNT = 0;

    @BeforeAll
    static void setupPrecondition() {
        loginPage = new LoginPage(page());
        inventoryPage = new InventoryPage(page());
        header = new HeaderComponent(page());
        cartPage = new CartPage(page());
        sideMenu = new SideMenuComponent(page());
        loginPage.goToSauceDemoPage();
    }

    @Test
    @Order(1)
    @DisplayName("Step 1: Log in with standard_user/secret_sauce</br>" +
            "Expected Results: App navigates to https://www.saucedemo.com/inventory.html")
    public void login() {
        loginPage.loginAs(ConfigReader.get("username_standard_user"), ConfigReader.get("password"));
        final String expectedUrl = BASE_URL + "inventory.html";
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

    @Test
    @Order(2)
    @DisplayName("Step 2: Add any 2 products to the cart ([Sauce Labs Backpack], [Sauce Labs Bike Light])")
    public void addTwoProducts() {
        inventoryPage.addProductToCart("Sauce Labs Backpack");
        inventoryPage.addProductToCart("Sauce Labs Bike Light");
    }

    @Test
    @Order(3)
    @DisplayName("Step 3: Confirm the cart badge = [2]</br>" +
            "Expected Results: Cart badge shows [2]")
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
    @DisplayName("Step 4: Open the menu, click [Reset App State]")
    public void resetAppState() {
        header.clickMenuButton();
        sideMenu.clickResetAppState();
    }

    @Test
    @Order(5)
    @DisplayName("Step 5: Check the cart badge and the state of the Add to cart buttons on Inventory</br>" +
            "Expected Results:</br>" +
            "- The cart badge disappears;</br>" +
            "- All buttons revert to [Add to cart] (no longer [Remove]);</br>" +
            "- Revisiting the Cart page shows an empty product list")
    public void verifyAppStateReset() {
        final boolean isCartBadgeRemoved = header.isCartBadgeRemoved();
        final List<String> actualButtonTexts = inventoryPage.getAllProductButtonTexts();
        // Sidebar đang mở sẽ che header -> đóng menu trước khi click icon cart
        sideMenu.clickCloseButton();
        header.clickCartIcon();
        final int actualItemCount = cartPage.getCartItemCount();
        assertAll(
                () -> assertTrue(
                        isCartBadgeRemoved,
                        "The cart badge is still displayed after Reset App State"
                ),
                () -> assertTrue(
                        actualButtonTexts.stream().allMatch(ADD_TO_CART_BTN_TXT::equals),
                        "Not all product buttons reverted to [" + ADD_TO_CART_BTN_TXT + "]. Actual: ["
                                + actualButtonTexts
                                + "]"
                ),
                () -> assertEquals(
                        EXPECTED_ITEM_COUNT,
                        actualItemCount,
                        "The cart is not empty after Reset App State. Expected: ["
                                + EXPECTED_ITEM_COUNT
                                + "], Actual: ["
                                + actualItemCount
                                + "]"
                )
        );
    }
}
