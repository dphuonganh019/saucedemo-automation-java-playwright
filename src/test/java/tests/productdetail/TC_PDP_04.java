package tests.productdetail;

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

@DisplayName("TC_PDP_04: [Back to products] button navigates back to the Inventory page")
public class TC_PDP_04 extends BaseTest {
    static LoginPage loginPage;
    static InventoryPage inventoryPage;
    static ProductDetailPage productDetailPage;

    private static final String EXPECTED_URL = ConfigReader.get("baseUrl") + "inventory.html";
    private static final int EXPECTED_PRODUCT_COUNT = 6;

    @BeforeAll
    static void setupPrecondition() {
        loginPage = new LoginPage(page());
        inventoryPage = new InventoryPage(page());
        productDetailPage = new ProductDetailPage(page());
        loginPage.goToSauceDemoPage();
        loginPage.loginAs(ConfigReader.get("username_standard_user"), ConfigReader.get("password"));
        inventoryPage.clickProductName("Sauce Labs Backpack");
    }

    @Test
    @Order(1)
    @DisplayName("Step 1: Click the [Back to products] button")
    public void clickBackToProducts() {
        productDetailPage.clickBackToProductsButton();
    }

    @Test
    @Order(2)
    @DisplayName("Expected Results:</br>" +
            "- System navigates to https://www.saucedemo.com/inventory.html;</br>" +
            "- All 6 products are displayed")
    public void verifyBackToInventory() {
        final int actualProductCount = inventoryPage.getProductCount();
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
                        EXPECTED_PRODUCT_COUNT,
                        actualProductCount,
                        "The number of products is not as expected. Expected: ["
                                + EXPECTED_PRODUCT_COUNT
                                + "], Actual: ["
                                + actualProductCount
                                + "]"
                )
        );
    }
}
