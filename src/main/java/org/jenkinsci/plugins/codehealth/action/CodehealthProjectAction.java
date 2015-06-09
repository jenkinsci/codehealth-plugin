package org.jenkinsci.plugins.codehealth.action;

import hudson.model.Action;
import hudson.model.Api;
import hudson.model.TopLevelItem;
import org.jenkinsci.plugins.codehealth.model.Issue;
import org.jenkinsci.plugins.codehealth.model.State;
import org.jenkinsci.plugins.codehealth.service.IssueRepository;
import org.jenkinsci.plugins.codehealth.service.JPAIssueRepository;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * {@link hudson.model.AbstractProject}-based Action to retrieve current issues for a project.
 *
 * @author Michael Prankl
 */
@ExportedBean
public class CodehealthProjectAction extends AbstractCodehealthAction {

    private transient final List<State> newAndOpen = list(State.NEW, State.OPEN);

    public CodehealthProjectAction(TopLevelItem topLevelItem, IssueRepository issueRepository) {
        super(topLevelItem, issueRepository);
    }

    @Exported
    public Collection<Issue> getIssues() {
        return getIssueRepository().loadIssues(getTopLevelItem(), newAndOpen);
    }


}
