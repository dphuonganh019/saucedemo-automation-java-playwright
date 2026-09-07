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

    protected void clickElement(Locator locator) {
        waitElementVisible(locator);
        locator.click();
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

    protected List<String> getListInnerText(Locator locator){
        waitElementVisible(locator);
        return locator.allInnerTexts();
    }

    protected List<String> getListTextContent(Locator locator){
        waitElementVisible(locator);
        return locator.allTextContents();
    }

    protected double getElementYPosition(Locator locator)
    {
        waitElementVisible(locator);
        return locator.boundingBox().y;
    }

    protected String getCssValue(Locator locator, String cssProperty) {
        waitElementVisible(locator);
        return (String) locator.evaluate(
                "(el, prop) => window.getComputedStyle(el).getPropertyValue(prop)", cssProperty
        );
    }
}