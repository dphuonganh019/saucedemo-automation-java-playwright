package tests;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import report.ExtentReportExtension;
import utils.BrowserManager;
import utils.ConfigReader;
import utils.LogUtil;

@ExtendWith(ExtentReportExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BaseTest {
    static ThreadLocal<Playwright> playwrightTL = new ThreadLocal<>();
    static ThreadLocal<Browser> browserTL = new ThreadLocal<>();
    static ThreadLocal<Page> pageTL = new ThreadLocal<>();
    static ThreadLocal<BrowserContext> contextTL = new ThreadLocal<>();
    static ThreadLocal<BrowserManager> browserManagerTL = new ThreadLocal<>();

    @BeforeAll
    static void launchBrowser() {
        Playwright pw = Playwright.create();
        Browser br = pw.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(ConfigReader.getBoolean("headless")));
        BrowserContext ctx = br.newContext();
        Page page = ctx.newPage();

        page.setDefaultTimeout(5000);

        playwrightTL.set(pw);
        browserTL.set(br);
        pageTL.set(page);
        contextTL.set(ctx);
        browserManagerTL.set(new BrowserManager(ctx, page));
        LogUtil.setCurrentPage(page);
    }

    @AfterAll
    static void closeBrowser() {
        LogUtil.clearPage();
        contextTL.get().close();
        browserTL.get().close();
        playwrightTL.get().close();
    }

    static Page page() { return pageTL.get(); }
    static Browser browser() { return browserTL.get(); }
    static BrowserContext context() { return contextTL.get(); }
    static BrowserManager browserManager() { return browserManagerTL.get(); }
}
