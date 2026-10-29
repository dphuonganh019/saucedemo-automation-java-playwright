package tests.inventory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import pages.HeaderComponent;
import pages.LoginPage;
import tests.BaseTest;
import utils.ConfigReader;

import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("TC_INV_11: Cart badge is not shown when the cart is empty")
public class TC_INV_11 extends BaseTest {
    static LoginPage loginPage;
    static HeaderComponent header;

    @BeforeAll
    static void setupPrecondition() {
        loginPage = new LoginPage(page());
        header = new HeaderComponent(page());
        loginPage.goToSauceDemoPage();
        loginPage.loginAs(ConfigReader.get("username_standard_user"), ConfigReader.get("password"));
    }

    @Test
    @Order(1)
    @DisplayName("Step 1: Observe the cart icon in the header</br>" +
            "Expected Results: Cart icon displays normally")
    public void verifyCartIconDisplayed() {
        assertTrue(
                header.isCartIconVisible(),
                "The cart icon is not displayed as expected"
        );
    }

    @Test
    @Order(2)
    @DisplayName("Step 2: Check whether the cart badge element exists in the DOM</br>" +
            "Expected Results: The cart badge element does NOT exist in the DOM (no count is shown)")
    public void verifyCartBadgeNotExist() {
        assertTrue(
                header.isCartBadgeRemoved(),
                "The cart badge exists in the DOM although the cart is empty"
        );
    }
}
