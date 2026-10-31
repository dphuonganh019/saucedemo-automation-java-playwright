package tests.flow;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import pages.HeaderComponent;
import pages.LoginPage;
import utils.ConfigReader;

import static org.junit.jupiter.api.Assertions.*;

@Order(1)
@DisplayName("TC_FLOW_01: User Journey - Phase 1: Log in and save the authenticated session")
public class TC_FLOW_01 extends FlowBaseTest {
    static LoginPage loginPage;
    static HeaderComponent header;

    private static final String BASE_URL = ConfigReader.get("baseUrl");
    private static final String EXPECTED_PAGE_TITLE = "Products";
    private static final String SESSION_COOKIE = "session-username";

    @BeforeAll
    static void setupPrecondition() {
        startJourney();
        loginPage = new LoginPage(page());
        header = new HeaderComponent(page());
        loginPage.goToSauceDemoPage();
    }

    @Test
    @Order(1)
    @DisplayName("Step 1: Log in with standard_user/secret_sauce</br>" +
            "Expected Results: App navigates to https://www.saucedemo.com/inventory.html, page title is [Products]")
    public void login() {
        loginPage.loginAs(ConfigReader.get("username_standard_user"), ConfigReader.get("password"));
        final String actualPageTitle = header.getPageTitle();
        assertAll(
                () -> verifyCurrentUrl(BASE_URL + "inventory.html"),
                () -> assertEquals(
                        EXPECTED_PAGE_TITLE,
                        actualPageTitle,
                        "The page title is not as expected. Expected: ["
                                + EXPECTED_PAGE_TITLE
                                + "], Actual: ["
                                + actualPageTitle
                                + "]"
                )
        );
    }

    @Test
    @Order(2)
    @DisplayName("Step 2: Save the browser session (cookies + local storage) for Phase 2</br>" +
            "Expected Results: The saved session contains the login cookie [session-username]")
    public void saveLoggedInSession() {
        final String savedSession = saveSessionForNextPhase(1);
        assertTrue(
                savedSession.contains(SESSION_COOKIE),
                "The saved session does not contain the login cookie [" + SESSION_COOKIE + "]"
        );
    }
}
