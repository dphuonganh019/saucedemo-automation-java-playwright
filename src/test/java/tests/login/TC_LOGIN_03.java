package tests.login;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import pages.LoginPage;
import tests.BaseTest;
import utils.ConfigReader;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("TC_LOGIN_03: Login with multiple accounts (data-driven)")
public class TC_LOGIN_03 extends BaseTest {
    static LoginPage loginPage;

    private static final String EXPECTED_URL_AFTER_LOGIN = ConfigReader.get("baseUrl") + "inventory.html";
    private static final String SUCCESS_MARKER = "SUCCESS";
    // performance_glitch_user cố tình bị làm chậm bởi SauceDemo nên cần timeout dài hơn mặc định (5000ms)
    private static final String SLOW_USERNAME = "performance_glitch_user";
    private static final int SLOW_CLICK_TIMEOUT_MS = 10000;

    @BeforeAll
    static void setup() {
        loginPage = new LoginPage(page());
    }

    @ParameterizedTest(name = "[{index}] username={0}, expectedResult={2}")
    @DisplayName("Login with each account from testdata/login_accounts.csv and verify the result")
    @CsvFileSource(resources = "/testdata/login_accounts.csv", numLinesToSkip = 1)
    void loginWithMultipleAccounts(String username, String password, String expectedResult) {
        loginPage.goToSauceDemoPage();
        loginPage.fillUsername(username);
        loginPage.fillPassword(password);
        if (SLOW_USERNAME.equals(username)) {
            loginPage.clickLoginButton(SLOW_CLICK_TIMEOUT_MS);
        } else {
            loginPage.clickLoginButton();
        }

        if (SUCCESS_MARKER.equals(expectedResult)) {
            final String actualUrl = browserManager().getCurrentTabUrl();
            assertEquals(
                    EXPECTED_URL_AFTER_LOGIN,
                    actualUrl,
                    "Login with username [" + username + "] did not redirect to the expected URL. Expected: ["
                            + EXPECTED_URL_AFTER_LOGIN
                            + "], Actual: ["
                            + actualUrl
                            + "]"
            );
        } else {
            final String actualErrorMessage = loginPage.getErrorMessageContent();
            assertEquals(
                    expectedResult,
                    actualErrorMessage,
                    "Error message for username [" + username + "] is not as expected. Expected: ["
                            + expectedResult
                            + "], Actual: ["
                            + actualErrorMessage
                            + "]"
            );
        }
    }
}
