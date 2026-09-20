package tests;

import config.FlowSuite;
import config.ParallelSuite;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@ParallelSuite
@SelectClasses({
        TC_LOGIN_01.class,
        TC_LOGIN_02.class,
        TC_LOGIN_10.class
})
public class EndToEndSystemTestSuite {
}
