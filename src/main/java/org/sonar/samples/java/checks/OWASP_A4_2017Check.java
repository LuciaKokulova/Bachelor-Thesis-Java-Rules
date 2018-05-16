package org.sonar.samples.java.checks;

import com.google.common.collect.ImmutableList;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.java.model.expression.MethodInvocationTreeImpl;
import org.sonar.plugins.java.api.IssuableSubscriptionVisitor;
import org.sonar.plugins.java.api.tree.Arguments;
import org.sonar.plugins.java.api.tree.ExpressionTree;
import org.sonar.plugins.java.api.tree.Tree;

import java.util.List;

@Rule(key = "OWASP_A4_2017Check",
        name = "XML processors should not evaluate external entity references within XML documents",
        description = "Many older or poorly configured XML processors evaluate external entity references within XML\n" +
                "documents. External entities can be used to disclose internal files using the file URI handler,\n" +
                "internal SMB file shares on unpatched Windows servers, internal port scanning, remote code\n" +
                "execution, and denial of service attacks, such as the Billion Laughs attack. ",
        priority = Priority.CRITICAL,
        tags = {"security"}
)
public class OWASP_A4_2017Check extends IssuableSubscriptionVisitor {

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
            ExpressionTree firstArg = arguments.get(0);
            ExpressionTree secondArg = arguments.get(1);

            String firstArgument = firstArg.firstToken().text().substring(1, firstArg.firstToken().text().length() - 1);
            String secondArgument = secondArg.firstToken().text();

            // Xerces 1 features
            if (firstArgument.equals("http://apache.org/xml/features/nonvalidating/load-external-dtd") && secondArgument.equals("true")) {
                reportIssue(tree, "This feature should be set to false, to avoid XXE attack.");
            }
            if (firstArgument.equals("http://xml.org/sax/features/external-general-entities") && secondArgument.equals("true")) {
                reportIssue(tree, "This feature should be set to false, to avoid XXE attack.");
            }
            if (firstArgument.equals("http://xml.org/sax/features/external-parameter-entities") && secondArgument.equals("true")) {
                reportIssue(tree, "This feature should be set to false, to avoid XXE attack.");
            }

            // Xerces 2 additional features
            if (firstArgument.equals("http://apache.org/xml/features/disallow-doctype-decl") && secondArgument.equals("false")) {
                reportIssue(tree, "This feature should be set to true, to avoid XXE attack.");
            }
        }
    }

}
