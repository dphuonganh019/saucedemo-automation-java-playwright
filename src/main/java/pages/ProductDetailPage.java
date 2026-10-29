package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import utils.LogUtil;

public class ProductDetailPage extends BasePage {
    // Trang Inventory cũng dùng các data-test inventory-item-name/desc/price -> phải giới hạn trong container của trang detail,
    // nếu không ngay sau khi click từ Inventory, locator sẽ bắt nhầm 6 card cũ (strict mode violation)
    private final Locator productName = page.locator("//div[@class='inventory_details_container']//div[@data-test='inventory-item-name']");
    private final Locator productDescription = page.locator("//div[@class='inventory_details_container']//div[@data-test='inventory-item-desc']");
    private final Locator productPrice = page.locator("//div[@class='inventory_details_container']//div[@data-test='inventory-item-price']");
    // Ảnh ở trang detail có data-test dạng item-<slug>-img nên lấy theo class
    private final Locator productImage = page.locator("//img[@class='inventory_details_img']");
    // Khác trang Inventory: nút ở trang detail có data-test chung là add-to-cart / remove, không kèm tên sản phẩm
    private final Locator addToCartButton = page.locator("//button[@data-test='add-to-cart']");
    private final Locator removeButton = page.locator("//button[@data-test='remove']");
    private final Locator backToProductsButton = page.locator("//button[@data-test='back-to-products']");

    public ProductDetailPage(Page page) {
        super(page);
    }

    public String getProductName() {
        final String name = getInnerText(productName);
        LogUtil.info("Product name: " + name);
        return name;
    }

    public String getProductDescription() {
        final String description = getInnerText(productDescription);
        LogUtil.info("Product description: " + description);
        return description;
    }

    public String getProductPrice() {
        final String price = getInnerText(productPrice);
        LogUtil.info("Product price: " + price);
        return price;
    }

    public boolean isProductImageLoaded() {
        final boolean isLoaded = isImageLoaded(productImage);
        LogUtil.info("Product image loaded: " + isLoaded);
        return isLoaded;
    }

    public boolean isAddToCartButtonVisible() {
        return isElementVisible(addToCartButton);
    }

    public boolean isAddToCartButtonEnabled() {
        return isElementEnabled(addToCartButton);
    }

    public String getAddToCartButtonText() {
        final String buttonText = getInnerText(addToCartButton);
        LogUtil.info("Add to cart button text: " + buttonText);
        return buttonText;
    }

    public void clickAddToCartButton() {
        clickElement(addToCartButton);
    }

    public boolean isRemoveButtonVisible() {
        return isElementVisible(removeButton);
    }

    public String getRemoveButtonText() {
        final String buttonText = getInnerText(removeButton);
        LogUtil.info("Remove button text: " + buttonText);
        return buttonText;
    }

    public void clickRemoveButton() {
        clickElement(removeButton);
    }

    public boolean isBackToProductsButtonVisible() {
        return isElementVisible(backToProductsButton);
    }

    public boolean isBackToProductsButtonEnabled() {
        return isElementEnabled(backToProductsButton);
    }

    public void clickBackToProductsButton() {
        clickElement(backToProductsButton);
    }
}
