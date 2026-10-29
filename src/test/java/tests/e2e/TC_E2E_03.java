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

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TC_E2E_03: E2E - Add then remove a product, cart returns to empty, then log out")
public class TC_E2E_03 extends BaseTest {
    static LoginPage loginPage;
    static InventoryPage inventoryPage;
    static HeaderComponent header;
    static CartPage cartPage;
    static SideMenuComponent sideMenu;

    private static final String BASE_URL = ConfigReader.get("baseUrl");
    private static final String PRODUCT_NAME = "Sauce Labs Backpack";
    private static final String EXPECTED_CART_BADGE = "1";
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
        verifyCurrentUrl(BASE_URL + "inventory.html");
    }

    @Test
    @Order(2)
    @DisplayName("Step 2: Add [Sauce Labs Backpack] to the cart from the Inventory page</br>" +
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
    @DisplayName("Step 3: On the Cart page, remove [Sauce Labs Backpack]")
    public void removeProductOnCartPage() {
        header.clickCartIcon();
        cartPage.removeProduct(PRODUCT_NAME);
    }

    @Test
    @Order(4)
    @DisplayName("Step 4: Confirm the cart is empty (no badge, product card count in cart = 0)</br>" +
            "Expected Results: Cart is completely empty")
    public void verifyCartEmpty() {
        assertAll(
                () -> assertTrue(
                        header.isCartBadgeRemoved(),
                        "The cart badge is still displayed although the cart is empty"
                ),
                () -> {
                    final int actualItemCount = cartPage.getCartItemCount();
                    assertEquals(
                            EXPECTED_ITEM_COUNT,
                            actualItemCount,
                            "The number of items in cart is not as expected. Expected: ["
                                    + EXPECTED_ITEM_COUNT
                                    + "], Actual: ["
                                    + actualItemCount
                                    + "]"
                    );
                }
        );
    }

    @Test
    @Order(5)
    @DisplayName("Step 5: Open the menu, click [Logout]</br>" +
            "Expected Results: Navigates to the login page https://www.saucedemo.com/; session ends")
    public void logout() {
        header.clickMenuButton();
        sideMenu.clickLogout();
        assertAll(
                () -> assertTrue(
                        loginPage.isLoginButtonVisible(),
                        "The login page is not displayed after logging out"
                ),
                () -> verifyCurrentUrl(BASE_URL)
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
