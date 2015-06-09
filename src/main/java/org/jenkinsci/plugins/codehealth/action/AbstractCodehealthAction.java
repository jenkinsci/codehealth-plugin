package org.jenkinsci.plugins.codehealth.action;

import hudson.model.Action;
import hudson.model.Api;
import hudson.model.TopLevelItem;
import org.jenkinsci.plugins.codehealth.service.IssueRepository;

/**
 * @author Michael Prankl
 */
public abstract class AbstractCodehealthAction implements Action {

    private TopLevelItem topLevelItem;
    private transient IssueRepository issueRepository;

    public AbstractCodehealthAction(TopLevelItem topLevelItem, IssueRepository issueRepository) {
        this.topLevelItem = topLevelItem;
        this.issueRepository = issueRepository;
    }

    @Override
    public String getUrlName() {
        return "codehealth-api";
    }

    public Api getApi() {
        return new Api(this);
    }

    public TopLevelItem getTopLevelItem() {
        return topLevelItem;
    }

    public void setTopLevelItem(TopLevelItem topLevelItem) {
        this.topLevelItem = topLevelItem;
    }

    public IssueRepository getIssueRepository() {
        return issueRepository;
    }

    public void setIssueRepository(IssueRepository issueRepository) {
        this.issueRepository = issueRepository;
    }
}
