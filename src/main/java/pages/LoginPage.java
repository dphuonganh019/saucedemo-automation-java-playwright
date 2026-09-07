package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import utils.ConfigReader;
import utils.LogUtil;

public class LoginPage extends BasePage {
    private final Locator loginLogo = page.locator("//div[@class='login_logo']");
    private final Locator usernameInput = page.locator("//input[@data-test='username']");
    private final Locator passwordInput = page.locator("//input[@data-test='password']");
    private final Locator loginButton = page.locator("//input[@data-test='login-button']");
    private final Locator accountList = page.locator("//div[@data-test='login-credentials']");
    private final Locator passwordList = page.locator("//div[@data-test='login-password']");
    private final Locator loginBox = page.locator("//div[@class='login-box']");
    private final Locator errorMessage = page.locator("//div[@class = 'error-message-container error']");

    public LoginPage(Page page){
        super(page);
    }

    public void goToSauceDemoPage(){
        navigateToUrl(ConfigReader.get("baseUrl"));
    }

    public boolean isLoginLogoVisible() {
        return isElementVisible(loginLogo);
    }

    public String getLogoText() {
        LogUtil.info("Logo text: " + getInnerText(loginLogo));
        return getInnerText(loginLogo);
    }

    public boolean isUsernameInputVisible() {
        return isElementVisible(usernameInput);
    }

    public String getUsernamePlaceHolder() {
        LogUtil.info("Username placeholder: " + getPlaceHolderValue(usernameInput));
        return getPlaceHolderValue(usernameInput);
    }

    public String getUsernameInputValue() {
        LogUtil.info("Current input value: " + getAttributeValue(usernameInput));
        return getAttributeValue(usernameInput);
    }

    public String getPasswordPlaceHolder() {
        LogUtil.info("Password placeholder: " + getPlaceHolderValue(passwordInput));
        return getPlaceHolderValue(passwordInput);
    }

    public String getPasswordInputValue() {
        LogUtil.info("Current input value: " + getAttributeValue(usernameInput));
        return getAttributeValue(usernameInput);
    }

    public boolean isPasswordInputVisible() {
        return isElementVisible(passwordInput);
    }

    public boolean isLoginButtonVisible() {
        return isElementVisible(loginButton);
    }

    public boolean isLoginButtonEnable() {
        return isElementEnabled(loginButton);
    }

    public String getLoginButtonText() {
        LogUtil.info("Login button text: " + getAttributeValue(loginButton));
        return getAttributeValue(loginButton);
    }

    public boolean isAccountListVisible() {
        return isElementVisible(accountList);
    }

    public boolean isPasswordListVisible() {
        return isElementVisible(passwordList);
    }

    public void fillUsername(String username) {
        fillText(usernameInput, username);
        LogUtil.info("Filling username field: " + getAttributeValue(usernameInput));
    }

    public void fillPassword(String password) {
        fillText(passwordInput, password);
    }

    public void clickLoginButton() {
        clickElement(loginButton);
    }

    public String getListAccount() {
        LogUtil.info("List account text: " + getInnerText(accountList));
        return getInnerText(accountList);
    }

    public String getListPassword() {
        return getInnerText(passwordList);
    }

    public boolean isUsernameFieldInLoginBox() {
        return loginBox.locator(usernameInput).count() == 1;
    }

    public boolean isPasswordFieldInLoginBox() {
        return loginBox.locator(passwordInput).count() == 1;
    }

    public boolean isLoginButtonInLoginBox() {
        return loginBox.locator(loginButton).count() == 1;
    }

    public double getLoginFieldPositionY() {
        LogUtil.info("Login field position: " + getElementYPosition(usernameInput));
        return getElementYPosition(usernameInput);
    }

    public double getPasswordFieldPositionY() {
        LogUtil.info("Password field position: " + getElementYPosition(passwordInput));
        return getElementYPosition(passwordInput);
    }

    public double getLoginButtonPositionY() {
        LogUtil.info("Login button position: " + getElementYPosition(loginButton));
        return getElementYPosition(loginButton);
    }

    public String getErrorMessageContent() {
        LogUtil.info("Error message: " + getInnerText(errorMessage));
        return getInnerText(errorMessage);
    }

    public String getUsernameBorderColorCode(){
        LogUtil.info("Color code for Username border: " + getCssValue(usernameInput, "border-bottom-color"));
        return getCssValue(usernameInput, "border-bottom-color");
    }

    public String getPasswordBorderColorCode(){
        LogUtil.info("Color code for Password border: " + getCssValue(passwordInput, "border-bottom-color"));
        return getCssValue(passwordInput, "border-bottom-color");
    }

}