package tests.suites;

import config.ParallelSuite;
import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

// Chạy riêng module Product Detail: mvn test -Dtest=ProductDetailSuite
@Suite
@ParallelSuite
@SuiteDisplayName("Product Detail Suite")
@SelectPackages("tests.productdetail")
@IncludeClassNamePatterns("^.*\\.TC_.*$")
public class ProductDetailSuite {
}
