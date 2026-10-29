package tests.login;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import pages.LoginPage;
import tests.BaseTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("TC_LOGIN_04: Login fails - both Username and Password empty")
public class TC_LOGIN_04 extends BaseTest {
    static LoginPage loginPage;

    private static final String EXPECTED_ERROR_MESSAGE = "Epic sadface: Username is required";

    @BeforeAll
    static void setup() {
        loginPage = new LoginPage(page());
    }

    @Test
    @Order(1)
    @DisplayName("Step 1: Navigate to https://www.saucedemo.com/")
    public void navigateToSauceDemo() {
        loginPage.goToSauceDemoPage();
    }

    @Test
    @Order(2)
    @DisplayName("Step 2: Leave both Username and Password empty")
    public void emptyUsernameAndPassword() {
        loginPage.fillUsername(StringUtils.EMPTY);
        loginPage.fillPassword(StringUtils.EMPTY);
    }

    @Test
    @Order(3)
    @DisplayName("Step 3: Click the Login button")
    public void clickLoginButton() {
        loginPage.clickLoginButton();
    }

    @Test
    @Order(4)
    @DisplayName("Expected Result: Error message [Epic sadface: Username is required] is displayed (Username is validated first)")
    public void verifyUsernameValidatedFirst() {
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
