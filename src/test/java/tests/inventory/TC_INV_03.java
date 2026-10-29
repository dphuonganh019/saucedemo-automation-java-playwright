package tests.inventory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import pages.HeaderComponent;
import pages.InventoryPage;
import pages.LoginPage;
import tests.BaseTest;
import utils.ConfigReader;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("TC_INV_03: Add multiple products to the cart, badge updates to the correct count")
public class TC_INV_03 extends BaseTest {
    static LoginPage loginPage;
    static InventoryPage inventoryPage;
    static HeaderComponent header;

    private static final String BACKPACK = "Sauce Labs Backpack";
    private static final String BIKE_LIGHT = "Sauce Labs Bike Light";
    private static final String ONESIE = "Sauce Labs Onesie";
    private static final String REMOVE_BTN_TXT = "Remove";
    private static final String EXPECTED_CART_BADGE = "3";

    @BeforeAll
    static void setupPrecondition() {
        loginPage = new LoginPage(page());
        inventoryPage = new InventoryPage(page());
        header = new HeaderComponent(page());
        loginPage.goToSauceDemoPage();
        loginPage.loginAs(ConfigReader.get("username_standard_user"), ConfigReader.get("password"));
    }

    @Test
    @Order(1)
    @DisplayName("Step 1: Click [Add to cart] for [Sauce Labs Backpack]")
    public void addBackpackToCart() {
        inventoryPage.addProductToCart(BACKPACK);
    }

    @Test
    @Order(2)
    @DisplayName("Step 2: Click [Add to cart] for [Sauce Labs Bike Light]")
    public void addBikeLightToCart() {
        inventoryPage.addProductToCart(BIKE_LIGHT);
    }

    @Test
    @Order(3)
    @DisplayName("Step 3: Click [Add to cart] for [Sauce Labs Onesie]")
    public void addOnesieToCart() {
        inventoryPage.addProductToCart(ONESIE);
    }

    @Test
    @Order(4)
    @DisplayName("Expected Results:</br>" +
            "- Cart badge shows [3];</br>" +
            "- All 3 buttons now show [Remove]")
    public void verifyMultipleProductsAdded() {
        assertAll(
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
                },
                () -> {
                    for (String productName : List.of(BACKPACK, BIKE_LIGHT, ONESIE)) {
                        final String actualButtonText = inventoryPage.getProductButtonText(productName);
                        assertEquals(
                                REMOVE_BTN_TXT,
                                actualButtonText,
                                "The button of [" + productName + "] is not as expected. Expected: ["
                                        + REMOVE_BTN_TXT
                                        + "], Actual: ["
                                        + actualButtonText
                                        + "]"
                        );
                    }
                }
        );
    }
}
