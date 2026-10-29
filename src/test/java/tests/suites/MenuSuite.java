package tests.suites;

import config.ParallelSuite;
import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

// Chạy riêng module Menu: mvn test -Dtest=MenuSuite
@Suite
@ParallelSuite
@SuiteDisplayName("Menu Suite")
@SelectPackages("tests.menu")
@IncludeClassNamePatterns("^.*\\.TC_.*$")
public class MenuSuite {
}
