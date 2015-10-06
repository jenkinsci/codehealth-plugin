package org.jenkinsci.plugins.codehealth;

import hudson.ExtensionList;
import hudson.ExtensionPoint;
import hudson.model.AbstractBuild;
import hudson.model.AbstractDescribableImpl;
import jenkins.model.Jenkins;

/**
 * Extension point for code analysis plugins which want to contribute a LOC metric.
 *
 * @author Michael Prankl
 */
public abstract class LinesOfCodeProvider extends AbstractDescribableImpl<LinesOfCodeProvider> implements ExtensionPoint {

    /**
     * @return the LOC of the build
     */
    public abstract LinesOfCode getLOC(AbstractBuild<?, ?> build);

    /**
     * @return unique identifier of the contributing plugin
     */
    public abstract String getOrigin();

    @Override
    public LinesOfCodeDescriptor getDescriptor() {
        return (LinesOfCodeDescriptor) super.getDescriptor();
    }

}
