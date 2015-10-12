package org.jenkinsci.plugins.codehealth.provider.issues;

import org.jenkinsci.plugins.codehealth.model.IssueEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * Prototype factory for issues.
 *
 * @author Michael Prankl
 */
public class IssueFactory {

    private static Map<String, IssueEntity> prototypes = new HashMap<String, IssueEntity>();

    public static IssueEntity getIssue(final String origin) throws CloneNotSupportedException {
        if (prototypes.containsKey(origin)) {
            return (IssueEntity) prototypes.get(origin).clone();
        } else {
            IssueEntity issue = new IssueEntity();
            issue.setOrigin(origin);
            prototypes.put(origin, issue);
            return issue;
        }
    }
}
