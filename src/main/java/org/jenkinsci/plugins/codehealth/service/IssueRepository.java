package org.jenkinsci.plugins.codehealth.service;

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
     * @param data         the collection of new issues
     * @param topLevelItem the top-level-item to which these issues belong
     * @param issueMapper the issue mapper
     */
    public abstract void newIssues(Collection<E> data, TopLevelItem topLevelItem, AbstractIssueMapper<E> issueMapper);
}
