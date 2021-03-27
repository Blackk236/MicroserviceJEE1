package sn.isi.m2gl;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;
import sn.isi.m2gl.JeeProjectApi1App;
import sn.isi.m2gl.config.TestSecurityConfiguration;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = { JeeProjectApi1App.class, TestSecurityConfiguration.class })
public @interface IntegrationTest {
}
