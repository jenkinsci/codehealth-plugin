package org.jenkinsci.plugins.codehealth.action;

import hudson.model.AbstractProject;
import hudson.util.DescribableList;

/**
 * @author Michael Prankl
 */
public interface CodehealthActiveChecker {

    public boolean isCodehealthActive(AbstractProject project);
}
