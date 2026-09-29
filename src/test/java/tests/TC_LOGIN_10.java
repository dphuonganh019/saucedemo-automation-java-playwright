package tests;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import pages.LoginPage;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TC_LOGIN_10: Detailed UI verification of the Login screen")
public class TC_LOGIN_10 extends BaseTest{

    static LoginPage loginPage;

    private final static String EXPECTED_USERNAME_PLACEHOLDER = "Username";
    private final static String EXPECTED_PASSWORD_PLACEHOLDER = "Password";
    private final static String LOGIN_BTN_TXT = "Login";
    private final static String EXPECTED_HEADER_ACCOUNT_LIST = "Accepted usernames are:";
    private final static String EXPECTED_HEADER_PASSWORD_LIST = "Password for all users:";
    private final static List<String> EXPECTED_ACCOUNT_LIST = List.of(
            "standard_user",
            "locked_out_user",
            "problem_user",
            "performance_glitch_user",
            "error_user",
            "visual_user"
    );
    // Currently the list has only one value, but I still declare it as a List, in case of in the future the system will have
    // other passwords for different accounts
    private final static List<String> EXPECTED_PASSWORD_LIST = List.of("secret_sauce");
    @BeforeAll
    static void setupPrecondition(){
        loginPage = new LoginPage(page());
        loginPage.goToSauceDemoPage();
    }
    @Test
    @Order(1)
    @DisplayName("Step 1: Verify the Swag Labs logo text is displayed at the top of the page</br>" +
    "Expected Results: - Display the login Logo with text [Swag Labs]")
    public void verifyUILoginPage() {
        assertAll(
                () -> assertTrue(
                        loginPage.isLoginLogoVisible(),
                        "The Login Logo is not display as expected"
                ),
                () -> {
                    final String expectedLogoText = "Swag Labs";
                    final String actualLogoText =  loginPage.getLogoText();
                    assertEquals(
                            expectedLogoText,
                            actualLogoText,
                            "The text of Login Logo is not as expected. Expected: ["
                                    + expectedLogoText
                                    + "], Actual: ["
                                    + actualLogoText
                                    + "]"
                    );
                }
        );
    }

    @Test
    @Order(2)
    @DisplayName("Step 2: Verify the Username field is displayed with placeholder Username</br>" +
            "Expected Results: Username field is displayed, placeholder = Username, field is empty."
    )
    public void verifyUsernameField(){
        assertAll(
                () -> assertTrue(
                        loginPage.isUsernameInputVisible(),
                        "The Username input field is not display as expected"
                ),
                () -> {
                    final String actualUsernamePlaceholder = loginPage.getUsernamePlaceHolder();
                    assertEquals(
                            EXPECTED_USERNAME_PLACEHOLDER,
                            actualUsernamePlaceholder,
                            "The Username placeholder is as expected. Expected: ["
                                    + EXPECTED_USERNAME_PLACEHOLDER
                                    + "], Actual: ["
                                    + actualUsernamePlaceholder
                                    + "]"
                    );
                },
                () -> assertTrue(loginPage.getUsernameInputValue().isEmpty(),
                        "The current input value is not empty as expected"
                )
        );
    }

    @Test
    @Order(3)
    @DisplayName("Step 3: Verify the Password field is displayed with placeholder Password</br>" +
            "Expected Results: Password field is displayed, placeholder = Password, field is empty")
    public void verifyPasswordField(){
        assertAll(
                () -> assertTrue(
                        loginPage.isPasswordInputVisible(),
                        "The Password input field is not display as expected"
                ),
                () -> {
                    final String actualPasswordPlaceholder = loginPage.getPasswordPlaceHolder();
                    assertEquals(
                            EXPECTED_PASSWORD_PLACEHOLDER,
                            actualPasswordPlaceholder,
                            "The Password placeholder is as expected. Expected: ["
                            + EXPECTED_PASSWORD_PLACEHOLDER
                            + "], Actual: ["
                            + actualPasswordPlaceholder
                    );
                },
                () -> assertTrue(loginPage.getPasswordInputValue().isEmpty(),
                        "The current input value is not empty as expected")
        );
    }

