package tests.suites;

import config.ParallelSuite;
import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

// Chạy riêng module Inventory: mvn test -Dtest=InventorySuite
@Suite
@ParallelSuite
@SuiteDisplayName("Inventory Suite")
@SelectPackages("tests.inventory")
@IncludeClassNamePatterns("^.*\\.TC_.*$")
public class InventorySuite {
}
