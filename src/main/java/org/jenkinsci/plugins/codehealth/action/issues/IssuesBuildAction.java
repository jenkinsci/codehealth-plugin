package org.jenkinsci.plugins.codehealth.action.issues;

import hudson.model.TopLevelItem;
import org.jenkinsci.plugins.codehealth.action.issues.AbstractIssuesAction;
import org.jenkinsci.plugins.codehealth.model.DuplicateCodeEntity;
import org.jenkinsci.plugins.codehealth.model.IssueEntity;
import org.jenkinsci.plugins.codehealth.model.LinesOfCodeEntity;
import org.jenkinsci.plugins.codehealth.model.State;
import org.jenkinsci.plugins.codehealth.service.DuplicateCodeRepository;
import org.jenkinsci.plugins.codehealth.service.IssueRepository;
import org.jenkinsci.plugins.codehealth.service.LinesOfCodeRepository;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import java.util.Collection;

/**
 * {@link hudson.model.Run}-based Action for retrieving newly introduced and fixed issues for a build.
 *
 * @author Michael Prankl
 */
@ExportedBean
public class IssuesBuildAction extends AbstractIssuesAction {

    private int buildNr;

    public IssuesBuildAction(int buildNr, TopLevelItem topLevelItem, IssueRepository issueRepository) {
        super(topLevelItem, issueRepository);
        this.buildNr = buildNr;
    }

    @Exported
    public Collection<IssueEntity> newIssues() {
        return getIssueRepository().loadIssues(getTopLevelItem(), this.buildNr, State.NEW);
    }

    @Exported
    public Collection<IssueEntity> fixedIssues() {
        return getIssueRepository().loadIssues(getTopLevelItem(), this.buildNr, State.CLOSED);
    }

}
