package org.sonar.samples.java.checks;

import com.google.common.collect.ImmutableList;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.java.model.expression.AssignmentExpressionTreeImpl;
import org.sonar.java.model.expression.BinaryExpressionTreeImpl;
import org.sonar.java.model.expression.MemberSelectExpressionTreeImpl;
import org.sonar.java.model.expression.MethodInvocationTreeImpl;
import org.sonar.java.model.statement.IfStatementTreeImpl;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.*;
import sun.reflect.generics.tree.TypeArgument;

import java.beans.Expression;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Rule(key = "OWASP_A9_2017Check",
        name = "Components with known vulnerabilities should not be used",
        description = "Applications and APIs using components with known vulnerabilities may undermine application defenses and enable various attacks and impacts.",
        priority = Priority.CRITICAL,
        tags = {"security"}
)
public class OWASP_A9_2017Check extends IssuableSubscriptionVisitor {

    List<String> hashes = Arrays.asList("SHA-1", "MD2", "MD5");

    @Override
    public List<Tree.Kind> nodesToVisit() {
        // specifying nodes to visit with enum Kind
        return ImmutableList.of(Tree.Kind.METHOD_INVOCATION);
    }

    @Override
    public void visitNode(Tree tree) {
        MethodInvocationTreeImpl mit = (MethodInvocationTreeImpl) tree;

        Arguments arguments = mit.arguments();
        if (arguments.size() != 0) {
            String argument = arguments.get(0).lastToken().text();
            argument = argument.substring(1, argument.length() - 1);
            for (String hash : hashes) {
                if (argument.equals(hash)) {
                    reportIssue(tree, hash + " is cryptographic hash function with known vulnerabilities! Use another one.");
                }
            }
        }
    }
}