package org.sonar.samples.java.checks;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.java.model.expression.AssignmentExpressionTreeImpl;
import org.sonar.java.model.expression.BinaryExpressionTreeImpl;
import org.sonar.java.model.expression.LiteralTreeImpl;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.ExpressionTree;
import org.sonar.plugins.java.api.tree.Tree;

import java.util.List;
import java.util.Set;

@Rule(
        key = "SQLInjectionCheck",
        priority = Priority.MAJOR,
        description = "SQL Injection flaws are introduced when software developers create dynamic database queries that include user supplied input. To avoid SQL injection flaws is simple. Developers need to either: a) stop writing dynamic queries; and/or b) prevent user supplied input which contains malicious SQL from affecting the logic of the executed query.",
        name = "Using concatenation with data manipulation statements can cause SQL Injection.",
        tags = {"security"}
)
public class SQLInjectionCheck extends IssuableSubscriptionVisitor {

    private static final Set<String> DATA_MANIPULATION_STATEMENTS = ImmutableSet.of("DELETE", "SELECT", "INSERT", "UPDATE", "REPLACE", "select", "insert", "update", "replace");

    @Override
    public List<Tree.Kind> nodesToVisit() {
        // specifying nodes to visit with enum Kind
        return ImmutableList.of(Tree.Kind.ASSIGNMENT, Tree.Kind.PLUS_ASSIGNMENT, Tree.Kind.PLUS);
    }

    @Override
    public void visitNode(Tree tree) {
        if (tree.is(Tree.Kind.ASSIGNMENT)) {
            AssignmentExpressionTreeImpl aet = (AssignmentExpressionTreeImpl) tree;
            try {
                BinaryExpressionTreeImpl bet = (BinaryExpressionTreeImpl) aet.expression();
                String operator = bet.operatorToken().text();

                ExpressionTree leftOperand = bet.leftOperand();
                LiteralTreeImpl lt = (LiteralTreeImpl) leftOperand;

                for (String statement : DATA_MANIPULATION_STATEMENTS) {
                    if (operator.equals("+") && lt.value().contains(statement)) {
                        reportIssue(tree, "Remove the usage of " + statement + " with concatenation and use parameterized query.");
                    }
                }
            } catch (ClassCastException e) {

            }
        }
        if (tree.is(Tree.Kind.PLUS_ASSIGNMENT)) {
            AssignmentExpressionTreeImpl et = (AssignmentExpressionTreeImpl) tree;
            try {
                BinaryExpressionTreeImpl bet = (BinaryExpressionTreeImpl) et.expression();
                String left = bet.leftOperand().firstToken().text();
                String right = bet.rightOperand().firstToken().text();

                for (String statement : DATA_MANIPULATION_STATEMENTS) {
                    if (left.contains(statement) || right.contains(statement)) {
                        reportIssue(tree, "Remove the usage of " + statement + " with concatenation and use parameterized query.");
                    }
                }
            } catch (ClassCastException e) {

            }
        }
        if (tree.is(Tree.Kind.PLUS)) {
            try {
                BinaryExpressionTreeImpl bet = (BinaryExpressionTreeImpl) tree;
                String left = bet.leftOperand().firstToken().text();
                String right = bet.rightOperand().firstToken().text();

                for (String statement : DATA_MANIPULATION_STATEMENTS) {
                    if (left.contains(statement) || right.contains(statement)) {
                        reportIssue(tree, "Remove the usage of " + statement + " with concatenation and use parameterized query.");
                    }
                }
            } catch (ClassCastException e) {

            }
        }
    }
}

