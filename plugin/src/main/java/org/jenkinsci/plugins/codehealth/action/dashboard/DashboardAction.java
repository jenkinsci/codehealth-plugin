package org.jenkinsci.plugins.codehealth.action.dashboard;

import hudson.model.AbstractBuild;
import hudson.model.Action;
import org.jenkinsci.plugins.codehealth.action.CodehealthActiveChecker;

/**
 * @author Michael Prankl
 */
public class DashboardAction implements Action {

    private AbstractBuild<?, ?> build;

    public DashboardAction(AbstractBuild<?, ?> build) {
        this.build = build;
    }

    @Override
    public String getIconFileName() {
        return "graph.gif";
    }

    @Override
    public String getDisplayName() {
        return "Codehealth Dashboard";
    }

    @Override
    public String getUrlName() {
        return "codehealth";
    }

    public AbstractBuild<?, ?> getBuild() {
        return build;
    }

    public void setBuild(AbstractBuild<?, ?> build) {
        this.build = build;
    }
}
