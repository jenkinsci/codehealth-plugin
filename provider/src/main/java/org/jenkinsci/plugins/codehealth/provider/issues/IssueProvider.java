package org.jenkinsci.plugins.codehealth.provider.issues;

import hudson.ExtensionList;
import hudson.ExtensionPoint;
import hudson.model.AbstractBuild;
import jenkins.model.Jenkins;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
    @Nonnull
    public abstract Collection<Issue> getIssues(AbstractBuild<?, ?> build);

    /**
     * @return unique identifier of the plugin which contributes issues
     */
    @Nonnull
    public abstract String getOrigin();

    /**
     * @return pretty name of the plugin which contributes issues (will be displayed on UI)
     */
    @Nonnull
    public abstract String getOriginPluginName();

    /**
     * @return the project result action url
     */
    @Nullable
    public abstract String getProjectResultUrlName();

    /**
     * @return the build result action url
     */
    @Nullable
    public abstract String getBuildResultUrlName();

    public static ExtensionList<IssueProvider> all() {
        return Jenkins.getInstance().getExtensionList(IssueProvider.class);
    }

    /**
     * @param origin
     * @return the IssueProvider for this origin or null
     */
    public static IssueProvider findProvider(String origin) {
        for (IssueProvider issueProvider : all()) {
            if (issueProvider.getOrigin().equals(origin)) {
                return issueProvider;
            }
        }
        return null;
    }

}
