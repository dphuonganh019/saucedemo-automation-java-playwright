package tests.suites;

import config.ParallelSuite;
import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

// Chạy toàn bộ testcase của mọi module (tests.login, tests.inventory, ...).
// Class testcase đặt tên TC_* không khớp pattern mặc định của @Suite (*Test/*Tests) nên phải khai báo lại pattern
@Suite
@ParallelSuite
@SuiteDisplayName("Regression Suite - All modules")
@SelectPackages("tests")
@IncludeClassNamePatterns("^.*\\.TC_.*$")
public class RegressionSuite {
}
