package tests.flow;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import pages.HeaderComponent;
import pages.InventoryPage;
import pages.LoginPage;
import pages.SideMenuComponent;
import utils.ConfigReader;

import static org.junit.jupiter.api.Assertions.*;

@Order(5)
@DisplayName("TC_FLOW_05: User Journey - Phase 5: Log out ends the session for later visits")
public class TC_FLOW_05 extends FlowBaseTest {
    static LoginPage loginPage;
    static InventoryPage inventoryPage;
    static HeaderComponent header;
    static SideMenuComponent sideMenu;

    private static final String BASE_URL = ConfigReader.get("baseUrl");
    private static final String EXPECTED_PAGE_TITLE = "Products";
    private static final String EXPECTED_ERROR_MESSAGE =
            "Epic sadface: You can only access '/inventory.html' when you are logged in.";

    @BeforeAll
    static void setupPrecondition() {
        continueFromPhase(4);
        loginPage = new LoginPage(page());
        inventoryPage = new InventoryPage(page());
        header = new HeaderComponent(page());
        sideMenu = new SideMenuComponent(page());
    }

    @Test
    @Order(1)
    @DisplayName("Step 1: Open https://www.saucedemo.com/inventory.html directly in a new browser</br>" +
            "Expected Results:</br>" +
            "- The user is still logged in, page title is [Products];</br>" +
            "- The cart is empty after the order completed in Phase 4 (no cart badge)")
    public void openInventoryAfterOrder() {
        inventoryPage.goToInventoryPageByUrl();
        final String actualPageTitle = header.getPageTitle();
        final boolean isCartBadgeRemoved = header.isCartBadgeRemoved();
        assertAll(
                () -> assertEquals(
                        EXPECTED_PAGE_TITLE,
                        actualPageTitle,
                        "The page title is not as expected, the session was not reused. Expected: ["
                                + EXPECTED_PAGE_TITLE
                                + "], Actual: ["
                                + actualPageTitle
                                + "]"
                ),
                () -> assertTrue(
                        isCartBadgeRemoved,
                        "The cart badge is still displayed although the order was completed"
                )
        );
    }

    @Test
    @Order(2)
    @DisplayName("Step 2: Open the menu, click [Logout]</br>" +
            "Expected Results: Navigates to the login page https://www.saucedemo.com/")
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

    @Test
    @Order(3)
    @DisplayName("Step 3: Navigate directly to https://www.saucedemo.com/inventory.html again</br>" +
            "Expected Results: Access is denied, error message " +
            "[Epic sadface: You can only access '/inventory.html' when you are logged in.] is displayed")
    public void verifyAccessDeniedAfterLogout() {
        inventoryPage.goToInventoryPageByUrl();
        final String actualErrorMessage = loginPage.getErrorMessageContent();
        assertAll(
                () -> assertTrue(
                        loginPage.isLoginButtonVisible(),
                        "The login page is not displayed, the user can still access the internal page"
                ),
                () -> assertEquals(
                        EXPECTED_ERROR_MESSAGE,
                        actualErrorMessage,
                        "The actual error message is not as expected. Expected: ["
                                + EXPECTED_ERROR_MESSAGE
                                + "], Actual: ["
                                + actualErrorMessage
                                + "]"
                )
        );
    }
}