    @Test
    @Order(4)
    @DisplayName("Step 4: Verify the Login button is displayed, its label is [Login], and it is enabled</br>" +
            "Expected Results: Login button is displayed with text [Login] and is enabled")
    public void verifyLoginButton(){
        assertAll(
                () -> assertTrue(
                        loginPage.isLoginButtonVisible(),
                        "The Login button is not displayed as expected"
                ),
                () -> assertTrue(
                        loginPage.isLoginButtonEnable(),
                        "The Login button is enabled as expected"
                ),
                () -> {
                    final String actualTextButton = loginPage.getLoginButtonText();
                    assertEquals(
                            LOGIN_BTN_TXT,
                            actualTextButton,
                            "The Login button is not as expected. Expected: ["
                            +  LOGIN_BTN_TXT
                            + "], Actual: ["
                            + actualTextButton
                    );
                }
        );
    }

    @Test
    @Order(5)
    @DisplayName("Step 5: Verify the Accepted usernames panel displays all 6 valid usernames in the correct order</br>" +
            "Expected Results: Credentials panel lists exactly 6 usernames in this order: standard_user, locked_out_user, problem_user, performance_glitch_user, error_user, visual_user.")
    public void verifyAcceptedUsernamesPanel() {
        String actualHeaderAndAccountList = loginPage.getListAccount();
        List<String> splitData = List.of(actualHeaderAndAccountList.split(StringUtils.LF));
        String actualHeaderAccountList = splitData.getFirst();
        final int indexOfFirstUsername = 1;
        List<String> actualAccountList = splitData.subList(indexOfFirstUsername, splitData.size());
        assertAll(
                () -> assertEquals(
                        EXPECTED_HEADER_ACCOUNT_LIST,
                        actualHeaderAccountList,
                        "The Accepted usernames header is not as expected. Expected: ["
                        + EXPECTED_HEADER_ACCOUNT_LIST
                        + "], Actual: ["
                        + actualHeaderAccountList
                        + "]"
                ),
                () -> assertEquals(
                        EXPECTED_ACCOUNT_LIST,
                        actualAccountList,
                        "The Accepted usernames list are not as expected. Expected: ["
                        + EXPECTED_ACCOUNT_LIST
                        + "], Actual: ["
                        + actualAccountList
                        + "]"
                )
        );
    }

    @Test
    @Order(6)
    @DisplayName("Step 6: Verify the password hint panel displays the correct text</br>" +
            "Expected Results: Panel displays the exact text")
    public void verifyPasswordPanel() {
        String actualHeaderAndPasswordList = loginPage.getListPassword();
        List<String> splitData = List.of(actualHeaderAndPasswordList.split(StringUtils.LF));
        String actualHeaderPasswordList = splitData.getFirst();
        final int indexOfFirstPassword = 1;
        List<String> actualPasswordList = splitData.subList(indexOfFirstPassword, splitData.size());

        assertAll(
                () -> assertEquals(
                        EXPECTED_HEADER_PASSWORD_LIST,
                        actualHeaderPasswordList,
                        "The Password panel header is not as expected. Expected: ["
                                + EXPECTED_HEADER_PASSWORD_LIST
                                + "], Actual: ["
                                + actualHeaderPasswordList
                                + "]"
                ),
                () -> assertEquals(
                        EXPECTED_PASSWORD_LIST,
                        actualPasswordList,
                        "The password list are not as expected. Expected: ["
                                + EXPECTED_PASSWORD_LIST
                                + "], Actual: ["
                                + actualPasswordList
                                + "]"
                )
        );
    }

    @Test
    @Order(7)
    @DisplayName("Step 7: Verify the Username field, Password field, and Login button all sit inside the login box [class='login-box']" +
            " and appear in this top-to-bottom order: Username -> Password -> Login</br>" +
            "Expected Results: Top-to-bottom order is correct: Username field -> Password field -> Login button.")
    public void verifyLoginBox() {
        final double loginFieldPositionY = loginPage.getLoginFieldPositionY();
        final double passwordFieldPositionY = loginPage.getPasswordFieldPositionY();
        final double loginButtonPositionY = loginPage.getLoginButtonPositionY();
        assertAll(
                () -> assertTrue(
                        loginPage.isUsernameFieldInLoginBox(),
                        "The username input field is not sit inside the login box as expected."
                ),
                () -> assertTrue(
                        loginPage.isPasswordFieldInLoginBox(),
                        "The password input field is not sit inside the login box as expected."
                ),
                () -> assertTrue(
                        loginPage.isLoginButtonInLoginBox(),
                        "The login button is not sit inside the login box as expected."
                ),
                () -> assertTrue(
                        loginFieldPositionY < passwordFieldPositionY,
                        "The Login Field is not positioned above the Password field"
                ),
                () -> assertTrue(
                        passwordFieldPositionY < loginButtonPositionY,
                        "The Password field is not positioned above the Login Button"
                )
        );
    }
}
