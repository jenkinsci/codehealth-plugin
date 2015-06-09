package org.jenkinsci.plugins.codehealth.action;

import com.google.inject.Inject;
import hudson.Extension;
import hudson.model.Action;
import hudson.model.Run;
import hudson.model.TopLevelItem;
import jenkins.model.Jenkins;
import jenkins.model.TransientActionFactory;
import org.jenkinsci.plugins.codehealth.service.JPAIssueRepository;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Factory for creating the build-level action.
 *
 * @author Michael Prankl
 */
@Extension
public class CodehealthBuildActionFactory extends TransientActionFactory {

    @Inject
    private JPAIssueRepository jpaIssueRepository;

    public CodehealthBuildActionFactory() {
        super();
        Jenkins.getInstance().getInjector().injectMembers(this);
    }

    @Override
    public Class type() {
        return Run.class;
    }

    @Nonnull
    @Override
    public Collection<? extends Action> createFor(@Nonnull Object target) {
        final List<Action> actions = new ArrayList<Action>();
        Run r = (Run) target;
        TopLevelItem topLevelItem = (TopLevelItem) r.getParent();
        actions.add(new CodehealthBuildAction(r.getNumber(), topLevelItem, jpaIssueRepository));
        return actions;
    }
}
