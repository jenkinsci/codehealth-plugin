package org.jenkinsci.plugins.codehealth.action.dashboard;

import hudson.model.Action;
import org.jenkinsci.plugins.codehealth.action.CodehealthActiveChecker;

/**
 * @author Michael Prankl
 */
public class DashboardAction implements Action {
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
}
