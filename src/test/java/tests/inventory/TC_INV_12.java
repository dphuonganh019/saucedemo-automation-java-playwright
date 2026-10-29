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

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TC_INV_12: Detailed verification of the header UI on the Inventory page")
public class TC_INV_12 extends BaseTest {
    static LoginPage loginPage;
    static InventoryPage inventoryPage;
    static HeaderComponent header;

    private static final String EXPECTED_LOGO_TEXT = "Swag Labs";
    private static final String EXPECTED_PAGE_TITLE = "Products";
    private static final String EXPECTED_DEFAULT_SORT_OPTION = "Name (A to Z)";
    private static final List<String> EXPECTED_SORT_OPTIONS = List.of(
            "Name (A to Z)",
            "Name (Z to A)",
            "Price (low to high)",
            "Price (high to low)"
    );

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
    @DisplayName("Step 1: Verify the hamburger menu icon is displayed on the left of the header</br>" +
            "Expected Results: Menu icon is displayed and clickable")
    public void verifyMenuIcon() {
        assertAll(
                () -> assertTrue(
                        header.isMenuIconVisible(),
                        "The menu icon is not displayed as expected"
                ),
                () -> assertTrue(
                        header.isMenuButtonEnabled(),
                        "The menu icon is not clickable as expected"
                )
        );
    }

    @Test
    @Order(2)
    @DisplayName("Step 2: Verify the [Swag Labs] logo text is displayed in the center of the header</br>" +
            "Expected Results: [Swag Labs] text renders correctly")
    public void verifyAppLogo() {
        assertAll(
                () -> assertTrue(
                        header.isAppLogoVisible(),
                        "The app logo is not displayed as expected"
                ),
                () -> {
                    final String actualLogoText = header.getAppLogoText();
                    assertEquals(
                            EXPECTED_LOGO_TEXT,
                            actualLogoText,
                            "The app logo text is not as expected. Expected: ["
                                    + EXPECTED_LOGO_TEXT
                                    + "], Actual: ["
                                    + actualLogoText
                                    + "]"
                    );
                }
        );
    }

    @Test
    @Order(3)
    @DisplayName("Step 3: Verify the cart icon is displayed on the right of the header</br>" +
            "Expected Results: Cart icon is displayed, no badge present (cart is empty)")
    public void verifyCartIcon() {
        assertAll(
                () -> assertTrue(
                        header.isCartIconVisible(),
                        "The cart icon is not displayed as expected"
                ),
                () -> assertTrue(
                        header.isCartBadgeRemoved(),
                        "The cart badge is displayed although the cart is empty"
                )
        );
    }

    @Test
    @Order(4)
    @DisplayName("Step 4: Verify the page title [Products] is displayed</br>" +
            "Expected Results: [Products] title is displayed correctly")
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
    @Order(5)
    @DisplayName("Step 5: Verify the sort dropdown is displayed with the default value [Name (A to Z)]</br>" +
            "Expected Results: Dropdown defaults to [Name (A to Z)], with all 4 options present")
    public void verifySortDropdown() {
        assertAll(
                () -> assertTrue(
                        inventoryPage.isSortDropdownVisible(),
                        "The sort dropdown is not displayed as expected"
                ),
                () -> {
                    final String actualActiveOption = inventoryPage.getActiveSortOption();
                    assertEquals(
                            EXPECTED_DEFAULT_SORT_OPTION,
                            actualActiveOption,
                            "The default sort option is not as expected. Expected: ["
                                    + EXPECTED_DEFAULT_SORT_OPTION
                                    + "], Actual: ["
                                    + actualActiveOption
                                    + "]"
                    );
                },
                () -> {
                    final List<String> actualSortOptions = inventoryPage.getSortOptions();
                    assertEquals(
                            EXPECTED_SORT_OPTIONS,
                            actualSortOptions,
                            "The sort options are not as expected. Expected: ["
                                    + EXPECTED_SORT_OPTIONS
                                    + "], Actual: ["
                                    + actualSortOptions
                                    + "]"
                    );
                }
        );
    }
}
