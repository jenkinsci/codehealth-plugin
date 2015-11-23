package org.jenkinsci.plugins.codehealth.action.issues;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.Job;
import org.jenkinsci.plugins.codehealth.action.AbstractProjectActionFactory;
import org.jenkinsci.plugins.codehealth.service.JPAIssueRepository;

import javax.annotation.Nonnull;
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
    public Class<Job> type() {
        return Job.class;
    }

    @Nonnull
    @Override
    public Collection<? extends Action> createFor(Job target) {
        final List<Action> actions = new ArrayList<Action>();
        if (target instanceof AbstractProject) {
            if (isCodehealthActive((AbstractProject) target)) {
                actions.add(new IssuesProjectAction(target, getRepositoryImplementation()));
            }
        }
        return actions;
    }
}
