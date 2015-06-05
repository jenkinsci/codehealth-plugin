package org.jenkinsci.plugins.codehealth.action;

import com.google.inject.Inject;
import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.TransientProjectActionFactory;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.codehealth.service.JPAIssueRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Factory for creating the job-level action.
 *
 * @author Michael Prankl
 */
@Extension
public class CodehealthJobActionFactory extends TransientProjectActionFactory {

    @Inject
    private JPAIssueRepository jpaIssueRepository;

    public CodehealthJobActionFactory() {
        super();
        Jenkins.getInstance().getInjector().injectMembers(this);
    }

    @Override
    public Collection<? extends Action> createFor(AbstractProject abstractProject) {
        final List<Action> actions = new ArrayList<Action>();
        actions.add(new CodehealthJobAction((hudson.model.TopLevelItem) abstractProject, jpaIssueRepository));
        return actions;
    }
}
