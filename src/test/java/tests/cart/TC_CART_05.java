package tests.cart;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import pages.CartPage;
import pages.HeaderComponent;
import pages.LoginPage;
import tests.BaseTest;
import utils.ConfigReader;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TC_CART_05: Detailed verification of the Cart page UI when the cart is empty")
public class TC_CART_05 extends BaseTest {
    static LoginPage loginPage;
    static HeaderComponent header;
    static CartPage cartPage;

    private static final int EXPECTED_ITEM_COUNT = 0;

    @BeforeAll
    static void setupPrecondition() {
        loginPage = new LoginPage(page());
        header = new HeaderComponent(page());
        cartPage = new CartPage(page());
        loginPage.goToSauceDemoPage();
        loginPage.loginAs(ConfigReader.get("username_standard_user"), ConfigReader.get("password"));
    }

    @Test
    @Order(1)
    @DisplayName("Step 1: Click the cart icon")
    public void clickCartIcon() {
        header.clickCartIcon();
    }

    @Test
    @Order(2)
    @DisplayName("Step 2: Count the product cards in the list</br>" +
            "Expected Results: Card count = 0, list is empty, no error message is shown")
    public void verifyCartEmpty() {
        final int actualItemCount = cartPage.getCartItemCount();
        assertAll(
                () -> assertEquals(
                        EXPECTED_ITEM_COUNT,
                        actualItemCount,
                        "The number of items in cart is not as expected. Expected: ["
                                + EXPECTED_ITEM_COUNT
                                + "], Actual: ["
                                + actualItemCount
                                + "]"
                ),
                () -> assertEquals(
                        0,
                        cartPage.getErrorMessageCount(),
                        "An error message is displayed on the empty cart page"
                )
        );
    }

    // Oracle theo hành vi thực tế: site vẫn hiển thị và cho phép click Checkout dù cart rỗng
    @Test
    @Order(3)
    @DisplayName("Step 3: Check whether the [Checkout] button is displayed and its enabled/disabled state</br>" +
            "Expected Results: The [Checkout] button is still displayed and clickable even with an empty cart (actual behavior)")
    public void verifyCheckoutButton() {
        assertAll(
                () -> assertTrue(
                        cartPage.isCheckoutButtonVisible(),
                        "The Checkout button is not displayed as expected"
                ),
                () -> assertTrue(
                        cartPage.isCheckoutButtonEnabled(),
                        "The Checkout button is disabled"
                )
        );
    }

    @Test
    @Order(4)
    @DisplayName("Step 4: Verify the [Continue Shopping] button displays normally</br>" +
            "Expected Results: [Continue Shopping] button is displayed and clickable")
    public void verifyContinueShoppingButton() {
        assertAll(
                () -> assertTrue(
                        cartPage.isContinueShoppingButtonVisible(),
                        "The Continue Shopping button is not displayed as expected"
                ),
                () -> assertTrue(
                        cartPage.isContinueShoppingButtonEnabled(),
                        "The Continue Shopping button is disabled"
                )
        );
    }
}
