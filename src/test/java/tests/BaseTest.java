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

import java.nio.file.Path;

@ExtendWith(ExtentReportExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BaseTest {
    static ThreadLocal<Playwright> playwrightTL = new ThreadLocal<>();
    static ThreadLocal<Browser> browserTL = new ThreadLocal<>();
    static ThreadLocal<Page> pageTL = new ThreadLocal<>();
    static ThreadLocal<BrowserContext> contextTL = new ThreadLocal<>();
    static ThreadLocal<BrowserManager> browserManagerTL = new ThreadLocal<>();

    @BeforeAll
    protected static void launchBrowser() {
        Playwright pw = Playwright.create();
        Browser br = pw.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(ConfigReader.getBoolean("headless")));

        playwrightTL.set(pw);
        browserTL.set(br);
        openContext(new Browser.NewContextOptions());
    }

    // Lưu session hiện tại (cookie + localStorage) ra file để một class khác mở lại trong browser mới
    protected static void saveSession(Path stateFile) {
        contextTL.get().storageState(new BrowserContext.StorageStateOptions().setPath(stateFile));
    }

    // Đóng context mặc định, mở context mới từ file session đã lưu -> như người dùng quay lại bằng browser khác
    protected static void restoreSession(Path stateFile) {
        LogUtil.clearPage();
        contextTL.get().close();
        openContext(new Browser.NewContextOptions().setStorageStatePath(stateFile));
    }

    private static void openContext(Browser.NewContextOptions options) {
        BrowserContext ctx = browserTL.get().newContext(options);
        Page page = ctx.newPage();

        page.setDefaultTimeout(5000);
        // Khi nhiều class chạy song song, page.navigate thỉnh thoảng vượt 5s -> tách riêng timeout cho navigation
        page.setDefaultNavigationTimeout(15000);

        pageTL.set(page);
        contextTL.set(ctx);
        browserManagerTL.set(new BrowserManager(ctx, page));
        LogUtil.setCurrentPage(page);
    }

    @AfterAll
    protected static void closeBrowser() {
        LogUtil.clearPage();
        contextTL.get().close();
        browserTL.get().close();
        playwrightTL.get().close();
    }

    protected static Page page() { return pageTL.get(); }
    protected static Browser browser() { return browserTL.get(); }
    protected static BrowserContext context() { return contextTL.get(); }
    protected static BrowserManager browserManager() { return browserManagerTL.get(); }
}
