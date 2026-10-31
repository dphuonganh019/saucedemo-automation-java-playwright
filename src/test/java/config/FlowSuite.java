package config;

import org.junit.platform.suite.api.ConfigurationParameter;
import java.lang.annotation.*;

// Suite chạy tuần tự: các class phụ thuộc nhau (class sau dùng session class trước để lại)
// -> tắt parallel và chạy class theo @Order đặt trên từng class
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ConfigurationParameter(key = "junit.jupiter.execution.parallel.enabled", value = "false")
@ConfigurationParameter(key = "junit.jupiter.testclass.order.default", value = "org.junit.jupiter.api.ClassOrderer$OrderAnnotation")
public @interface FlowSuite {
}
