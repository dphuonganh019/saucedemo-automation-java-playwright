package tests.checkout;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import pages.CartPage;
import pages.CheckoutInformationPage;
import pages.HeaderComponent;
import pages.InventoryPage;
import pages.LoginPage;
import tests.BaseTest;
import utils.ConfigReader;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("TC_CO_04: Checkout Step One - empty Postal Code")
public class TC_CO_04 extends BaseTest {
    static LoginPage loginPage;
    static InventoryPage inventoryPage;
    static HeaderComponent header;
    static CartPage cartPage;
    static CheckoutInformationPage checkoutInformationPage;

    private static final String FIRST_NAME = "Bear";
    private static final String LAST_NAME = "Nguyen";
    private static final String EXPECTED_URL = ConfigReader.get("baseUrl") + "checkout-step-one.html";
    private static final String EXPECTED_ERROR_MESSAGE = "Error: Postal Code is required";

    @BeforeAll
    static void setupPrecondition() {
        loginPage = new LoginPage(page());
        inventoryPage = new InventoryPage(page());
        header = new HeaderComponent(page());
        cartPage = new CartPage(page());
        checkoutInformationPage = new CheckoutInformationPage(page());
        loginPage.goToSauceDemoPage();
        loginPage.loginAs(ConfigReader.get("username_standard_user"), ConfigReader.get("password"));
        inventoryPage.addProductToCart("Sauce Labs Backpack");
        header.clickCartIcon();
        cartPage.clickCheckout();
    }

    @Test
    @Order(1)
    @DisplayName("Step 1: Enter [Bear] into First Name")
    public void enterFirstName() {
        checkoutInformationPage.fillFirstName(FIRST_NAME);
    }

    @Test
    @Order(2)
    @DisplayName("Step 2: Enter [Nguyen] into Last Name")
    public void enterLastName() {
        checkoutInformationPage.fillLastName(LAST_NAME);
    }

    @Test
    @Order(3)
    @DisplayName("Step 3: Leave Postal Code empty")
    public void emptyPostalCode() {
        checkoutInformationPage.fillPostalCode(StringUtils.EMPTY);
    }

    @Test
    @Order(4)
    @DisplayName("Step 4: Click the [Continue] button")
    public void clickContinue() {
        checkoutInformationPage.clickContinue();
    }

    @Test
    @Order(5)
    @DisplayName("Expected Results:</br>" +
            "- Does not navigate to the next page (still https://www.saucedemo.com/checkout-step-one.html);</br>" +
            "- Error [Error: Postal Code is required] is displayed")
    public void verifyCheckoutFailureResults() {
        final String actualErrorMessage = checkoutInformationPage.getErrorMessageContent();
        assertAll(
                () -> {
                    final String actualUrl = browserManager().getCurrentTabUrl();
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
                () -> assertEquals(
                        EXPECTED_ERROR_MESSAGE,
                        actualErrorMessage,
                        "The actual error message is not as expected. Expected: ["
                                + EXPECTED_ERROR_MESSAGE
                                + "], Actual: ["
                                + actualErrorMessage
                                + "]"
                )
        );
    }
}
