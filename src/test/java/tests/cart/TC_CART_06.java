package tests.cart;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import pages.CartPage;
import pages.HeaderComponent;
import pages.InventoryPage;
import pages.LoginPage;
import tests.BaseTest;
import utils.ConfigReader;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("TC_CART_06: Product quantity in the cart is always 1 (no increment/decrement control)")
public class TC_CART_06 extends BaseTest {
    static LoginPage loginPage;
    static InventoryPage inventoryPage;
    static HeaderComponent header;
    static CartPage cartPage;

    private static final String PRODUCT_NAME = "Sauce Labs Backpack";
    private static final String EXPECTED_QUANTITY = "1";
    private static final int EXPECTED_QUANTITY_CONTROL_COUNT = 0;

    @BeforeAll
    static void setupPrecondition() {
        loginPage = new LoginPage(page());
        inventoryPage = new InventoryPage(page());
        header = new HeaderComponent(page());
        cartPage = new CartPage(page());
        loginPage.goToSauceDemoPage();
        loginPage.loginAs(ConfigReader.get("username_standard_user"), ConfigReader.get("password"));
        inventoryPage.addProductToCart(PRODUCT_NAME);
    }

    @Test
    @Order(1)
    @DisplayName("Step 1: Go to the Cart page")
    public void goToCartPage() {
        header.clickCartIcon();
    }

    @Test
    @Order(2)
    @DisplayName("Step 2: Check the value of the quantity</br>" +
            "Expected Results: Quantity value is always [1]")
    public void verifyQuantityValue() {
        final String actualQuantity = cartPage.getCartItemQuantityText(PRODUCT_NAME);
        assertEquals(
                EXPECTED_QUANTITY,
                actualQuantity,
                "The quantity is not as expected. Expected: ["
                        + EXPECTED_QUANTITY
                        + "], Actual: ["
                        + actualQuantity
                        + "]"
        );
    }

    @Test
    @Order(3)
    @DisplayName("Step 3: Confirm there is no +/- control anywhere on the page to change the quantity</br>" +
            "Expected Results: No UI control (+/- buttons, quantity input) exists to increase/decrease the product quantity")
    public void verifyNoQuantityControl() {
        final int actualControlCount = cartPage.getQuantityControlCount();
        assertEquals(
                EXPECTED_QUANTITY_CONTROL_COUNT,
                actualControlCount,
                "Found quantity controls in the cart. Expected: ["
                        + EXPECTED_QUANTITY_CONTROL_COUNT
                        + "], Actual: ["
                        + actualControlCount
                        + "]"
        );
    }
}
