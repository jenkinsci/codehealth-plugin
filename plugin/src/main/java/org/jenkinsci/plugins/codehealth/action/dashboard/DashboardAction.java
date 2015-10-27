package org.jenkinsci.plugins.codehealth.action.dashboard;

import hudson.model.AbstractProject;
import hudson.model.Action;

/**
 * @author Michael Prankl
 */
public class DashboardAction implements Action {

    private AbstractProject<?, ?> project;

    public DashboardAction(AbstractProject<?, ?> project) {
        this.project = project;
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

    public AbstractProject<?, ?> getProject() {
        return project;
    }

    public void setProject(AbstractProject<?, ?> project) {
        this.project = project;
    }
}
