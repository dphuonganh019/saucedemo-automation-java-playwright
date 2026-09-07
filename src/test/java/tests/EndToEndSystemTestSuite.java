package tests;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        TC_LOGIN_01.class,
        TC_LOGIN_02.class,
        TC_LOGIN_10.class
})
public class EndToEndSystemTestSuite {
}
