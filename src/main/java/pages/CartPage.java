package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import utils.ConfigReader;
import utils.LogUtil;

import java.util.List;

public class CartPage extends BasePage {
    private final Locator cartList = page.locator("//div[@data-test='cart-list']");
    private final Locator quantityLabel = page.locator("//div[@data-test='cart-quantity-label']");
    private final Locator descriptionLabel = page.locator("//div[@data-test='cart-desc-label']");
    // Trang Inventory cũng có data-test='inventory-item' -> lấy theo class cart_item để không bắt nhầm card ở trang trước
    private final Locator cartItems = page.locator("//div[@class='cart_item']");
    private final Locator cartItemNames = page.locator("//div[@class='cart_item']//div[@data-test='inventory-item-name']");
    // Control thay đổi số lượng (nếu có) sẽ là input hoặc button khác nút Remove
    private final Locator quantityControls = page.locator("//div[@data-test='cart-list']//input"
            + " | //div[@data-test='cart-list']//button[not(starts-with(@data-test,'remove'))]");
    private final Locator errorMessage = page.locator("//h3[@data-test='error']");
    private final Locator continueShoppingButton = page.locator("//button[@data-test='continue-shopping']");
    private final Locator checkoutButton = page.locator("//button[@data-test='checkout']");

    public CartPage(Page page) {
        super(page);
    }

    // Truy cập thẳng URL cart.html (dùng cho case mở lại giỏ hàng trong browser mới)
    public void goToCartPageByUrl() {
        navigateToUrl(ConfigReader.get("baseUrl") + "cart.html");
    }

    // Playwright chỉ tự nhận XPath khi chuỗi bắt đầu bằng // -> XPath tương đối .// phải thêm tiền tố "xpath="
    private Locator getCartItem(String productName) {
        return page.locator("//div[@class='cart_item'][.//div[@data-test='inventory-item-name' and text()='"
                + productName + "']]");
    }

    private Locator getCartItemPrice(String productName) {
        return getCartItem(productName).locator("xpath=.//div[@data-test='inventory-item-price']");
    }

    private Locator getCartItemQuantity(String productName) {
        return getCartItem(productName).locator("xpath=.//div[@data-test='item-quantity']");
    }

    private Locator getRemoveButton(String productName) {
        return getCartItem(productName).locator("xpath=.//button[starts-with(@data-test,'remove')]");
    }

    public boolean isCartListVisible() {
        return isElementVisible(cartList);
    }

    // Cart có thể rỗng -> chờ cart list hiển thị (trang đã load) rồi mới đếm, không chờ item
    public int getCartItemCount() {
        isCartListVisible();
        final int itemCount = getElementCount(cartItems);
        LogUtil.info("Number of items in cart: " + itemCount);
        return itemCount;
    }

    public List<String> getCartItemNames() {
        isCartListVisible();
        final List<String> names = cartItemNames.allInnerTexts();
        LogUtil.info("Items in cart: " + names);
        return names;
    }

    public boolean isProductInCart(String productName) {
        return isElementVisible(getCartItem(productName));
    }

    public boolean isProductRemovedFromCart(String productName) {
        return isElementHidden(getCartItem(productName));
    }

    public String getCartItemPriceText(String productName) {
        final String price = getInnerText(getCartItemPrice(productName));
        LogUtil.info("Price of [" + productName + "] in cart: " + price);
        return price;
    }

    public String getCartItemQuantityText(String productName) {
        final String quantity = getInnerText(getCartItemQuantity(productName));
        LogUtil.info("Quantity of [" + productName + "] in cart: " + quantity);
        return quantity;
    }

    public void removeProduct(String productName) {
        LogUtil.info("Remove from cart page: " + productName);
        clickElement(getRemoveButton(productName));
    }

    public int getQuantityControlCount() {
        isCartListVisible();
        final int controlCount = getElementCount(quantityControls);
        LogUtil.info("Number of quantity controls: " + controlCount);
        return controlCount;
    }

    public int getErrorMessageCount() {
        return getElementCount(errorMessage);
    }

    public String getQuantityLabelText() {
        final String label = getInnerText(quantityLabel);
        LogUtil.info("Quantity column header: " + label);
        return label;
    }

    public String getDescriptionLabelText() {
        final String label = getInnerText(descriptionLabel);
        LogUtil.info("Description column header: " + label);
        return label;
    }

    public double getQuantityLabelPositionX() {
        final double positionX = getElementXPosition(quantityLabel);
        LogUtil.info("QTY header position X: " + positionX);
        return positionX;
    }

    public double getDescriptionLabelPositionX() {
        final double positionX = getElementXPosition(descriptionLabel);
        LogUtil.info("Description header position X: " + positionX);
        return positionX;
    }

    public double getQuantityLabelPositionY() {
        final double positionY = getElementYPosition(quantityLabel);
        LogUtil.info("QTY header position Y: " + positionY);
        return positionY;
    }

    public double getFirstCartItemPositionY() {
        final double positionY = getElementYPosition(cartItems.first());
        LogUtil.info("First cart item position Y: " + positionY);
        return positionY;
    }

    public boolean isContinueShoppingButtonVisible() {
        return isElementVisible(continueShoppingButton);
    }

    public boolean isContinueShoppingButtonEnabled() {
        return isElementEnabled(continueShoppingButton);
    }

    public void clickContinueShopping() {
        clickElement(continueShoppingButton);
    }

    public boolean isCheckoutButtonVisible() {
        return isElementVisible(checkoutButton);
    }

    public boolean isCheckoutButtonEnabled() {
        return isElementEnabled(checkoutButton);
    }

    public void clickCheckout() {
        clickElement(checkoutButton);
    }
}
