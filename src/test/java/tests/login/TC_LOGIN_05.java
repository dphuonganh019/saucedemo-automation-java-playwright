package tests.login;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import pages.LoginPage;
import tests.BaseTest;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("TC_LOGIN_05: Dismiss the error banner using the X button")
public class TC_LOGIN_05 extends BaseTest {
    static LoginPage loginPage;

    private static final String EXPECTED_ERROR_MESSAGE = "Epic sadface: Username is required";
    private static final String EXPECTED_ERROR_COLOR_CODE = "rgb(226, 35, 26)";
    private static final String EXPECTED_NORMAL_COLOR_CODE = "rgb(237, 237, 237)";

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
    @DisplayName("Step 2: Click Login with the fields left empty to trigger an error")
    public void clickLoginWithEmptyFields() {
        loginPage.clickLoginButton();
    }

    @Test
    @Order(3)
    @DisplayName("Step 3: Confirm the error banner is displayed</br>" +
            "Expected Results: Error banner [Epic sadface: Username is required] is displayed, " +
            "Username and Password fields show a red border (rgb(226, 35, 26))")
    public void verifyErrorBannerDisplayed() {
        assertAll(
                () -> assertTrue(
                        loginPage.isErrorMessageVisible(),
                        "The error banner is not displayed as expected"
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
                },
                () -> {
                    final String actualUsernameBorderColorCode = loginPage.getUsernameBorderColorCode();
                    assertEquals(
                            EXPECTED_ERROR_COLOR_CODE,
                            actualUsernameBorderColorCode,
                            "The username border color code is not as expected. Expected: ["
                                    + EXPECTED_ERROR_COLOR_CODE
                                    + "], Actual: ["
                                    + actualUsernameBorderColorCode
                                    + "]"
                    );
                },
                () -> {
                    final String actualPasswordBorderColorCode = loginPage.getPasswordBorderColorCode();
                    assertEquals(
                            EXPECTED_ERROR_COLOR_CODE,
                            actualPasswordBorderColorCode,
                            "The password border color code is not as expected. Expected: ["
                                    + EXPECTED_ERROR_COLOR_CODE
                                    + "], Actual: ["
                                    + actualPasswordBorderColorCode
                                    + "]"
                    );
                }
        );
    }

    @Test
    @Order(4)
    @DisplayName("Step 4: Click the close (X) button")
    public void clickErrorCloseButton() {
        loginPage.clickErrorCloseButton();
    }

    @Test
    @Order(5)
    @DisplayName("Expected Results:</br>" +
            "- The error banner disappears from the UI;</br>" +
            "- Username and Password fields return to their normal border (rgb(237, 237, 237))")
    public void verifyErrorBannerDismissed() {
        assertAll(
                () -> assertTrue(
                        loginPage.isErrorMessageHidden(),
                        "The error banner is still displayed after clicking the close (X) button"
                ),
                () -> {
                    final String actualUsernameBorderColorCode = loginPage.getUsernameBorderColorCode();
                    assertEquals(
                            EXPECTED_NORMAL_COLOR_CODE,
                            actualUsernameBorderColorCode,
                            "The username border color code is not as expected. Expected: ["
                                    + EXPECTED_NORMAL_COLOR_CODE
                                    + "], Actual: ["
                                    + actualUsernameBorderColorCode
                                    + "]"
                    );
                },
                () -> {
                    final String actualPasswordBorderColorCode = loginPage.getPasswordBorderColorCode();
                    assertEquals(
                            EXPECTED_NORMAL_COLOR_CODE,
                            actualPasswordBorderColorCode,
                            "The password border color code is not as expected. Expected: ["
                                    + EXPECTED_NORMAL_COLOR_CODE
                                    + "], Actual: ["
                                    + actualPasswordBorderColorCode
                                    + "]"
                    );
                }
        );
    }
}
