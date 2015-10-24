package org.jenkinsci.plugins.codehealth.action;

import hudson.model.AbstractProject;

/**
 * @author Michael Prankl
 */
public interface CodehealthActiveChecker {

    boolean isCodehealthActive(AbstractProject project);
}
