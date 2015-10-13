package org.jenkinsci.plugins.codehealth.action.duplicates;

import hudson.Extension;
import hudson.model.*;
import hudson.tasks.Publisher;
import org.jenkinsci.plugins.codehealth.CodehealthPublisher;
import org.jenkinsci.plugins.codehealth.action.AbstractBuildActionFactory;
import org.jenkinsci.plugins.codehealth.provider.duplicates.DuplicateCodeProvider;
import org.jenkinsci.plugins.codehealth.service.JPADuplicateCodeRepository;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Michael Prankl
 */
@Extension
public class DuplicateCodeBuildActionFactory extends AbstractBuildActionFactory<JPADuplicateCodeRepository> {

    @Nonnull
    @Override
    public Collection<? extends Action> createFor(@Nonnull Object target) {
        final List<Action> actions = new ArrayList<Action>();
        Run r = (Run) target;
        TopLevelItem topLevelItem = (TopLevelItem) r.getParent();
        Project p = (Project) r.getParent();
        if (isCodehealthActive(p)) {
            CodehealthPublisher publisher = (CodehealthPublisher) p.getPublisher(CodehealthPublisher.DESCRIPTOR);
            DuplicateCodeProvider duplicateCodeProvider = publisher.getDuplicateCodeProvider();
            actions.add(new DuplicateCodeBuildAction(r.getNumber(), topLevelItem, getRepositoryImplementation(), duplicateCodeProvider));
        }
        return actions;
    }
}
