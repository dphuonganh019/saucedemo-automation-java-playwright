package tests.inventory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import pages.InventoryPage;
import pages.LoginPage;
import pages.ProductDetailPage;
import tests.BaseTest;
import utils.ConfigReader;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("TC_INV_09: Clicking a product name navigates to the Product Detail page")
public class TC_INV_09 extends BaseTest {
    static LoginPage loginPage;
    static InventoryPage inventoryPage;
    static ProductDetailPage productDetailPage;

    private static final String PRODUCT_NAME = "Sauce Labs Backpack";
    private static final String EXPECTED_URL = ConfigReader.get("baseUrl") + "inventory-item.html?id=4";
    // Lấy description và price ở trang Inventory trước khi click để so sánh với trang Detail
    static String expectedDescription;
    static String expectedPrice;

    @BeforeAll
    static void setupPrecondition() {
        loginPage = new LoginPage(page());
        inventoryPage = new InventoryPage(page());
        productDetailPage = new ProductDetailPage(page());
        loginPage.goToSauceDemoPage();
        loginPage.loginAs(ConfigReader.get("username_standard_user"), ConfigReader.get("password"));
        expectedDescription = inventoryPage.getProductDescriptionText(PRODUCT_NAME);
        expectedPrice = inventoryPage.getProductPriceText(PRODUCT_NAME);
    }

    @Test
    @Order(1)
    @DisplayName("Step 1: Click the product name [Sauce Labs Backpack]")
    public void clickProductName() {
        inventoryPage.clickProductName(PRODUCT_NAME);
    }

    @Test
    @Order(2)
    @DisplayName("Expected Results:</br>" +
            "- System navigates to https://www.saucedemo.com/inventory-item.html?id=4;</br>" +
            "- Name, description, and price match the selected product")
    public void verifyProductDetailDisplayed() {
        final String actualName = productDetailPage.getProductName();
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
                () -> assertEquals(
                        PRODUCT_NAME,
                        actualName,
                        "The product name is not as expected. Expected: ["
                                + PRODUCT_NAME
                                + "], Actual: ["
                                + actualName
                                + "]"
                ),
                () -> {
                    final String actualDescription = productDetailPage.getProductDescription();
                    assertEquals(
                            expectedDescription,
                            actualDescription,
                            "The product description is not as expected. Expected: ["
                                    + expectedDescription
                                    + "], Actual: ["
                                    + actualDescription
                                    + "]"
                    );
                },
                () -> {
                    final String actualPrice = productDetailPage.getProductPrice();
                    assertEquals(
                            expectedPrice,
                            actualPrice,
                            "The product price is not as expected. Expected: ["
                                    + expectedPrice
                                    + "], Actual: ["
                                    + actualPrice
                                    + "]"
                    );
                }
        );
    }
}
