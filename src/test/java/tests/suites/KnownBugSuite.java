package tests.suites;

import config.ParallelSuite;
import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

// Chạy riêng các testcase đang bắt bug đã biết của site (@Tag("known-bug")): mvn test -Dtest=KnownBugSuite
// Tách khỏi RegressionSuite để bug đã biết không làm đỏ CI, nhưng test vẫn chạy mỗi lần và vẫn có report
@Suite
@ParallelSuite
@SuiteDisplayName("Known Bug Suite - quarantined test cases")
@SelectPackages("tests")
@IncludeClassNamePatterns("^.*\\.TC_.*$")
@IncludeTags("known-bug")
public class KnownBugSuite {
}
