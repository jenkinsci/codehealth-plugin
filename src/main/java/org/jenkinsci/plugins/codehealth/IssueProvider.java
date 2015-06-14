package org.jenkinsci.plugins.codehealth;

import hudson.ExtensionList;
import hudson.ExtensionPoint;
import hudson.model.AbstractBuild;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.codehealth.model.IssueEntity;

import java.util.Collection;

/**
 * Extension point for code analysis plugins which want to contribute issues for continuous code quality monitoring.
 *
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

    /**
     * @return unique identifier of the plugin which contributes issues
     */
    public abstract String getOrigin();

    /**
     * @return if the plugin can provide information about fixed issues with {@link org.jenkinsci.plugins.codehealth.IssueProvider#getFixedIssues(hudson.model.AbstractBuild)}
     */
    public abstract boolean canProvideFixedIssues();

    static ExtensionList<IssueProvider> all() {
        return Jenkins.getInstance().getExtensionList(IssueProvider.class);
    }
}
