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
public class CodehealthAction implements Action {

    private TopLevelItem topLevelItem;
    private transient IssueRepository issueRepository;

    public CodehealthAction(TopLevelItem topLevelItem, IssueRepository issueRepository){
        this.topLevelItem = topLevelItem;
        this.issueRepository = issueRepository;
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
        return "codehealth";
    }

    public Api getApi(){
        return new Api(this);
    }

    @Exported
    public int getIssueCount(){
        return issueRepository.loadIssues(this.topLevelItem).size();
    }

    @Exported
    public Collection<Issue> getIssues(){
        return issueRepository.loadIssues(this.topLevelItem);
    }


}
