package tests.checkout;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import pages.CartPage;
import pages.CheckoutCompletePage;
import pages.CheckoutInformationPage;
import pages.CheckoutOverviewPage;
import pages.HeaderComponent;
import pages.InventoryPage;
import pages.LoginPage;
import tests.BaseTest;
import utils.ConfigReader;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TC_CO_09: Checkout Complete - detailed verification of the success message")
public class TC_CO_09 extends BaseTest {
    static LoginPage loginPage;
    static InventoryPage inventoryPage;
    static HeaderComponent header;
    static CartPage cartPage;
    static CheckoutInformationPage checkoutInformationPage;
    static CheckoutOverviewPage checkoutOverviewPage;
    static CheckoutCompletePage checkoutCompletePage;

    private static final String EXPECTED_PAGE_TITLE = "Checkout: Complete!";
    private static final String EXPECTED_COMPLETE_HEADER = "Thank you for your order!";
    private static final String EXPECTED_COMPLETE_TEXT =
            "Your order has been dispatched, and will arrive just as fast as the pony can get there!";

    @BeforeAll
    static void setupPrecondition() {
        loginPage = new LoginPage(page());
        inventoryPage = new InventoryPage(page());
        header = new HeaderComponent(page());
        cartPage = new CartPage(page());
        checkoutInformationPage = new CheckoutInformationPage(page());
        checkoutOverviewPage = new CheckoutOverviewPage(page());
        checkoutCompletePage = new CheckoutCompletePage(page());
        loginPage.goToSauceDemoPage();
        loginPage.loginAs(ConfigReader.get("username_standard_user"), ConfigReader.get("password"));
        inventoryPage.addProductToCart("Sauce Labs Backpack");
        header.clickCartIcon();
        cartPage.clickCheckout();
        checkoutInformationPage.fillInformationAndContinue("Bear", "Nguyen", "700000");
        checkoutOverviewPage.clickFinish();
    }

    @Test
    @Order(1)
    @DisplayName("Step 1: Verify the page title [Checkout: Complete!] is displayed</br>" +
            "Expected Results: Title = [Checkout: Complete!]")
    public void verifyPageTitle() {
        // Chờ trang Complete render xong rồi mới đọc title (title cũng có ở trang Overview)
        checkoutCompletePage.getCompleteHeader();
        final String actualPageTitle = header.getPageTitle();
        assertEquals(
                EXPECTED_PAGE_TITLE,
                actualPageTitle,
                "The page title is not as expected. Expected: ["
                        + EXPECTED_PAGE_TITLE
                        + "], Actual: ["
                        + actualPageTitle
                        + "]"
        );
    }

    @Test
    @Order(2)
    @DisplayName("Step 2: Verify the header [Thank you for your order!] is displayed</br>" +
            "Expected Results: Header = [Thank you for your order!]")
    public void verifyCompleteHeader() {
        final String actualCompleteHeader = checkoutCompletePage.getCompleteHeader();
        assertEquals(
                EXPECTED_COMPLETE_HEADER,
                actualCompleteHeader,
                "The complete header is not as expected. Expected: ["
                        + EXPECTED_COMPLETE_HEADER
                        + "], Actual: ["
                        + actualCompleteHeader
                        + "]"
        );
    }

    @Test
    @Order(3)
    @DisplayName("Step 3: Verify the description text is displayed correctly</br>" +
            "Expected Results: Text = [Your order has been dispatched, and will arrive just as fast as the pony can get there!]")
    public void verifyCompleteText() {
        final String actualCompleteText = checkoutCompletePage.getCompleteText();
        assertEquals(
                EXPECTED_COMPLETE_TEXT,
                actualCompleteText,
                "The complete text is not as expected. Expected: ["
                        + EXPECTED_COMPLETE_TEXT
                        + "], Actual: ["
                        + actualCompleteText
                        + "]"
        );
    }

    @Test
    @Order(4)
    @DisplayName("Step 4: Verify the Pony Express image is displayed, not broken</br>" +
            "Expected Results: Pony Express image renders fully")
    public void verifyPonyExpressImage() {
        assertTrue(
                checkoutCompletePage.isPonyExpressImageLoaded(),
                "The Pony Express image is broken or not displayed"
        );
    }

    @Test
    @Order(5)
    @DisplayName("Step 5: Verify the [Back Home] button is displayed and clickable</br>" +
            "Expected Results: [Back Home] button is displayed, not disabled")
    public void verifyBackHomeButton() {
        assertAll(
                () -> assertTrue(
                        checkoutCompletePage.isBackHomeButtonVisible(),
                        "The Back Home button is not displayed as expected"
                ),
                () -> assertTrue(
                        checkoutCompletePage.isBackHomeButtonEnabled(),
                        "The Back Home button is disabled"
                )
        );
    }
}
