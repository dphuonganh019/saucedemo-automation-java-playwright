package report;


import org.junit.jupiter.api.extension.BeforeEachCallback;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.junit.jupiter.api.extension.*;
import org.opentest4j.MultipleFailuresError;
import utils.LogUtil;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ExtentReportExtension implements BeforeEachCallback, TestWatcher {
    private static final ExtentReports extent = ReportManager.getInstance();
    private static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create("report");
    private static final Map<String, ExtentTest> testCaseMap = new ConcurrentHashMap<>();

    @Override
    public void beforeEach(ExtensionContext context) {
        String testCaseName = context.getParent()
                .map(ExtensionContext::getDisplayName)
                .orElse(context.getRequiredTestClass().getSimpleName());

        ExtentTest testCase = testCaseMap.computeIfAbsent(testCaseName, extent::createTest);
        ExtentTest step = testCase.createNode(context.getDisplayName());

        context.getStore(NAMESPACE).put("extentTest", step);
        LogUtil.setCurrentTest(step);
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        getTest(context).log(Status.PASS, "Test passed");
        LogUtil.clear();
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        ExtentTest test = getTest(context);
        String testName = context.getRequiredTestClass().getSimpleName()
                + "_" + context.getRequiredTestMethod().getName();
        LogUtil.captureFailureScreenshot(testName);
        if (cause instanceof MultipleFailuresError multipleFailuresError) {
            for (Throwable failure : multipleFailuresError.getFailures()) {
                test.log(Status.FAIL, escapeHtml(failure.getMessage()));
            }
        } else {
            test.log(Status.FAIL, escapeHtml(cause.getMessage()));
        }
        LogUtil.clear();
    }

    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        getTest(context).log(Status.SKIP, "Blocked: " + escapeHtml(cause.getMessage()));
        LogUtil.clear();
    }

    private ExtentTest getTest(ExtensionContext context) {
        return context.getStore(NAMESPACE).get("extentTest", ExtentTest.class);
    }

    // ExtentReports log() nhận raw HTML (để hỗ trợ chèn markup), nên message thật
    // (vd assertEquals tự sinh "expected: <x> but was: <y>") phải escape thủ công,
    // không thì trình duyệt hiểu nhầm <...> là thẻ HTML và nuốt mất phần chữ bên trong.
    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
