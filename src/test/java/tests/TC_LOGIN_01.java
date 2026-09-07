package tests;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import pages.LoginPage;
import utils.ConfigReader;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("TC_LOGIN_01: Successful login with standard_user")
public class TC_LOGIN_01 extends BaseTest {
    static LoginPage loginPage;

    private final static String EXPECTED_URL_AFTER_LOGIN = ConfigReader.get("baseUrl") + "inventory.html";

    @BeforeAll
    static void setup() {
        loginPage = new LoginPage(page);
    }

    @Test
    @Order(1)
    @DisplayName("Step 1: Navigate to https://www.saucedemo.com/")
    public void navigateToSauceDemo() {
        loginPage.goToSauceDemoPage();
    }

    @Test
    @Order(2)
    @DisplayName("Step 2: Enter 'standard_user' into the Username field")
    public void enterTheUsername() {
        loginPage.fillUsername(ConfigReader.get("username_standard_user"));
    }

    @Test
    @Order(3)
    @DisplayName("Step 3: Enter value into the Password field")
    public void enterThePassword() {
        loginPage.fillPassword(ConfigReader.get("password"));
    }

    @Test
    @Order(4)
    @DisplayName("Step 4: Click the Login button")
    public void clickLoginButton() {
        loginPage.clickLoginButton();
    }

    @Test
    @Order(5)
    @DisplayName("Expected Result: System redirects to https://www.saucedemo.com/inventory.html")
    public void verifyLoginSuccessfully() {
        final String actualUrl = browserManager.getCurrentTabUrl();
        assertEquals(
                EXPECTED_URL_AFTER_LOGIN,
                actualUrl,
                "The system did not redirect to the expected URL. Expected: ["
                        + EXPECTED_URL_AFTER_LOGIN
                        + "], Actual: ["
                        +  actualUrl
                        + "]"
        );
    }


}
