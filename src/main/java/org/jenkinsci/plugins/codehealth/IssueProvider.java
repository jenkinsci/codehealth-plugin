package org.jenkinsci.plugins.codehealth;

import hudson.ExtensionList;
import hudson.ExtensionPoint;
import hudson.model.AbstractBuild;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.codehealth.model.Issue;

import java.util.Collection;

/**
 * @author Michael Prankl
 */
public abstract class IssueProvider implements ExtensionPoint {

    /**
     * @return existing issues for a build
     */
    public abstract Collection<Issue> getExistingIssues(AbstractBuild<?, ?> build);

    /**
     * @return issues that have been fixed with a build
     */
    public abstract Collection<Issue> getFixedIssues(AbstractBuild<?, ?> build);

    public static ExtensionList<IssueProvider> all() {
        return Jenkins.getInstance().getExtensionList(IssueProvider.class);
    }
}
