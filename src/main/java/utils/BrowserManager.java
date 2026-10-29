package utils;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;

public class BrowserManager {
    private final BrowserContext context;
    private Page currentTab;

    public BrowserManager(BrowserContext context, Page initialTab) {
        this.context = context;
        this.currentTab = initialTab;
    }

    public String getCurrentTabUrl() {
        LogUtil.info("The current URL is: " + currentTab.url());
        return currentTab.url();
    }

    // Chờ URL chuyển sang expectedUrl rồi mới đọc, tránh đọc nhầm URL của trang trước khi vừa click chuyển trang.
    // Hết timeout mà URL vẫn chưa đổi thì không throw, trả về URL hiện tại để assertion báo lỗi rõ ràng (Expected/Actual)
    public String getCurrentTabUrl(String expectedUrl) {
        try {
            currentTab.waitForURL(expectedUrl);
        } catch (PlaywrightException e) {
            LogUtil.info("URL did not change to [" + expectedUrl + "] within the timeout");
        }
        return getCurrentTabUrl();
    }
}
