package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import utils.ConfigReader;
import utils.LogUtil;

import java.util.List;

public class InventoryPage extends BasePage {
    private final Locator productCards = page.locator("//div[@data-test='inventory-item']");
    private final Locator productNames = page.locator("//div[@data-test='inventory-item-name']");
    private final Locator productPrices = page.locator("//div[@data-test='inventory-item-price']");
    private final Locator sortDropdown = page.locator("//select[@data-test='product-sort-container']");
    private final Locator sortOptions = page.locator("//select[@data-test='product-sort-container']/option");
    private final Locator activeSortOption = page.locator("//span[@data-test='active-option']");
    private final Locator productButtons = page.locator("//div[@data-test='inventory-item']//button");

    public InventoryPage(Page page) {
        super(page);
    }

    // Truy cập thẳng URL inventory.html (dùng cho case chưa login mà vào thẳng trang trong)
    public void goToInventoryPageByUrl() {
        navigateToUrl(ConfigReader.get("baseUrl") + "inventory.html");
    }

    public List<String> getAllProductButtonTexts() {
        final List<String> buttonTexts = getListInnerText(productButtons);
        LogUtil.info("Product buttons: " + buttonTexts);
        return buttonTexts;
    }

    // Tìm product card theo tên sản phẩm, các locator con bên dưới đều tìm trong phạm vi card này (bắt đầu bằng .//)
    // Playwright chỉ tự nhận XPath khi chuỗi bắt đầu bằng // -> XPath tương đối .// phải thêm tiền tố "xpath="
    private Locator getProductCard(String productName) {
        return page.locator("//div[@data-test='inventory-item'][.//div[@data-test='inventory-item-name' and text()='"
                + productName + "']]");
    }

    private Locator getProductImage(String productName) {
        return getProductCard(productName).locator("xpath=.//img");
    }

    private Locator getProductNameLink(String productName) {
        return getProductCard(productName).locator("xpath=.//a[contains(@data-test,'title-link')]");
    }

    private Locator getProductImageLink(String productName) {
        return getProductCard(productName).locator("xpath=.//a[contains(@data-test,'img-link')]");
    }

    private Locator getProductDescription(String productName) {
        return getProductCard(productName).locator("xpath=.//div[@data-test='inventory-item-desc']");
    }

    private Locator getProductPrice(String productName) {
        return getProductCard(productName).locator("xpath=.//div[@data-test='inventory-item-price']");
    }

    private Locator getProductButton(String productName) {
        return getProductCard(productName).locator("xpath=.//button");
    }

    private Locator getAddToCartButton(String productName) {
        return getProductCard(productName).locator("xpath=.//button[starts-with(@data-test,'add-to-cart')]");
    }

    private Locator getRemoveButton(String productName) {
        return getProductCard(productName).locator("xpath=.//button[starts-with(@data-test,'remove')]");
    }

    public int getProductCount() {
        final int productCount = getVisibleElementCount(productCards);
        LogUtil.info("Number of product cards: " + productCount);
        return productCount;
    }

    public List<String> getProductNames() {
        final List<String> names = getListInnerText(productNames);
        LogUtil.info("Product names: " + names);
        return names;
    }

    public List<String> getProductPrices() {
        final List<String> prices = getListInnerText(productPrices);
        LogUtil.info("Product prices: " + prices);
        return prices;
    }

    public boolean isProductDisplayed(String productName) {
        return isElementVisible(getProductCard(productName));
    }

    public boolean isProductNameVisible(String productName) {
        return isElementVisible(getProductNameLink(productName));
    }

    public boolean isProductImageLoaded(String productName) {
        final boolean isLoaded = isImageLoaded(getProductImage(productName));
        LogUtil.info("Image of [" + productName + "] loaded: " + isLoaded);
        return isLoaded;
    }

    public String getProductDescriptionText(String productName) {
        final String description = getInnerText(getProductDescription(productName));
        LogUtil.info("Description of [" + productName + "]: " + description);
        return description;
    }

    public String getProductPriceText(String productName) {
        final String price = getInnerText(getProductPrice(productName));
        LogUtil.info("Price of [" + productName + "]: " + price);
        return price;
    }

    public String getProductButtonText(String productName) {
        final String buttonText = getInnerText(getProductButton(productName));
        LogUtil.info("Button of [" + productName + "]: " + buttonText);
        return buttonText;
    }

    public void addProductToCart(String productName) {
        LogUtil.info("Add to cart: " + productName);
        clickElement(getAddToCartButton(productName));
    }

    public void removeProductFromCart(String productName) {
        LogUtil.info("Remove from cart: " + productName);
        clickElement(getRemoveButton(productName));
    }

    public void clickProductName(String productName) {
        clickElement(getProductNameLink(productName));
    }

    public void clickProductImage(String productName) {
        clickElement(getProductImageLink(productName));
    }

    public boolean isSortDropdownVisible() {
        return isElementVisible(sortDropdown);
    }

    public void clickSortDropdown() {
        clickElement(sortDropdown);
    }

    public void selectSortOption(String optionValue) {
        LogUtil.info("Select sort option: " + optionValue);
        selectOptionByValue(sortDropdown, optionValue);
    }

    public String getActiveSortOption() {
        final String activeOption = getInnerText(activeSortOption);
        LogUtil.info("Active sort option: " + activeOption);
        return activeOption;
    }

    // <option> nằm trong <select> đang đóng không được Playwright coi là visible -> lấy textContent, không chờ visible
    public List<String> getSortOptions() {
        isElementVisible(sortDropdown);
        final List<String> options = sortOptions.allTextContents();
        LogUtil.info("Sort options: " + options);
        return options;
    }
}
