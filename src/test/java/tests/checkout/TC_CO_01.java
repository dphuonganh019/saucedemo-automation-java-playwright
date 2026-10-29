package tests.checkout;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import pages.CartPage;
import pages.CheckoutInformationPage;
import pages.CheckoutOverviewPage;
import pages.HeaderComponent;
import pages.InventoryPage;
import pages.LoginPage;
import tests.BaseTest;
import utils.ConfigReader;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("TC_CO_01: Checkout Step One - submit valid information")
public class TC_CO_01 extends BaseTest {
    static LoginPage loginPage;
    static InventoryPage inventoryPage;
    static HeaderComponent header;
    static CartPage cartPage;
    static CheckoutInformationPage checkoutInformationPage;
    static CheckoutOverviewPage checkoutOverviewPage;

    private static final String FIRST_NAME = "Bear";
    private static final String LAST_NAME = "Nguyen";
    private static final String POSTAL_CODE = "700000";
    private static final String EXPECTED_URL = ConfigReader.get("baseUrl") + "checkout-step-two.html";

    @BeforeAll
    static void setupPrecondition() {
        loginPage = new LoginPage(page());
        inventoryPage = new InventoryPage(page());
        header = new HeaderComponent(page());
        cartPage = new CartPage(page());
        checkoutInformationPage = new CheckoutInformationPage(page());
        checkoutOverviewPage = new CheckoutOverviewPage(page());
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
    @DisplayName("Step 3: Enter [700000] into Postal Code")
    public void enterPostalCode() {
        checkoutInformationPage.fillPostalCode(POSTAL_CODE);
    }

    @Test
    @Order(4)
    @DisplayName("Step 4: Click the [Continue] button")
    public void clickContinue() {
        checkoutInformationPage.clickContinue();
    }

    @Test
    @Order(5)
    @DisplayName("Expected Result: System navigates to https://www.saucedemo.com/checkout-step-two.html (Checkout: Overview)")
    public void verifyNavigateToOverview() {
        // Chờ trang Overview render xong rồi mới đọc URL
        checkoutOverviewPage.isFinishButtonVisible();
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
    }
}
