package tests.e2e;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import pages.InventoryPage;
import pages.LoginPage;
import tests.BaseTest;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TC_E2E_07: E2E - Directly accessing an internal page URL while logged out is denied")
public class TC_E2E_07 extends BaseTest {
    static LoginPage loginPage;
    static InventoryPage inventoryPage;

    private static final String EXPECTED_ERROR_MESSAGE =
            "Epic sadface: You can only access '/inventory.html' when you are logged in.";

    @BeforeAll
    static void setup() {
        loginPage = new LoginPage(page());
        inventoryPage = new InventoryPage(page());
    }

    @Test
    @Order(1)
    @DisplayName("Step 1: Navigate directly to https://www.saucedemo.com/inventory.html (bypassing the login screen)")
    public void navigateDirectlyToInventory() {
        inventoryPage.goToInventoryPageByUrl();
    }

    @Test
    @Order(2)
    @DisplayName("Expected Results:</br>" +
            "- Access is denied; user remains on the logged-out page;</br>" +
            "- Error message [Epic sadface: You can only access '/inventory.html' when you are logged in.] is displayed")
    public void verifyAccessDenied() {
        assertAll(
                () -> assertTrue(
                        loginPage.isLoginButtonVisible(),
                        "The login page is not displayed, user was able to access the internal page"
                ),
                () -> {
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
        );
    }
}
