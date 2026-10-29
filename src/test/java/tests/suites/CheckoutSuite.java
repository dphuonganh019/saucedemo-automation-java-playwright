package tests.suites;

import config.ParallelSuite;
import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

// Chạy riêng module Checkout: mvn test -Dtest=CheckoutSuite
@Suite
@ParallelSuite
@SuiteDisplayName("Checkout Suite")
@SelectPackages("tests.checkout")
@IncludeClassNamePatterns("^.*\\.TC_.*$")
public class CheckoutSuite {
}
