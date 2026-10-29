package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import utils.LogUtil;

import java.math.BigDecimal;
import java.util.List;

// Checkout Step Two: checkout-step-two.html (Checkout: Overview)
public class CheckoutOverviewPage extends BasePage {
    private final Locator overviewItemNames = page.locator("//div[@data-test='checkout-summary-container']"
            + "//div[@class='cart_item']//div[@data-test='inventory-item-name']");
    private final Locator paymentInfo = page.locator("//div[@data-test='payment-info-value']");
    private final Locator shippingInfo = page.locator("//div[@data-test='shipping-info-value']");
    private final Locator subtotalLabel = page.locator("//div[@data-test='subtotal-label']");
    private final Locator taxLabel = page.locator("//div[@data-test='tax-label']");
    private final Locator totalLabel = page.locator("//div[@data-test='total-label']");
    // Trang Checkout Step One cũng có nút data-test='cancel' -> trước khi click phải chờ nút Finish (chỉ có ở Overview)
    private final Locator cancelButton = page.locator("//button[@data-test='cancel']");
    private final Locator finishButton = page.locator("//button[@data-test='finish']");

    public CheckoutOverviewPage(Page page) {
        super(page);
    }

    private Locator getOverviewItemPrice(String productName) {
        return page.locator("//div[@data-test='checkout-summary-container']//div[@class='cart_item']"
                + "[.//div[@data-test='inventory-item-name' and text()='" + productName + "']]"
                + "//div[@data-test='inventory-item-price']");
    }

    // "Item total: $29.99" -> 29.99
    private BigDecimal parseAmount(String labelText) {
        return new BigDecimal(labelText.substring(labelText.indexOf('$') + 1).trim());
    }

    public List<String> getOverviewItemNames() {
        final List<String> names = getListInnerText(overviewItemNames);
        LogUtil.info("Items in overview: " + names);
        return names;
    }

    public String getOverviewItemPriceText(String productName) {
        final String price = getInnerText(getOverviewItemPrice(productName));
        LogUtil.info("Price of [" + productName + "] in overview: " + price);
        return price;
    }

    public String getPaymentInformation() {
        final String payment = getInnerText(paymentInfo);
        LogUtil.info("Payment Information: " + payment);
        return payment;
    }

    public String getShippingInformation() {
        final String shipping = getInnerText(shippingInfo);
        LogUtil.info("Shipping Information: " + shipping);
        return shipping;
    }

    public String getSubtotalText() {
        final String subtotal = getInnerText(subtotalLabel);
        LogUtil.info("Subtotal: " + subtotal);
        return subtotal;
    }

    public String getTaxText() {
        final String tax = getInnerText(taxLabel);
        LogUtil.info("Tax: " + tax);
        return tax;
    }

    public String getTotalText() {
        final String total = getInnerText(totalLabel);
        LogUtil.info("Total: " + total);
        return total;
    }

    public BigDecimal getSubtotalAmount() {
        return parseAmount(getSubtotalText());
    }

    public BigDecimal getTaxAmount() {
        return parseAmount(getTaxText());
    }

    public BigDecimal getTotalAmount() {
        return parseAmount(getTotalText());
    }

    public boolean isFinishButtonVisible() {
        return isElementVisible(finishButton);
    }

    public boolean isFinishButtonEnabled() {
        return isElementEnabled(finishButton);
    }

    public boolean isCancelButtonVisible() {
        return isElementVisible(cancelButton);
    }

    public boolean isCancelButtonEnabled() {
        return isElementEnabled(cancelButton);
    }

    public void clickCancel() {
        isFinishButtonVisible();
        clickElement(cancelButton);
    }

    public void clickFinish() {
        clickElement(finishButton);
    }
}
