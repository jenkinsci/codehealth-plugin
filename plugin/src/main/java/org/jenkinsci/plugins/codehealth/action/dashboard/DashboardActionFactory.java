package org.jenkinsci.plugins.codehealth.action.dashboard;

import com.google.common.collect.Lists;
import hudson.Extension;
import hudson.model.AbstractBuild;
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
        AbstractBuild lastBuild = target.getLastBuild();
        if (target.getPublishersList().contains(CodehealthPublisher.DESCRIPTOR) && lastBuild != null) {
            actions.add(new DashboardAction(lastBuild));
        }
        return actions;
    }


}
