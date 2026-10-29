package tests.cart;

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

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TC_CART_04: [Checkout] button navigates to Checkout Step One")
public class TC_CART_04 extends BaseTest {
    static LoginPage loginPage;
    static InventoryPage inventoryPage;
    static HeaderComponent header;
    static CartPage cartPage;
    static CheckoutInformationPage checkoutInformationPage;

    private static final String EXPECTED_URL = ConfigReader.get("baseUrl") + "checkout-step-one.html";

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
    }

    @Test
    @Order(1)
    @DisplayName("Step 1: Click the [Checkout] button")
    public void clickCheckout() {
        cartPage.clickCheckout();
    }

    @Test
    @Order(2)
    @DisplayName("Expected Results:</br>" +
            "- System navigates to https://www.saucedemo.com/checkout-step-one.html;</br>" +
            "- First Name, Last Name, and Postal Code fields are shown empty")
    public void verifyCheckoutStepOneDisplayed() {
        final String actualFirstName = checkoutInformationPage.getFirstNameValue();
        assertAll(
                () -> {
                    final String actualUrl = browserManager().getCurrentTabUrl();
                    assertEquals(
                            EXPECTED_URL,
                            actualUrl,
                            "The system did not redirect to the expected URL. Expected: ["
                                    + EXPECTED_URL
                                    + "], Actual: ["
                                    + actualUrl
                                    + "]"
                    );
                },
                () -> assertTrue(
                        actualFirstName.isEmpty(),
                        "The First Name field is not empty as expected"
                ),
                () -> assertTrue(
                        checkoutInformationPage.getLastNameValue().isEmpty(),
                        "The Last Name field is not empty as expected"
                ),
                () -> assertTrue(
                        checkoutInformationPage.getPostalCodeValue().isEmpty(),
                        "The Postal Code field is not empty as expected"
                )
        );
    }
}
