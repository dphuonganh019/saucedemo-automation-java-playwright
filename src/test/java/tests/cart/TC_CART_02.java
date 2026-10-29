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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TC_CART_02: Remove a product from the Cart page")
public class TC_CART_02 extends BaseTest {
    static LoginPage loginPage;
    static InventoryPage inventoryPage;
    static HeaderComponent header;
    static CartPage cartPage;

    private static final String BACKPACK = "Sauce Labs Backpack";
    private static final String BIKE_LIGHT = "Sauce Labs Bike Light";
    private static final List<String> EXPECTED_REMAINING_ITEMS = List.of(BIKE_LIGHT);
    private static final String EXPECTED_CART_BADGE = "1";

    @BeforeAll
    static void setupPrecondition() {
        loginPage = new LoginPage(page());
        inventoryPage = new InventoryPage(page());
        header = new HeaderComponent(page());
        cartPage = new CartPage(page());
        loginPage.goToSauceDemoPage();
        loginPage.loginAs(ConfigReader.get("username_standard_user"), ConfigReader.get("password"));
        inventoryPage.addProductToCart(BACKPACK);
        inventoryPage.addProductToCart(BIKE_LIGHT);
        header.clickCartIcon();
    }

    @Test
    @Order(1)
    @DisplayName("Step 1: On the Cart page, click the [Remove] button for [Sauce Labs Backpack]")
    public void removeBackpack() {
        cartPage.removeProduct(BACKPACK);
    }

    @Test
    @Order(2)
    @DisplayName("Expected Results:</br>" +
            "- [Sauce Labs Backpack] disappears from the cart list;</br>" +
            "- Only [Sauce Labs Bike Light] remains;</br>" +
            "- Cart badge decreases from [2] to [1]")
    public void verifyProductRemoved() {
        assertAll(
                () -> assertTrue(
                        cartPage.isProductRemovedFromCart(BACKPACK),
                        "[" + BACKPACK + "] is still displayed in the cart list"
                ),
                () -> {
                    final List<String> actualRemainingItems = cartPage.getCartItemNames();
                    assertEquals(
                            EXPECTED_REMAINING_ITEMS,
                            actualRemainingItems,
                            "The remaining items in cart are not as expected. Expected: ["
                                    + EXPECTED_REMAINING_ITEMS
                                    + "], Actual: ["
                                    + actualRemainingItems
                                    + "]"
                    );
                },
                () -> {
                    final String actualCartBadge = header.getCartBadgeText();
                    assertEquals(
                            EXPECTED_CART_BADGE,
                            actualCartBadge,
                            "The cart badge is not as expected. Expected: ["
                                    + EXPECTED_CART_BADGE
                                    + "], Actual: ["
                                    + actualCartBadge
                                    + "]"
                    );
                }
        );
    }
}
