package org.jenkinsci.plugins.codehealth.action.issues;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Action;
import org.jenkinsci.plugins.codehealth.action.AbstractProjectActionFactory;
import org.jenkinsci.plugins.codehealth.service.JPAIssueRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Factory for creating the job-level action for issues.
 *
 * @author Michael Prankl
 */
@Extension
public class IssuesProjectActionFactory extends AbstractProjectActionFactory<JPAIssueRepository> {

    @Override
    public Collection<? extends Action> createFor(AbstractProject abstractProject) {
        final List<Action> actions = new ArrayList<Action>();
        actions.add(new IssuesProjectAction(abstractProject, getRepositoryImplementation()));
        return actions;
    }
}
