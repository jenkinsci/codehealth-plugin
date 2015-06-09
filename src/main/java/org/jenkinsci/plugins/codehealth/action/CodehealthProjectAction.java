package org.jenkinsci.plugins.codehealth.action;

import hudson.model.Action;
import hudson.model.Api;
import hudson.model.TopLevelItem;
import org.jenkinsci.plugins.codehealth.model.Issue;
import org.jenkinsci.plugins.codehealth.service.IssueRepository;
import org.jenkinsci.plugins.codehealth.service.JPAIssueRepository;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import java.util.Collection;

/**
 * @author Michael Prankl
 */
@ExportedBean
public class CodehealthProjectAction extends AbstractCodehealthAction {

    public CodehealthProjectAction(TopLevelItem topLevelItem, IssueRepository issueRepository) {
        super(topLevelItem, issueRepository);
    }

    @Override
    public String getIconFileName() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return null;
    }

    @Exported
    public Collection<Issue> getIssues() {
        return getIssueRepository().loadIssues(getTopLevelItem());
    }


}
