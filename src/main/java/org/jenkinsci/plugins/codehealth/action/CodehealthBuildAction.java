package org.jenkinsci.plugins.codehealth.action;

import hudson.model.TopLevelItem;
import org.jenkinsci.plugins.codehealth.model.Issue;
import org.jenkinsci.plugins.codehealth.model.State;
import org.jenkinsci.plugins.codehealth.service.IssueRepository;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import java.util.Collection;

/**
 * {@link hudson.model.Run}-based Action for retrieving newly introduced and fixed issues for a build.
 *
 * @author Michael Prankl
 */
@ExportedBean
public class CodehealthBuildAction extends AbstractCodehealthAction {

    private int buildNr;

    public CodehealthBuildAction(int buildNr, TopLevelItem topLevelItem, IssueRepository issueRepository) {
        super(topLevelItem, issueRepository);
        this.buildNr = buildNr;
    }

    @Exported
    public Collection<Issue> newIssues() {
        return getIssueRepository().loadIssues(getTopLevelItem(), this.buildNr, State.NEW);
    }

    @Exported
    public Collection<Issue> fixedIssues() {
        return getIssueRepository().loadIssues(getTopLevelItem(), this.buildNr, State.CLOSED);
    }
}
