package tests;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import pages.LoginPage;
import utils.ConfigReader;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("TC_LOGIN_02: Login fails - empty Username")
public class TC_LOGIN_02 extends BaseTest {
    static LoginPage loginPage;

    private static final String EXPECTED_URL = ConfigReader.get("baseUrl");
    private static final String EXPECTED_ERROR_MESSAGE = "Epic sadface: Username is required";
    private static final String EXPECTED_COLOR_CODE = "rgb(226, 35, 26)";

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
    @DisplayName("Step 2: Leave Username empty")
    public void emptyTheUsername() {
        loginPage.fillUsername(StringUtils.EMPTY);
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
    @DisplayName("Expected Results:</br>" +
            "- URL stays unchanged (still https://www.saucedemo.com/);</br>" +
            "- Error message [Epic sadface: Username is required] is displayed;</br>" +
            "- Username and Password fields show a red border (rgb(226, 35, 26))")
    public void verifyLoginFailureResults() {
        assertAll(
                () -> {
                    final String actualUrl = browserManager.getCurrentTabUrl();
                    assertEquals(
                            EXPECTED_URL,
                            actualUrl,
                            "The current URL is not as expected. Expected: ["
                                    + EXPECTED_URL
                                    + "], Actual: ["
                                    + actualUrl
                                    + "]"
                    );
                },
                () -> {
                    final String actualErrorMessage = loginPage.getErrorMessageContent();
                    assertEquals(
                            EXPECTED_ERROR_MESSAGE,
                            actualErrorMessage,
                            "The actual error message is not as expected. Expected: ["
                                    + EXPECTED_ERROR_MESSAGE
                                    +  "], Actual: ["
                                    + actualErrorMessage
                                    + "]"
                    );
                },
                () -> {
                    final String actualUsernameBorderColorCode = loginPage.getUsernameBorderColorCode();
                    assertEquals(
                            EXPECTED_COLOR_CODE,
                            actualUsernameBorderColorCode,
                            "The username border color code is not as expected. Expected: ["
                                    + EXPECTED_COLOR_CODE
                                    + "], Actual: ["
                                    + actualUsernameBorderColorCode
                                    + "]"
                    );
                },
                () -> {
                    final String actualPasswordBorderColorCode = loginPage.getPasswordBorderColorCode();
                    assertEquals(
                            EXPECTED_COLOR_CODE,
                            actualPasswordBorderColorCode,
                            "The password border color code is not as expected. Expected: ["
                                    + EXPECTED_COLOR_CODE
                                    + "], Actual: ["
                                    +  actualPasswordBorderColorCode
                                    + "]"
                    );
                }
        );
    }

}
