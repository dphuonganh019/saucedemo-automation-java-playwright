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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("TC_MENU_02: [All Items] menu link navigates back to the Inventory page")
public class TC_MENU_02 extends BaseTest {
    static LoginPage loginPage;
    static InventoryPage inventoryPage;
    static HeaderComponent header;
    static SideMenuComponent sideMenu;

    private static final String EXPECTED_URL = ConfigReader.get("baseUrl") + "inventory.html";
    private static final int EXPECTED_PRODUCT_COUNT = 6;

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
    @DisplayName("Step 1: Navigate to a page other than Inventory while logged in (cart.html)")
    public void navigateToCartPage() {
        header.clickCartIcon();
    }

    @Test
    @Order(2)
    @DisplayName("Step 2: Click the menu icon")
    public void clickMenuIcon() {
        header.clickMenuButton();
    }

    @Test
    @Order(3)
    @DisplayName("Step 3: Click [All Items]")
    public void clickAllItems() {
        sideMenu.clickAllItems();
    }

    @Test
    @Order(4)
    @DisplayName("Expected Results:</br>" +
            "- System navigates to https://www.saucedemo.com/inventory.html;</br>" +
            "- All 6 products are displayed")
    public void verifyBackToInventory() {
        final int actualProductCount = inventoryPage.getProductCount();
        assertAll(
                () -> {
                    final String actualUrl = browserManager().getCurrentTabUrl();
                    assertEquals(
                            EXPECTED_URL,
                            actualUrl,
                            "The system did not redirect to the expected URL. Expected: ["
                                    + EXPECTED_URL
                                    + "], Actual: ["
                                    + actualUrl
                                    + "]"
                    );
                },
                () -> assertEquals(
                        EXPECTED_PRODUCT_COUNT,
                        actualProductCount,
                        "The number of products is not as expected. Expected: ["
                                + EXPECTED_PRODUCT_COUNT
                                + "], Actual: ["
                                + actualProductCount
                                + "]"
                )
        );
    }
}
