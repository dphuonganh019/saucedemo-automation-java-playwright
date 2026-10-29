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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("TC_CART_01: Detailed verification that the Cart page shows the correct products")
public class TC_CART_01 extends BaseTest {
    static LoginPage loginPage;
    static InventoryPage inventoryPage;
    static HeaderComponent header;
    static CartPage cartPage;

    private static final String BACKPACK = "Sauce Labs Backpack";
    private static final String BIKE_LIGHT = "Sauce Labs Bike Light";
    private static final String EXPECTED_URL = ConfigReader.get("baseUrl") + "cart.html";
    private static final String EXPECTED_PAGE_TITLE = "Your Cart";
    private static final String EXPECTED_QTY_LABEL = "QTY";
    private static final String EXPECTED_DESC_LABEL = "Description";
    private static final int EXPECTED_ITEM_COUNT = 2;
    private static final String EXPECTED_QUANTITY = "1";

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
    }

    @Test
    @Order(1)
    @DisplayName("Step 1: Click the cart icon")
    public void clickCartIcon() {
        header.clickCartIcon();
    }

    @Test
    @Order(2)
    @DisplayName("Step 2: Verify the current page URL</br>" +
            "Expected Results: URL = https://www.saucedemo.com/cart.html")
    public void verifyCartUrl() {
        // Chờ trang cart render xong rồi mới đọc URL
        cartPage.isCartListVisible();
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
    }

    @Test
    @Order(3)
    @DisplayName("Step 3: Verify the page title [Your Cart] is displayed</br>" +
            "Expected Results: Title = [Your Cart]")
    public void verifyPageTitle() {
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
    @Order(4)
    @DisplayName("Step 4: Verify the [QTY] column header is displayed</br>" +
            "Expected Results: Column header shows [QTY] correctly")
    public void verifyQuantityLabel() {
        final String actualQtyLabel = cartPage.getQuantityLabelText();
        assertEquals(
                EXPECTED_QTY_LABEL,
                actualQtyLabel,
                "The QTY column header is not as expected. Expected: ["
                        + EXPECTED_QTY_LABEL
                        + "], Actual: ["
                        + actualQtyLabel
                        + "]"
        );
    }

    @Test
    @Order(5)
    @DisplayName("Step 5: Verify the [Description] column header is displayed</br>" +
            "Expected Results: Column header shows [Description] correctly")
    public void verifyDescriptionLabel() {
        final String actualDescLabel = cartPage.getDescriptionLabelText();
        assertEquals(
                EXPECTED_DESC_LABEL,
                actualDescLabel,
                "The Description column header is not as expected. Expected: ["
                        + EXPECTED_DESC_LABEL
                        + "], Actual: ["
                        + actualDescLabel
                        + "]"
        );
    }

    @Test
    @Order(6)
    @DisplayName("Step 6: Count the product cards in the cart</br>" +
            "Expected Results: Exactly 2 product cards are shown")
    public void verifyCartItemCount() {
        final int actualItemCount = cartPage.getCartItemCount();
        assertEquals(
                EXPECTED_ITEM_COUNT,
                actualItemCount,
                "The number of items in cart is not as expected. Expected: ["
                        + EXPECTED_ITEM_COUNT
                        + "], Actual: ["
                        + actualItemCount
                        + "]"
        );
    }

    @Test
    @Order(7)
    @DisplayName("Step 7: Verify the [Sauce Labs Backpack] card has the correct name, price, and quantity = 1</br>" +
            "Expected Results: Backpack - price [$29.99], quantity [1]")
    public void verifyBackpackItem() {
        verifyCartItem(BACKPACK, "$29.99");
    }

    @Test
    @Order(8)
    @DisplayName("Step 8: Verify the [Sauce Labs Bike Light] card has the correct name, price, and quantity = 1</br>" +
            "Expected Results: Bike Light - price [$9.99], quantity [1]")
    public void verifyBikeLightItem() {
        verifyCartItem(BIKE_LIGHT, "$9.99");
    }

    // 2 step trên chỉ khác nhau ở tên sản phẩm và giá -> gom phần assert vào 1 hàm dùng chung
    private void verifyCartItem(String productName, String expectedPrice) {
        assertAll(
                () -> {
                    final String actualPrice = cartPage.getCartItemPriceText(productName);
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
                    final String actualQuantity = cartPage.getCartItemQuantityText(productName);
                    assertEquals(
                            EXPECTED_QUANTITY,
                            actualQuantity,
                            "The quantity of [" + productName + "] is not as expected. Expected: ["
                                    + EXPECTED_QUANTITY
                                    + "], Actual: ["
                                    + actualQuantity
                                    + "]"
                    );
                }
        );
    }
}
