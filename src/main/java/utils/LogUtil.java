package utils;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.microsoft.playwright.Page;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogUtil {
    private static final ThreadLocal<ExtentTest> currentTest = new ThreadLocal<>();
    private static final ThreadLocal<Page> currentPage = new ThreadLocal<>();
    private static final String SCREENSHOT_DIR = "test-output/screenshots/";

    private LogUtil() {
    }

    public static void setCurrentTest(ExtentTest test) {
        currentTest.set(test);
    }

    public static void clear() {
        currentTest.remove();
    }

    public static void setCurrentPage(Page page) {
        currentPage.set(page);
    }

    public static void clearPage() {
        currentPage.remove();
    }

    public static void info(String message) {
        currentTest.get().log(Status.INFO, message);
    }

    public static void screenshot(Page page, String stepName) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyyyy_HHmmss_SSS"));
        String fileName = stepName + "_" + timestamp + ".png";

        Path filePath = Paths.get(SCREENSHOT_DIR + fileName);
        page.screenshot(new Page.ScreenshotOptions().setPath(filePath));

        String reportImagePath = "screenshots/" + fileName;
        currentTest.get().log(Status.INFO, stepName,
                MediaEntityBuilder.createScreenCaptureFromPath(reportImagePath).build());
    }

    public static void captureFailureScreenshot(String testName) {
        Page page = currentPage.get();
        if (page == null) return;
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("ddMMyyyy_HHmmss_SSS"));
        String fileName = "FAILURE_" + sanitizeForFileName(testName) + "_" + timestamp + ".png";

        Path filePath = Paths.get(SCREENSHOT_DIR + fileName);
        page.screenshot(new Page.ScreenshotOptions().setPath(filePath).setFullPage(true));

        String reportImagePath = "screenshots/" + fileName;
        currentTest.get().log(Status.FAIL, "Failure Screenshot",
                MediaEntityBuilder.createScreenCaptureFromPath(reportImagePath).build());
    }

    // Ten test co the chua ky tu khong hop le tren he thong file (vd dau ngoac trong displayName)
    private static String sanitizeForFileName(String name) {
        return name.replaceAll("[^a-zA-Z0-9_-]", "_");
    }
}