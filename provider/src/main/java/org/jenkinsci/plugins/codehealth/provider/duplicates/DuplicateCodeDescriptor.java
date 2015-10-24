package org.jenkinsci.plugins.codehealth.provider.duplicates;

import hudson.DescriptorExtensionList;
import hudson.model.Descriptor;
import jenkins.model.Jenkins;

/**
 * @author Michael Prankl
 */
public abstract class DuplicateCodeDescriptor extends Descriptor<DuplicateCodeProvider> {

    protected DuplicateCodeDescriptor() {
    }

    protected DuplicateCodeDescriptor(Class<? extends DuplicateCodeProvider> clazz) {
        super(clazz);
    }


}
