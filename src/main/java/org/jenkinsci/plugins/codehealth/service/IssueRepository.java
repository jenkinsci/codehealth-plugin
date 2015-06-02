package org.jenkinsci.plugins.codehealth.service;

import hudson.model.AbstractBuild;
import hudson.model.Build;
import hudson.model.TopLevelItem;
import org.jenkinsci.plugins.codehealth.util.AbstractIssueMapper;

import java.util.Collection;

/**
 * @author Michael Prankl
 */
public abstract class IssueRepository<E> {

    /**
     * Updates new or open issues.
     *
     * @param data        the collection of found issues
     * @param build       the build which produced the issues
     * @param issueMapper the issue mapper
     */
    public abstract void updateIssues(Collection<E> data, AbstractBuild<?, ?> build, AbstractIssueMapper<E> issueMapper);

    /**
     * Mark some issues as fixed.
     * @param data the issues that have been fixed
     * @param build the corresponding build nr
     * @param issueMapper the issue mapper
     */
    public abstract void fixedIssues(Collection<E> data, AbstractBuild<?, ?> build, AbstractIssueMapper<E> issueMapper);
}
