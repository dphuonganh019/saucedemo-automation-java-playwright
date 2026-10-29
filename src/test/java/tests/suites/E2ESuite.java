package tests.suites;

import config.ParallelSuite;
import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

// Chạy riêng module E2E: mvn test -Dtest=E2ESuite
@Suite
@ParallelSuite
@SuiteDisplayName("E2E Suite")
@SelectPackages("tests.e2e")
@IncludeClassNamePatterns("^.*\\.TC_.*$")
public class E2ESuite {
}
