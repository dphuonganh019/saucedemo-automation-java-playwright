package tests.suites;

import config.ParallelSuite;
import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

// Chạy riêng module Login: mvn test -Dtest=LoginSuite
@Suite
@ParallelSuite
@SuiteDisplayName("Login Suite")
@SelectPackages("tests.login")
@IncludeClassNamePatterns("^.*\\.TC_.*$")
public class LoginSuite {
}
