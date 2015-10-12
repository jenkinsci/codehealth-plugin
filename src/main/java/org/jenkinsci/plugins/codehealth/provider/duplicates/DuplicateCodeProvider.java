package org.jenkinsci.plugins.codehealth.provider.duplicates;

import hudson.ExtensionPoint;
import hudson.model.AbstractBuild;
import hudson.model.AbstractDescribableImpl;

/**
 * @author Michael Prankl
 */
public abstract class DuplicateCodeProvider extends AbstractDescribableImpl<DuplicateCodeProvider> implements ExtensionPoint {

    /**
     * @param build the corresponding build
     * @return duplicate code
     */
    public abstract DuplicateCode getDuplicateCode(AbstractBuild<?, ?> build);

    /**
     * @return unique identifier of the providing plugin
     */
    public abstract String getOrigin();

    @Override
    public DuplicateCodeDescriptor getDescriptor() {
        return (DuplicateCodeDescriptor) super.getDescriptor();
    }
}
