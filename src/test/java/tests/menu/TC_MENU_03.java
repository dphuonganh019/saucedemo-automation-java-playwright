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

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("TC_MENU_03: [About] menu link navigates to saucelabs.com")
public class TC_MENU_03 extends BaseTest {
    static LoginPage loginPage;
    static HeaderComponent header;
    static SideMenuComponent sideMenu;

    private static final String EXPECTED_ABOUT_HREF = "https://saucelabs.com/";

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

    // Không click sang saucelabs.com để test không rời khỏi domain đang test -> verify thuộc tính href của link
    @Test
    @Order(2)
    @DisplayName("Step 2: Verify the [About] link target (href) instead of clicking through to leave the domain under test</br>" +
            "Expected Results: [About] link points to [https://saucelabs.com/]")
    public void verifyAboutLinkHref() {
        final String actualHref = sideMenu.getAboutLinkHref();
        assertEquals(
                EXPECTED_ABOUT_HREF,
                actualHref,
                "The About link href is not as expected. Expected: ["
                        + EXPECTED_ABOUT_HREF
                        + "], Actual: ["
                        + actualHref
                        + "]"
        );
    }
}
