package tests.suites;

import config.ParallelSuite;
import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

// Chạy riêng module Cart: mvn test -Dtest=CartSuite
@Suite
@ParallelSuite
@SuiteDisplayName("Cart Suite")
@SelectPackages("tests.cart")
@IncludeClassNamePatterns("^.*\\.TC_.*$")
public class CartSuite {
}
