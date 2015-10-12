package org.jenkinsci.plugins.codehealth.action.loc;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Action;
import org.jenkinsci.plugins.codehealth.action.AbstractProjectActionFactory;
import org.jenkinsci.plugins.codehealth.service.JPALinesOfCodeRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Michael Prankl
 */
@Extension
public class LinesOfCodeProjectActionFactory extends AbstractProjectActionFactory<JPALinesOfCodeRepository> {

    @Override
    public Collection<? extends Action> createFor(AbstractProject abstractProject) {
        final List<Action> actions = new ArrayList<Action>();
        actions.add(new LinesOfCodeProjectAction(abstractProject, getRepositoryImplementation()));
        return actions;
    }
}
