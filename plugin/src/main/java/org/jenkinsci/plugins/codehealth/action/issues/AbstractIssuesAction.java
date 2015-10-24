package org.jenkinsci.plugins.codehealth.action.issues;

import hudson.model.TopLevelItem;
import org.jenkinsci.plugins.codehealth.action.AbstractCodehealthAction;
import org.jenkinsci.plugins.codehealth.model.State;
import org.jenkinsci.plugins.codehealth.service.IssueRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract {@link hudson.model.Action} to access Codehealth issues via the remote Jenkins API.
 * getIconFileName and getDisplayName always return null to prohibit the action to show up as Action item.
 *
 * @author Michael Prankl
 */
public abstract class AbstractIssuesAction extends AbstractCodehealthAction {

    private transient IssueRepository issueRepository;

    public AbstractIssuesAction(TopLevelItem topLevelItem, IssueRepository issueRepository) {
        super(topLevelItem);
        this.issueRepository = issueRepository;
    }

    @Override
    public String getUrlName() {
        return "issues-api";
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
}
