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
 * @author Michael Prankl
 */
@Extension
public class CodehealthActionFactory extends TransientProjectActionFactory {

    @Inject
    private JPAIssueRepository jpaIssueRepository;

    public CodehealthActionFactory() {
        super();
        Jenkins.getInstance().getInjector().injectMembers(this);
    }

    @Override
    public Collection<? extends Action> createFor(AbstractProject abstractProject) {
        final List<Action> actions = new ArrayList<Action>();
        actions.add(new CodehealthAction((hudson.model.TopLevelItem) abstractProject, jpaIssueRepository));
        return actions;
    }
}
