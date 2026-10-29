package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import utils.LogUtil;

// Header dùng chung cho mọi màn hình sau khi login (Inventory, Product Detail, Cart, Checkout)
public class HeaderComponent extends BasePage {
    private final Locator menuIcon = page.locator("//img[@data-test='open-menu']");
    // Icon <img> bị <button> bọc ngoài che mất pointer event -> phải click vào button
    private final Locator menuButton = page.locator("//button[@id='react-burger-menu-btn']");
    private final Locator appLogo = page.locator("//div[@class='app_logo']");
    private final Locator cartIcon = page.locator("//a[@data-test='shopping-cart-link']");
    private final Locator cartBadge = page.locator("//span[@data-test='shopping-cart-badge']");
    private final Locator pageTitle = page.locator("//span[@data-test='title']");

    public HeaderComponent(Page page) {
        super(page);
    }

    public boolean isMenuIconVisible() {
        return isElementVisible(menuIcon);
    }

    public boolean isMenuButtonEnabled() {
        return isElementEnabled(menuButton);
    }

    public void clickMenuButton() {
        clickElement(menuButton);
    }

    public boolean isAppLogoVisible() {
        return isElementVisible(appLogo);
    }

    public String getAppLogoText() {
        final String logoText = getInnerText(appLogo);
        LogUtil.info("App logo text: " + logoText);
        return logoText;
    }

    public boolean isCartIconVisible() {
        return isElementVisible(cartIcon);
    }

    public void clickCartIcon() {
        clickElement(cartIcon);
    }

    public String getCartBadgeText() {
        final String badgeText = getInnerText(cartBadge);
        LogUtil.info("Cart badge: " + badgeText);
        return badgeText;
    }

    // Cart rỗng thì badge bị xoá hẳn khỏi DOM (không phải chỉ bị ẩn)
    public boolean isCartBadgeRemoved() {
        final boolean isHidden = isElementHidden(cartBadge);
        final int badgeCount = getElementCount(cartBadge);
        LogUtil.info("Number of cart badge elements in DOM: " + badgeCount);
        return isHidden && badgeCount == 0;
    }

    public boolean isPageTitleVisible() {
        return isElementVisible(pageTitle);
    }

    public String getPageTitle() {
        final String title = getInnerText(pageTitle);
        LogUtil.info("Page title: " + title);
        return title;
    }
}
