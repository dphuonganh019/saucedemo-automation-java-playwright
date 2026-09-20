package config;

import org.junit.platform.suite.api.ConfigurationParameter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ConfigurationParameter(key = "junit.jupiter.execution.parallel.enabled", value = "true")
@ConfigurationParameter(key = "junit.jupiter.execution.parallel.mode.classes.default", value = "concurrent")
@ConfigurationParameter(key = "junit.jupiter.execution.parallel.mode.default", value = "same_thread")
@ConfigurationParameter(key = "junit.jupiter.execution.parallel.config.strategy", value = "dynamic")
@ConfigurationParameter(key = "junit.jupiter.execution.parallel.config.dynamic.factor", value = "1")
public @interface ParallelSuite {
}
