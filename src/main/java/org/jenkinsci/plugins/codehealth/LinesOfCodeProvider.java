package org.jenkinsci.plugins.codehealth;

import hudson.ExtensionList;
import hudson.ExtensionPoint;
import hudson.model.AbstractBuild;
import jenkins.model.Jenkins;

/**
 * Extension point for code analysis plugins which want to contribute a LOC metric.
 *
 * @author Michael Prankl
 */
public abstract class LinesOfCodeProvider implements ExtensionPoint {

    /**
     * @return the LOC of the build
     */
    public abstract LinesOfCode getLOC(AbstractBuild<?, ?> build);

    static ExtensionList<LinesOfCodeProvider> all() {
        return Jenkins.getInstance().getExtensionList(LinesOfCodeProvider.class);
    }
}
