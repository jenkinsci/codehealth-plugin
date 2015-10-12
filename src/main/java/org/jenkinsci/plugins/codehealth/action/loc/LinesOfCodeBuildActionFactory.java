package org.jenkinsci.plugins.codehealth.action.loc;

import hudson.Extension;
import hudson.model.Action;
import hudson.model.Run;
import hudson.model.TopLevelItem;
import org.jenkinsci.plugins.codehealth.action.AbstractBuildActionFactory;
import org.jenkinsci.plugins.codehealth.service.JPALinesOfCodeRepository;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Michael Prankl
 */
@Extension
public class LinesOfCodeBuildActionFactory extends AbstractBuildActionFactory<JPALinesOfCodeRepository> {

    @Nonnull
    @Override
    public Collection<? extends Action> createFor(@Nonnull Object target) {
        final List<Action> actions = new ArrayList<Action>();
        Run r = (Run) target;
        TopLevelItem topLevelItem = (TopLevelItem) r.getParent();
        actions.add(new LinesOfCodeBuildAction(r.getNumber(), topLevelItem, getRepositoryImplementation()));
        return actions;
    }
}
