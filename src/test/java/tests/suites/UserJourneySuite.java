package tests.suites;

import config.FlowSuite;
import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

// Chạy riêng User Journey: mvn test -Dtest=UserJourneySuite
// 5 phase phụ thuộc nhau (phase sau mở browser mới từ session phase trước để lại) -> bắt buộc chạy tuần tự, đúng thứ tự @Order
@Suite
@FlowSuite
@SuiteDisplayName("User Journey Suite - sequential flow")
@SelectPackages("tests.flow")
@IncludeClassNamePatterns("^.*\\.TC_.*$")
public class UserJourneySuite {
}
