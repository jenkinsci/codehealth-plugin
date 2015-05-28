package org.jenkinsci.plugins.codehealth.util;

import org.jenkinsci.plugins.codehealth.model.Issue;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Prankl
 */
public abstract class AbstractIssueMapper {

    /**
     *
     * @param e your domain representation of an issue (annotation/warning)
     * @return the corresponding Issue
     */
    public abstract Issue map(Object e);

    public List<Issue> transform(List<Object> annotations){
        List<Issue> issues = new ArrayList<Issue>(annotations.size());
        for (Object a : annotations){
            Issue mappedIssue = this.map(a);
            checkIssue(mappedIssue);
        }
        return issues;
    }

    private void checkIssue(Issue issue){
    // TODO Check the issue
    }
}
