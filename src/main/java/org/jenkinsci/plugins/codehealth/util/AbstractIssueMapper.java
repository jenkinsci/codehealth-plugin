package org.jenkinsci.plugins.codehealth.util;

import org.jenkinsci.plugins.codehealth.model.Issue;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class to support the mapping of annotations/warnings to codehealth Issues.
 *
 * @author Michael Prankl
 */
public abstract class AbstractIssueMapper<E extends Object> {

    /**
     * @param e your domain representation of an issue (annotation/warning)
     * @return the corresponding Issue
     */
    public abstract Issue map(E e);

    public List<Issue> transform(List<E> annotations) {
        List<Issue> issues = new ArrayList<Issue>(annotations.size());
        for (E a : annotations) {
            Issue mappedIssue = this.map(a);
            checkIssue(mappedIssue);
        }
        return issues;
    }

    /**
     * Checks to see if needed fields are filled.
     *
     * @param issue the mapped Issue to check
     */
    private void checkIssue(Issue issue) {
        if (issue == null) {
            throw new IllegalArgumentException("issue is null");
        }
        if (issue.getOrigin() == null) {
            throw new IllegalArgumentException("origin not set");
        }
        if (issue.getContextHashCode() == 0L) {
            throw new IllegalArgumentException("contextHashCode not set");
        }
        if (issue.getMessage() == null) {
            throw new IllegalArgumentException("message not set");
        }
        if (issue.getPriority() == null) {
            throw new IllegalArgumentException("priority not set");
        }
    }
}
