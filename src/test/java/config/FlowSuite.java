package config;

import org.junit.platform.suite.api.ConfigurationParameter;
import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ConfigurationParameter(key = "junit.jupiter.execution.parallel.enabled", value = "false")
public @interface FlowSuite {
}
