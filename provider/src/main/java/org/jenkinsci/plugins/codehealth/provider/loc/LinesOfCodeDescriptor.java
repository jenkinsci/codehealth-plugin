package org.jenkinsci.plugins.codehealth.provider.loc;

import hudson.DescriptorExtensionList;
import hudson.model.Descriptor;
import jenkins.model.Jenkins;

/**
 * @author Michael Prankl
 */
public abstract class LinesOfCodeDescriptor extends Descriptor<LinesOfCodeProvider> {

    protected LinesOfCodeDescriptor() {
    }

    protected LinesOfCodeDescriptor(Class<? extends LinesOfCodeProvider> clazz) {
        super(clazz);
    }

}
