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

@DisplayName("TC_INV_06: Sort products by Name (Z to A)")
public class TC_INV_06 extends BaseTest {
    static LoginPage loginPage;
    static InventoryPage inventoryPage;

    private static final String SORT_OPTION_VALUE = "za";
    private static final String EXPECTED_ACTIVE_OPTION = "Name (Z to A)";
    private static final List<String> EXPECTED_PRODUCT_NAMES = List.of(
            "Test.allTheThings() T-Shirt (Red)",
            "Sauce Labs Onesie",
            "Sauce Labs Fleece Jacket",
            "Sauce Labs Bolt T-Shirt",
            "Sauce Labs Bike Light",
            "Sauce Labs Backpack"
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
    @DisplayName("Step 2: Select the [Name (Z to A)] option (value=[za])")
    public void selectSortOption() {
        inventoryPage.selectSortOption(SORT_OPTION_VALUE);
    }

    @Test
    @Order(3)
    @DisplayName("Expected Results:</br>" +
            "- List is sorted alphabetically descending (reverse of TC_INV_05);</br>" +
            "- First item is [Test.allTheThings() T-Shirt (Red)];</br>" +
            "- Active option shows [Name (Z to A)]")
    public void verifyProductsSorted() {
        final List<String> actualProductNames = inventoryPage.getProductNames();
        assertAll(
                () -> assertEquals(
                        EXPECTED_PRODUCT_NAMES,
                        actualProductNames,
                        "The product order after sorting is not as expected. Expected: ["
                                + EXPECTED_PRODUCT_NAMES
                                + "], Actual: ["
                                + actualProductNames
                                + "]"
                ),
                () -> {
                    final String expectedFirstProduct = "Test.allTheThings() T-Shirt (Red)";
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
