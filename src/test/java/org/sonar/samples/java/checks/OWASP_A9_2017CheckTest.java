package org.sonar.samples.java.checks;

import org.junit.Test;
import org.sonar.java.checks.verifier.JavaCheckVerifier;

public class OWASP_A9_2017CheckTest {

    @Test
    public void test() {
        // linking the test to rule
        // JavaCheckVerifier class provides useful methods to validate rule implementations,
        // allowing us to totally abstract all the mechanisms related to analyzer initialization
        // Note that while verifying a rule, the verifier will collect lines marked as being Noncompliant,
        // and verify that the rule raises the expected issues and only those issues.
        JavaCheckVerifier.verify("src/test/files/OWASP_A9_2017Check.java", new OWASP_A9_2017Check());
    }

}
