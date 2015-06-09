package org.jenkinsci.plugins.codehealth;

import hudson.ExtensionList;
import hudson.ExtensionPoint;
import hudson.model.AbstractBuild;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.codehealth.model.Issue;

import java.util.Collection;

/**
 * Extension point for code analysis plugins which want to contribute issues for continuous code quality monitoring.
 * An issue must have set following attributes:
 * <ul>
 *     <li>origin - can refer to plugin id</li>
 *     <li>contextHashCode - must be unique for the scope of the plugin (origin)</li>
 *     <li>priority</li>
 *     <li>message</li>
 * </ul>
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

    public static ExtensionList<IssueProvider> all() {
        return Jenkins.getInstance().getExtensionList(IssueProvider.class);
    }
}
