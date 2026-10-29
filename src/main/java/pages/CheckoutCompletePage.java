package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import utils.LogUtil;

// Checkout Complete: checkout-complete.html
public class CheckoutCompletePage extends BasePage {
    private final Locator completeHeader = page.locator("//h2[@data-test='complete-header']");
    private final Locator completeText = page.locator("//div[@data-test='complete-text']");
    private final Locator ponyExpressImage = page.locator("//img[@data-test='pony-express']");
    private final Locator backHomeButton = page.locator("//button[@data-test='back-to-products']");

    public CheckoutCompletePage(Page page) {
        super(page);
    }

    public String getCompleteHeader() {
        final String header = getInnerText(completeHeader);
        LogUtil.info("Complete header: " + header);
        return header;
    }

    public String getCompleteText() {
        final String text = getInnerText(completeText);
        LogUtil.info("Complete text: " + text);
        return text;
    }

    public boolean isPonyExpressImageLoaded() {
        final boolean isLoaded = isImageLoaded(ponyExpressImage);
        LogUtil.info("Pony Express image loaded: " + isLoaded);
        return isLoaded;
    }

    public boolean isBackHomeButtonVisible() {
        return isElementVisible(backHomeButton);
    }

    public boolean isBackHomeButtonEnabled() {
        return isElementEnabled(backHomeButton);
    }

    public void clickBackHome() {
        clickElement(backHomeButton);
    }
}
