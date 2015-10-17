package org.jenkinsci.plugins.codehealth.action.dashboard;

import com.google.common.collect.Lists;
import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.TransientProjectActionFactory;
import org.jenkinsci.plugins.codehealth.CodehealthPublisher;

import java.util.Collection;
import java.util.List;

/**
 * @author Michael Prankl
 */
@Extension
public class DashboardActionFactory extends TransientProjectActionFactory {

    @Override
    public Collection<? extends Action> createFor(AbstractProject target) {
        final List<Action> actions = Lists.newArrayList();
        if (target.getPublishersList().contains(CodehealthPublisher.DESCRIPTOR)) {
            actions.add(new DashboardAction());
        }
        return actions;
    }


}
