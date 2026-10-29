package pages;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.options.WaitForSelectorState;
import java.util.List;

abstract class BasePage {
    protected Page page;

    protected BasePage(Page page) {
        this.page = page;
    }

    // Build 1 hàm chung để chờ locator visible, rồi sử dụng lại trong các hàm thực hiện action
    // Như vậy thì khi gọi test, sẽ biết rõ nếu fail ở step sử dụng thì là do locator không hiển thị (lỗi timeout)
    // hay là locator đã hiển thị rồi nhưng không tương tác được
    private void waitElementVisible(Locator locator){
        locator.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
    }

    private void waitElementVisible(Locator locator, int timeoutMs){
        locator.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(timeoutMs));
    }

    protected void clickElement(Locator locator) {
        waitElementVisible(locator);
        locator.click();
    }

    // Overload dùng cho các trường hợp cần chờ lâu hơn timeout mặc định (vd: performance_glitch_user)
    protected void clickElement(Locator locator, int timeoutMs) {
        waitElementVisible(locator, timeoutMs);
        locator.click(new Locator.ClickOptions().setTimeout(timeoutMs));
    }

    protected void fillText(Locator locator, String text){
        waitElementVisible(locator);
        locator.fill(text);
    }

    protected String getInnerText(Locator locator) {
        waitElementVisible(locator);
        return locator.innerText();
    }

    protected String getTextContent(Locator locator) {
        waitElementVisible(locator);
        return locator.textContent();
    }

    protected void navigateToUrl(String url){
        page.navigate(url);
    }

    protected String getUrl() {
        return page.url();
    }

    // Hàm này để chờ element 1 tgian tới khi nó hiển thị, nếu nó hiện thì thì true,
    // mà hết timeout không hiển thị trả false
    protected boolean isElementVisible(Locator locator) {
        try {
            waitElementVisible(locator);
            return true;
        } catch (PlaywrightException e) {
            return false;
        }
    }

    // Ngược lại với isElementVisible: chờ element biến mất (hidden hoặc bị xoá khỏi DOM),
    // hết timeout mà vẫn còn hiển thị thì trả false
    protected boolean isElementHidden(Locator locator) {
        try {
            locator.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN));
            return true;
        } catch (PlaywrightException e) {
            return false;
        }
    }

    protected boolean isElementEnabled(Locator locator) {
        try {
            waitElementVisible(locator);
            return locator.isEnabled();
        } catch (PlaywrightException e) {
            return false;
        }
    }

    protected String getPlaceHolderValue(Locator locator) {
        waitElementVisible(locator);
        return locator.getAttribute("placeholder");
    }

    protected String getAttributeValue(Locator locator) {
        waitElementVisible(locator);
        return locator.getAttribute("value");
    }

    // Locator trỏ tới nhiều element -> chỉ chờ element đầu tiên, vì waitFor() trên nhiều element sẽ vi phạm strict mode
    protected List<String> getListInnerText(Locator locator){
        waitElementVisible(locator.first());
        return locator.allInnerTexts();
    }

    protected List<String> getListTextContent(Locator locator){
        waitElementVisible(locator.first());
        return locator.allTextContents();
    }

    protected double getElementYPosition(Locator locator)
    {
        waitElementVisible(locator);
        return locator.boundingBox().y;
    }

    protected double getElementXPosition(Locator locator)
    {
        waitElementVisible(locator);
        return locator.boundingBox().x;
    }

    protected String getCssValue(Locator locator, String cssProperty) {
        waitElementVisible(locator);
        return (String) locator.evaluate(
                "(el, prop) => window.getComputedStyle(el).getPropertyValue(prop)", cssProperty
        );
    }

    protected String getAttribute(Locator locator, String attributeName) {
        waitElementVisible(locator);
        return locator.getAttribute(attributeName);
    }

    protected void selectOptionByValue(Locator locator, String value) {
        waitElementVisible(locator);
        locator.selectOption(value);
    }

    // Đếm số element đang có trong DOM tại thời điểm gọi, không chờ (dùng để kiểm tra element KHÔNG tồn tại, vd: cart badge)
    protected int getElementCount(Locator locator) {
        return locator.count();
    }

    // Đếm số element sau khi chờ element đầu tiên hiển thị (dùng cho danh sách chắc chắn có dữ liệu, vd: product cards)
    protected int getVisibleElementCount(Locator locator) {
        waitElementVisible(locator.first());
        return locator.count();
    }

    // Ảnh bị broken thì naturalWidth = 0
    protected boolean isImageLoaded(Locator locator) {
        waitElementVisible(locator);
        return (Boolean) locator.evaluate("img => img.complete && img.naturalWidth > 0");
    }
}
