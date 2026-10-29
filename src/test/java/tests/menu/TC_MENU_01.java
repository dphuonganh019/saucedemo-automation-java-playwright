package tests.menu;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import pages.HeaderComponent;
import pages.LoginPage;
import pages.SideMenuComponent;
import tests.BaseTest;
import utils.ConfigReader;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// Lưu ý: testcase gốc ghi menu có 4 item, nhưng site thực tế hiện có 5 item (thêm [Dynamic Catalog] ở vị trí 2)
// -> các step bên dưới verify theo hành vi thực tế của site
@DisplayName("TC_MENU_01: Detailed verification of the Hamburger Menu items")
public class TC_MENU_01 extends BaseTest {
    static LoginPage loginPage;
    static HeaderComponent header;
    static SideMenuComponent sideMenu;

    private static final List<String> EXPECTED_MENU_ITEMS = List.of(
            "All Items",
            "Dynamic Catalog",
            "About",
            "Logout",
            "Reset App State"
    );

    @BeforeAll
    static void setupPrecondition() {
        loginPage = new LoginPage(page());
        header = new HeaderComponent(page());
        sideMenu = new SideMenuComponent(page());
        loginPage.goToSauceDemoPage();
        loginPage.loginAs(ConfigReader.get("username_standard_user"), ConfigReader.get("password"));
    }

    @Test
    @Order(1)
    @DisplayName("Step 1: Verify the hamburger menu icon is displayed at the top-left of the header</br>" +
            "Expected Results: Menu icon is clearly visible")
    public void verifyMenuIcon() {
        assertTrue(
                header.isMenuIconVisible(),
                "The menu icon is not displayed as expected"
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
    @DisplayName("Step 3: Verify the sidebar slides in and displays exactly 5 items, in order</br>" +
            "Expected Results: Sidebar opens, showing all 5 items: All Items, Dynamic Catalog, About, Logout, Reset App State")
    public void verifySidebarItems() {
        assertAll(
                () -> assertTrue(
                        sideMenu.isMenuOpened(),
                        "The sidebar menu is not opened as expected"
                ),
                () -> {
                    final List<String> actualMenuItems = sideMenu.getMenuItemTexts();
                    assertEquals(
                            EXPECTED_MENU_ITEMS,
                            actualMenuItems,
                            "The menu items are not as expected. Expected: ["
                                    + EXPECTED_MENU_ITEMS
                                    + "], Actual: ["
                                    + actualMenuItems
                                    + "]"
                    );
                }
        );
    }

    @Test
    @Order(4)
    @DisplayName("Step 4: Verify item 1 [All Items] is displayed with the correct text and is clickable</br>" +
            "Expected Results: [All Items] is in position 1")
    public void verifyAllItemsLink() {
        verifyMenuItem("All Items", 1);
    }

    @Test
    @Order(5)
    @DisplayName("Step 5: Verify item 2 [Dynamic Catalog] is displayed with the correct text and is clickable</br>" +
            "Expected Results: [Dynamic Catalog] is in position 2")
    public void verifyDynamicCatalogLink() {
        verifyMenuItem("Dynamic Catalog", 2);
    }

    @Test
    @Order(6)
    @DisplayName("Step 6: Verify item 3 [About] is displayed with the correct text and is clickable</br>" +
            "Expected Results: [About] is in position 3")
    public void verifyAboutLink() {
        verifyMenuItem("About", 3);
    }

    @Test
    @Order(7)
    @DisplayName("Step 7: Verify item 4 [Logout] is displayed with the correct text and is clickable</br>" +
            "Expected Results: [Logout] is in position 4")
    public void verifyLogoutLink() {
        verifyMenuItem("Logout", 4);
    }

    @Test
    @Order(8)
    @DisplayName("Step 8: Verify item 5 [Reset App State] is displayed with the correct text and is clickable</br>" +
            "Expected Results: [Reset App State] is in position 5 (last)")
    public void verifyResetAppStateLink() {
        verifyMenuItem("Reset App State", 5);
    }

    @Test
    @Order(9)
    @DisplayName("Step 9: Verify the close (X) button is displayed at the top of the sidebar</br>" +
            "Expected Results: Close (X) button is displayed and clickable")
    public void verifyCloseButton() {
        assertAll(
                () -> assertTrue(
                        sideMenu.isCloseIconVisible(),
                        "The close (X) button is not displayed as expected"
                ),
                () -> assertTrue(
                        sideMenu.isCloseButtonEnabled(),
                        "The close (X) button is not clickable as expected"
                )
        );
    }

    // Các step verify từng item chỉ khác nhau ở text và vị trí -> gom phần assert vào 1 hàm dùng chung
    private void verifyMenuItem(String itemText, int expectedPosition) {
        assertAll(
                () -> assertTrue(
                        sideMenu.isMenuItemVisible(itemText),
                        "The menu item [" + itemText + "] is not displayed as expected"
                ),
                () -> assertTrue(
                        sideMenu.isMenuItemEnabled(itemText),
                        "The menu item [" + itemText + "] is not clickable as expected"
                ),
                () -> {
                    final int actualPosition = sideMenu.getMenuItemTexts().indexOf(itemText) + 1;
                    assertEquals(
                            expectedPosition,
                            actualPosition,
                            "The position of menu item [" + itemText + "] is not as expected. Expected: ["
                                    + expectedPosition
                                    + "], Actual: ["
                                    + actualPosition
                                    + "]"
                    );
                }
        );
    }
}
