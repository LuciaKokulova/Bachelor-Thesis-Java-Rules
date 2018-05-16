package org.sonar.samples.java.checks;

import org.junit.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

public class SQLInjectionCheckTest {

    @Test
    public void test() {
        JavaCheckVerifier.verify("src/test/files/SQLInjectionCheck.java", new SQLInjectionCheck());
    }

}
