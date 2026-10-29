package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import utils.LogUtil;

import java.util.List;

// Sidebar (hamburger menu) mở ra từ HeaderComponent.clickMenuButton()
public class SideMenuComponent extends BasePage {
    private final Locator menuItems = page.locator("//div[@class='bm-menu-wrap']//a[contains(@class,'bm-item')]");
    private final Locator allItemsLink = page.locator("//a[@data-test='inventory-sidebar-link']");
    private final Locator aboutLink = page.locator("//a[@data-test='about-sidebar-link']");
    private final Locator logoutLink = page.locator("//a[@data-test='logout-sidebar-link']");
    private final Locator resetAppStateLink = page.locator("//a[@data-test='reset-sidebar-link']");
    private final Locator closeIcon = page.locator("//img[@data-test='close-menu']");
    // Giống icon mở menu: <img> bị <button> bọc ngoài che mất pointer event -> phải click vào button
    private final Locator closeButton = page.locator("//button[@id='react-burger-cross-btn']");
    private final Locator openedMenu = page.locator("//div[@class='bm-menu-wrap' and @aria-hidden='false']");

    public SideMenuComponent(Page page) {
        super(page);
    }

    // Tìm menu item theo text hiển thị, dùng cho các step verify từng item
    private Locator getMenuItem(String itemText) {
        return page.locator("//div[@class='bm-menu-wrap']//a[contains(@class,'bm-item') and text()='" + itemText + "']");
    }

    public boolean isMenuOpened() {
        return isElementVisible(openedMenu) && isElementVisible(allItemsLink);
    }

    // Khi sidebar đóng, các link vẫn còn trong DOM nhưng bị ẩn
    public boolean isMenuClosed() {
        return isElementHidden(allItemsLink);
    }

    public List<String> getMenuItemTexts() {
        final List<String> itemTexts = getListInnerText(menuItems);
        LogUtil.info("Menu items: " + itemTexts);
        return itemTexts;
    }

    public boolean isMenuItemVisible(String itemText) {
        return isElementVisible(getMenuItem(itemText));
    }

    public boolean isMenuItemEnabled(String itemText) {
        return isElementEnabled(getMenuItem(itemText));
    }

    public void clickAllItems() {
        clickElement(allItemsLink);
    }

    public String getAboutLinkHref() {
        final String href = getAttribute(aboutLink, "href");
        LogUtil.info("About link href: " + href);
        return href;
    }

    public void clickLogout() {
        clickElement(logoutLink);
    }

    public void clickResetAppState() {
        clickElement(resetAppStateLink);
    }

    public boolean isCloseIconVisible() {
        return isElementVisible(closeIcon);
    }

    public boolean isCloseButtonEnabled() {
        return isElementEnabled(closeButton);
    }

    public void clickCloseButton() {
        clickElement(closeButton);
    }
}
