package org.jenkinsci.plugins.codehealth.action.issues;

import hudson.Extension;
import hudson.model.Action;
import hudson.model.Run;
import hudson.model.TopLevelItem;
import org.jenkinsci.plugins.codehealth.action.AbstractBuildActionFactory;
import org.jenkinsci.plugins.codehealth.service.JPAIssueRepository;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Factory for creating the build-level action for issues.
 *
 * @author Michael Prankl
 */
@Extension
public class IssuesBuildActionFactory extends AbstractBuildActionFactory<JPAIssueRepository> {

    @Nonnull
    @Override
    public Collection<? extends Action> createFor(@Nonnull Object target) {
        final List<Action> actions = new ArrayList<Action>();
        Run r = (Run) target;
        TopLevelItem topLevelItem = (TopLevelItem) r.getParent();
        if (isCodehealthActive((hudson.model.AbstractProject) r.getParent())) {
            actions.add(new IssuesBuildAction(r.getNumber(), topLevelItem, getRepositoryImplementation()));
        }
        return actions;
    }
}
