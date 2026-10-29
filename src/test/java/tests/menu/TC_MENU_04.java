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

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TC_MENU_04: [Logout] menu link logs the user out successfully")
public class TC_MENU_04 extends BaseTest {
    static LoginPage loginPage;
    static InventoryPage inventoryPage;
    static HeaderComponent header;
    static SideMenuComponent sideMenu;

    private static final String EXPECTED_URL_AFTER_LOGOUT = ConfigReader.get("baseUrl");
    private static final String EXPECTED_ERROR_MESSAGE =
            "Epic sadface: You can only access '/inventory.html' when you are logged in.";

    @BeforeAll
    static void setupPrecondition() {
        loginPage = new LoginPage(page());
        inventoryPage = new InventoryPage(page());
        header = new HeaderComponent(page());
        sideMenu = new SideMenuComponent(page());
        loginPage.goToSauceDemoPage();
        loginPage.loginAs(ConfigReader.get("username_standard_user"), ConfigReader.get("password"));
    }

    @Test
    @Order(1)
    @DisplayName("Step 1: Click the menu icon")
    public void clickMenuIcon() {
        header.clickMenuButton();
    }

    @Test
    @Order(2)
    @DisplayName("Step 2: Click [Logout]")
    public void clickLogout() {
        sideMenu.clickLogout();
    }

    @Test
    @Order(3)
    @DisplayName("Expected Results:</br>" +
            "- System navigates to https://www.saucedemo.com/ (login page);</br>" +
            "- Username and Password fields are empty")
    public void verifyLoggedOut() {
        assertAll(
                () -> assertTrue(
                        loginPage.isLoginButtonVisible(),
                        "The login page is not displayed after logging out"
                ),
                () -> {
                    final String actualUrl = browserManager().getCurrentTabUrl();
                    assertEquals(
                            EXPECTED_URL_AFTER_LOGOUT,
                            actualUrl,
                            "The system did not redirect to the expected URL. Expected: ["
                                    + EXPECTED_URL_AFTER_LOGOUT
                                    + "], Actual: ["
                                    + actualUrl
                                    + "]"
                    );
                },
                () -> assertTrue(
                        loginPage.getUsernameInputValue().isEmpty(),
                        "The Username field is not empty after logging out"
                ),
                () -> assertTrue(
                        loginPage.getPasswordInputValue().isEmpty(),
                        "The Password field is not empty after logging out"
                )
        );
    }

    @Test
    @Order(4)
    @DisplayName("Step 3: Attempt to navigate directly to https://www.saucedemo.com/inventory.html")
    public void navigateDirectlyToInventory() {
        inventoryPage.goToInventoryPageByUrl();
    }

    @Test
    @Order(5)
    @DisplayName("Expected Result: Access is denied; error [Epic sadface: You can only access '/inventory.html' when you are logged in.] is shown")
    public void verifyAccessDenied() {
        final String actualErrorMessage = loginPage.getErrorMessageContent();
        assertEquals(
                EXPECTED_ERROR_MESSAGE,
                actualErrorMessage,
                "The actual error message is not as expected. Expected: ["
                        + EXPECTED_ERROR_MESSAGE
                        + "], Actual: ["
                        + actualErrorMessage
                        + "]"
        );
    }
}
