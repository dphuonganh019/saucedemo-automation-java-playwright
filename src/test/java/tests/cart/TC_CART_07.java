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

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TC_CART_07: Verify the QTY and Description column headers on the Cart page")
public class TC_CART_07 extends BaseTest {
    static LoginPage loginPage;
    static InventoryPage inventoryPage;
    static HeaderComponent header;
    static CartPage cartPage;

    private static final String EXPECTED_QTY_LABEL = "QTY";
    private static final String EXPECTED_DESC_LABEL = "Description";

    @BeforeAll
    static void setupPrecondition() {
        loginPage = new LoginPage(page());
        inventoryPage = new InventoryPage(page());
        header = new HeaderComponent(page());
        cartPage = new CartPage(page());
        loginPage.goToSauceDemoPage();
        loginPage.loginAs(ConfigReader.get("username_standard_user"), ConfigReader.get("password"));
        inventoryPage.addProductToCart("Sauce Labs Backpack");
        header.clickCartIcon();
    }

    @Test
    @Order(1)
    @DisplayName("Step 1: Verify the [QTY] column is displayed with the correct text, in the correct position</br>" +
            "Expected Results: [QTY] column shows the correct text, positioned above the product list")
    public void verifyQuantityLabel() {
        final String actualQtyLabel = cartPage.getQuantityLabelText();
        final double qtyLabelPositionY = cartPage.getQuantityLabelPositionY();
        final double firstItemPositionY = cartPage.getFirstCartItemPositionY();
        assertAll(
                () -> assertEquals(
                        EXPECTED_QTY_LABEL,
                        actualQtyLabel,
                        "The QTY column header is not as expected. Expected: ["
                                + EXPECTED_QTY_LABEL
                                + "], Actual: ["
                                + actualQtyLabel
                                + "]"
                ),
                () -> assertTrue(
                        qtyLabelPositionY < firstItemPositionY,
                        "The QTY column header is not positioned above the product list"
                )
        );
    }

    @Test
    @Order(2)
    @DisplayName("Step 2: Verify the [Description] column is displayed with the correct text, in the correct position</br>" +
            "Expected Results: [Description] column shows the correct text")
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
    @Order(3)
    @DisplayName("Step 3: Verify the display order: QTY on the left, Description on the right</br>" +
            "Expected Results: QTY -> Description order matches the original design")
    public void verifyColumnOrder() {
        final double qtyLabelPositionX = cartPage.getQuantityLabelPositionX();
        final double descLabelPositionX = cartPage.getDescriptionLabelPositionX();
        assertTrue(
                qtyLabelPositionX < descLabelPositionX,
                "The QTY column header is not positioned on the left of the Description column header"
        );
    }
}
