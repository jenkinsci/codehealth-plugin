package org.jenkinsci.plugins.codehealth.service;

import hudson.ExtensionPoint;
import hudson.model.TopLevelItem;
import org.jenkinsci.plugins.codehealth.model.Issue;

import java.util.Collection;

/**
 * @author Michael Prankl
 */
public abstract class IssueRepository {

    /**
     * Saves new Issues for this Top-Level-Item (Job) to the relational database.
     * @param issues the collection of new issues
     * @param topLevelItem the top-level-item to which these issues belong
     */
    public abstract void newIssues(Collection<Issue> issues, TopLevelItem topLevelItem);
}
