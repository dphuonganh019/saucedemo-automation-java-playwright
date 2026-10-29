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

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("TC_MENU_06: Close the Hamburger Menu using the X button")
public class TC_MENU_06 extends BaseTest {
    static LoginPage loginPage;
    static HeaderComponent header;
    static SideMenuComponent sideMenu;

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
    @DisplayName("Step 1: Click the menu icon")
    public void clickMenuIcon() {
        header.clickMenuButton();
    }

    @Test
    @Order(2)
    @DisplayName("Step 2: Confirm the sidebar is open</br>" +
            "Expected Results: Sidebar menu is opened")
    public void verifySidebarOpened() {
        assertTrue(
                sideMenu.isMenuOpened(),
                "The sidebar menu is not opened as expected"
        );
    }

    @Test
    @Order(3)
    @DisplayName("Step 3: Click the close (X) button")
    public void clickCloseButton() {
        sideMenu.clickCloseButton();
    }

    @Test
    @Order(4)
    @DisplayName("Expected Result: Sidebar menu closes and is no longer visible on screen")
    public void verifySidebarClosed() {
        assertTrue(
                sideMenu.isMenuClosed(),
                "The sidebar menu is still visible after clicking the close (X) button"
        );
    }
}
