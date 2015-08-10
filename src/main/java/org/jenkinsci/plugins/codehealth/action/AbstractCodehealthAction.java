package org.jenkinsci.plugins.codehealth.action;

import hudson.model.Action;
import hudson.model.Api;
import hudson.model.TopLevelItem;
import org.jenkinsci.plugins.codehealth.model.State;
import org.jenkinsci.plugins.codehealth.service.IssueRepository;
import org.jenkinsci.plugins.codehealth.service.LinesOfCodeRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract {@link hudson.model.Action} to access Codehealth issues via the remote Jenkins API.
 * getIconFileName and getDisplayName always return null to prohibit the action to show up as Action item.
 *
 * @author Michael Prankl
 */
public abstract class AbstractCodehealthAction implements Action {

    private TopLevelItem topLevelItem;
    private transient IssueRepository issueRepository;
    private transient LinesOfCodeRepository locRepository;

    public AbstractCodehealthAction(TopLevelItem topLevelItem, IssueRepository issueRepository, LinesOfCodeRepository locRepository) {
        this.topLevelItem = topLevelItem;
        this.issueRepository = issueRepository;
        this.locRepository = locRepository;
    }

    @Override
    public String getIconFileName() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return null;
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

    protected List<State> list(State... states) {
        List<State> list = new ArrayList<State>(states.length);
        for (State state : states) {
            list.add(state);
        }
        return list;
    }

    public LinesOfCodeRepository getLocRepository() {
        return locRepository;
    }

    public void setLocRepository(LinesOfCodeRepository locRepository) {
        this.locRepository = locRepository;
    }
}
