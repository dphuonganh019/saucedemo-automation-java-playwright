package tests.inventory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import pages.InventoryPage;
import pages.LoginPage;
import tests.BaseTest;
import utils.ConfigReader;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TC_INV_01: Detailed verification of all 6 products on the Products page")
public class TC_INV_01 extends BaseTest {
    static LoginPage loginPage;
    static InventoryPage inventoryPage;

    private static final int EXPECTED_PRODUCT_COUNT = 6;
    private static final String ADD_TO_CART_BTN_TXT = "Add to cart";
    private static final List<String> EXPECTED_DEFAULT_ORDER = List.of(
            "Sauce Labs Backpack",
            "Sauce Labs Bike Light",
            "Sauce Labs Bolt T-Shirt",
            "Sauce Labs Fleece Jacket",
            "Sauce Labs Onesie",
            "Test.allTheThings() T-Shirt (Red)"
    );

    @BeforeAll
    static void setupPrecondition() {
        loginPage = new LoginPage(page());
        inventoryPage = new InventoryPage(page());
        loginPage.goToSauceDemoPage();
        loginPage.loginAs(ConfigReader.get("username_standard_user"), ConfigReader.get("password"));
    }

    @Test
    @Order(1)
    @DisplayName("Step 1: Count the number of product cards displayed</br>" +
            "Expected Results: Card count = 6, in the default order")
    public void verifyProductCount() {
        final int actualProductCount = inventoryPage.getProductCount();
        final List<String> actualProductNames = inventoryPage.getProductNames();
        assertAll(
                () -> assertEquals(
                        EXPECTED_PRODUCT_COUNT,
                        actualProductCount,
                        "The number of product cards is not as expected. Expected: ["
                                + EXPECTED_PRODUCT_COUNT
                                + "], Actual: ["
                                + actualProductCount
                                + "]"
                ),
                () -> assertEquals(
                        EXPECTED_DEFAULT_ORDER,
                        actualProductNames,
                        "The default order of products is not as expected. Expected: ["
                                + EXPECTED_DEFAULT_ORDER
                                + "], Actual: ["
                                + actualProductNames
                                + "]"
                )
        );
    }

    @Test
    @Order(2)
    @DisplayName("Step 2: Verify product card 1 [Sauce Labs Backpack] shows image, name, description, price, and Add to cart button</br>" +
            "Expected Results: Price [$29.99], name, description, image and [Add to cart] button are displayed")
    public void verifyBackpackCard() {
        verifyProductCard("Sauce Labs Backpack", "$29.99");
    }

    @Test
    @Order(3)
    @DisplayName("Step 3: Verify product card 2 [Sauce Labs Bike Light] shows image, name, description, price, and Add to cart button</br>" +
            "Expected Results: Price [$9.99], name, description, image and [Add to cart] button are displayed")
    public void verifyBikeLightCard() {
        verifyProductCard("Sauce Labs Bike Light", "$9.99");
    }

    @Test
    @Order(4)
    @DisplayName("Step 4: Verify product card 3 [Sauce Labs Bolt T-Shirt] shows image, name, description, price, and Add to cart button</br>" +
            "Expected Results: Price [$15.99], name, description, image and [Add to cart] button are displayed")
    public void verifyBoltTShirtCard() {
        verifyProductCard("Sauce Labs Bolt T-Shirt", "$15.99");
    }

    @Test
    @Order(5)
    @DisplayName("Step 5: Verify product card 4 [Sauce Labs Fleece Jacket] shows image, name, description, price, and Add to cart button</br>" +
            "Expected Results: Price [$49.99], name, description, image and [Add to cart] button are displayed")
    public void verifyFleeceJacketCard() {
        verifyProductCard("Sauce Labs Fleece Jacket", "$49.99");
    }

    @Test
    @Order(6)
    @DisplayName("Step 6: Verify product card 5 [Sauce Labs Onesie] shows image, name, description, price, and Add to cart button</br>" +
            "Expected Results: Price [$7.99], name, description, image and [Add to cart] button are displayed")
    public void verifyOnesieCard() {
        verifyProductCard("Sauce Labs Onesie", "$7.99");
    }

    @Test
    @Order(7)
    @DisplayName("Step 7: Verify product card 6 [Test.allTheThings() T-Shirt (Red)] shows image, name, description, price, and Add to cart button</br>" +
            "Expected Results: Price [$15.99], name, description, image and [Add to cart] button are displayed")
    public void verifyRedTShirtCard() {
        verifyProductCard("Test.allTheThings() T-Shirt (Red)", "$15.99");
    }

    // 6 step trên chỉ khác nhau ở tên sản phẩm và giá -> gom phần assert vào 1 hàm dùng chung
    private void verifyProductCard(String productName, String expectedPrice) {
        assertAll(
                () -> assertTrue(
                        inventoryPage.isProductImageLoaded(productName),
                        "The image of [" + productName + "] is not displayed as expected"
                ),
                () -> assertTrue(
                        inventoryPage.isProductNameVisible(productName),
                        "The name of [" + productName + "] is not displayed as expected"
                ),
                () -> assertFalse(
                        inventoryPage.getProductDescriptionText(productName).isBlank(),
                        "The description of [" + productName + "] is empty"
                ),
                () -> {
                    final String actualPrice = inventoryPage.getProductPriceText(productName);
                    assertEquals(
                            expectedPrice,
                            actualPrice,
                            "The price of [" + productName + "] is not as expected. Expected: ["
                                    + expectedPrice
                                    + "], Actual: ["
                                    + actualPrice
                                    + "]"
                    );
                },
                () -> {
                    final String actualButtonText = inventoryPage.getProductButtonText(productName);
                    assertEquals(
                            ADD_TO_CART_BTN_TXT,
                            actualButtonText,
                            "The button of [" + productName + "] is not as expected. Expected: ["
                                    + ADD_TO_CART_BTN_TXT
                                    + "], Actual: ["
                                    + actualButtonText
                                    + "]"
                    );
                }
        );
    }
}
