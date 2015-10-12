package org.jenkinsci.plugins.codehealth.action.duplicates;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Action;
import org.jenkinsci.plugins.codehealth.action.AbstractProjectActionFactory;
import org.jenkinsci.plugins.codehealth.service.JPADuplicateCodeRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Michael Prankl
 */
@Extension
public class DuplicateCodeProjectActionFactory extends AbstractProjectActionFactory<JPADuplicateCodeRepository> {

    @Override
    public Collection<? extends Action> createFor(AbstractProject abstractProject) {
        final List<Action> actions = new ArrayList<Action>();
        actions.add(new DuplicateCodeProjectAction(abstractProject, getRepositoryImplementation()));
        return actions;
    }
}
