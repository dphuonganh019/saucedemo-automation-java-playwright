package tests.suites;

import config.ParallelSuite;
import org.junit.platform.suite.api.ExcludePackages;
import org.junit.platform.suite.api.ExcludeTags;
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
// tests.flow là các phase phụ thuộc nhau, không chạy song song được -> chạy riêng bằng UserJourneySuite (@FlowSuite)
@ExcludePackages("tests.flow")
// Testcase bắt bug đã biết (known-bug) chạy riêng trong KnownBugSuite để không làm đỏ CI
@ExcludeTags("known-bug")
@IncludeClassNamePatterns("^.*\\.TC_.*$")
public class RegressionSuite {
}
