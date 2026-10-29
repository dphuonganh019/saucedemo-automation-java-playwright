package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import utils.LogUtil;

// Checkout Step One: checkout-step-one.html (Checkout: Your Information)
public class CheckoutInformationPage extends BasePage {
    private final Locator firstNameInput = page.locator("//input[@data-test='firstName']");
    private final Locator lastNameInput = page.locator("//input[@data-test='lastName']");
    private final Locator postalCodeInput = page.locator("//input[@data-test='postalCode']");
    private final Locator continueButton = page.locator("//input[@data-test='continue']");
    private final Locator cancelButton = page.locator("//button[@data-test='cancel']");
    private final Locator errorMessage = page.locator("//h3[@data-test='error']");

    public CheckoutInformationPage(Page page) {
        super(page);
    }

    public void fillFirstName(String firstName) {
        fillText(firstNameInput, firstName);
        LogUtil.info("Filling First Name: " + firstName);
    }

    public void fillLastName(String lastName) {
        fillText(lastNameInput, lastName);
        LogUtil.info("Filling Last Name: " + lastName);
    }

    public void fillPostalCode(String postalCode) {
        fillText(postalCodeInput, postalCode);
        LogUtil.info("Filling Postal Code: " + postalCode);
    }

    // Dùng cho Precondition của các case ở Checkout Step Two / Complete
    public void fillInformationAndContinue(String firstName, String lastName, String postalCode) {
        fillFirstName(firstName);
        fillLastName(lastName);
        fillPostalCode(postalCode);
        clickContinue();
    }

    public String getFirstNameValue() {
        final String value = getAttributeValue(firstNameInput);
        LogUtil.info("First Name value: " + value);
        return value;
    }

    public String getLastNameValue() {
        final String value = getAttributeValue(lastNameInput);
        LogUtil.info("Last Name value: " + value);
        return value;
    }

    public String getPostalCodeValue() {
        final String value = getAttributeValue(postalCodeInput);
        LogUtil.info("Postal Code value: " + value);
        return value;
    }

    public void clickContinue() {
        clickElement(continueButton);
    }

    public void clickCancel() {
        clickElement(cancelButton);
    }

    public String getErrorMessageContent() {
        final String message = getInnerText(errorMessage);
        LogUtil.info("Error message: " + message);
        return message;
    }
}
