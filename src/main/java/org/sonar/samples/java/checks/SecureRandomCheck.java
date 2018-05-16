package org.sonar.samples.java.checks;

import com.google.common.collect.ImmutableList;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.java.matcher.MethodMatcher;
import org.sonar.java.model.ExpressionUtils;
import org.sonar.java.model.expression.MethodInvocationTreeImpl;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.*;

import java.util.List;

@Rule(key = SecureRandomCheck.KEY,
        name = "Functions generating random numbers that are not cryptographically safe should not be used",
        description = "This function does not generate cryptographically secure values, and should not be used for cryptographic purposes. If you need a cryptographically secure value, consider using random_int(), random_bytes(), or openssl_random_pseudo_bytes() instead.",
        priority = Priority.MAJOR,
        tags = {"security"}
)
public class SecureRandomCheck extends IssuableSubscriptionVisitor {

    public static final String KEY = "SecureRandomCheck";

    @Override
    public List<Tree.Kind> nodesToVisit() {
        // specifying nodes to visit with enum Kind
        return ImmutableList.of(Tree.Kind.NEW_CLASS, Tree.Kind.METHOD_INVOCATION);
    }

    @Override
    public void visitNode(Tree tree) {
        if (tree.is(Tree.Kind.METHOD_INVOCATION)) {
            MethodInvocationTree mit = (MethodInvocationTree) tree;
            ExpressionTree et = mit.methodSelect();

            if (et.firstToken().text().equals("Math") && et.lastToken().text().equals("random")) {
                reportIssue(mit, "Do not use this cryptographically weak function.");
            }

        } else {
            try {
                NewClassTree nct = (NewClassTree) tree;
                if (nct.symbolType().is("java.util.Random")) {
                    reportIssue(nct, "Do not use this cryptographically weak function.");
                }
            } catch (ClassCastException e) {

            }
        }
    }
}
