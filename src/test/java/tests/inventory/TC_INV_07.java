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

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("TC_INV_07: Sort products by Price (low to high)")
public class TC_INV_07 extends BaseTest {
    static LoginPage loginPage;
    static InventoryPage inventoryPage;

    private static final String SORT_OPTION_VALUE = "lohi";
    private static final String EXPECTED_ACTIVE_OPTION = "Price (low to high)";
    private static final List<String> EXPECTED_PRODUCT_PRICES = List.of(
            "$7.99",
            "$9.99",
            "$15.99",
            "$15.99",
            "$29.99",
            "$49.99"
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
    @DisplayName("Step 1: Click the sort dropdown")
    public void clickSortDropdown() {
        inventoryPage.clickSortDropdown();
    }

    @Test
    @Order(2)
    @DisplayName("Step 2: Select the [Price (low to high)] option (value=[lohi])")
    public void selectSortOption() {
        inventoryPage.selectSortOption(SORT_OPTION_VALUE);
    }

    @Test
    @Order(3)
    @DisplayName("Expected Results:</br>" +
            "- List is sorted ascending by price: $7.99, $9.99, $15.99, $15.99, $29.99, $49.99;</br>" +
            "- First item is [Sauce Labs Onesie] ($7.99);</br>" +
            "- Active option shows [Price (low to high)]")
    public void verifyProductsSorted() {
        final List<String> actualProductPrices = inventoryPage.getProductPrices();
        assertAll(
                () -> assertEquals(
                        EXPECTED_PRODUCT_PRICES,
                        actualProductPrices,
                        "The product prices after sorting is not as expected. Expected: ["
                                + EXPECTED_PRODUCT_PRICES
                                + "], Actual: ["
                                + actualProductPrices
                                + "]"
                ),
                () -> {
                    final String expectedFirstProduct = "Sauce Labs Onesie";
                    final String actualFirstProduct = inventoryPage.getProductNames().getFirst();
                    assertEquals(
                            expectedFirstProduct,
                            actualFirstProduct,
                            "The first product after sorting is not as expected. Expected: ["
                                    + expectedFirstProduct
                                    + "], Actual: ["
                                    + actualFirstProduct
                                    + "]"
                    );
                },
                () -> {
                    final String actualActiveOption = inventoryPage.getActiveSortOption();
                    assertEquals(
                            EXPECTED_ACTIVE_OPTION,
                            actualActiveOption,
                            "The active sort option is not as expected. Expected: ["
                                    + EXPECTED_ACTIVE_OPTION
                                    + "], Actual: ["
                                    + actualActiveOption
                                    + "]"
                    );
                }
        );
    }
}
