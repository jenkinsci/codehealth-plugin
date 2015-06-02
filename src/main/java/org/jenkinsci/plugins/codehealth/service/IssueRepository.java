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
     * Saves new Issues for this Top-Level-Item (Job) to the relational database.
     *
     * @param data        the collection of new issues
     * @param build       the build which produced the issues
     * @param issueMapper the issue mapper
     */
    public abstract void updateIssues(Collection<E> data, AbstractBuild<?, ?> build, AbstractIssueMapper<E> issueMapper);
}
