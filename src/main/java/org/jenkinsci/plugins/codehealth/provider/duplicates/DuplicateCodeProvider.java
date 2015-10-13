package org.jenkinsci.plugins.codehealth.provider.duplicates;

import hudson.ExtensionPoint;
import hudson.model.AbstractBuild;
import hudson.model.AbstractDescribableImpl;
import org.jenkinsci.plugins.codehealth.provider.ProvidesResultUrls;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Michael Prankl
 */
public abstract class DuplicateCodeProvider extends AbstractDescribableImpl<DuplicateCodeProvider>
        implements ExtensionPoint, ProvidesResultUrls {

    /**
     * @param build the corresponding build
     * @return duplicate code
     */
    @Nullable
    public abstract DuplicateCode getDuplicateCode(AbstractBuild<?, ?> build);

    /**
     * @return unique identifier of the providing plugin
     */
    @Nonnull
    public abstract String getOrigin();

    @Override
    public DuplicateCodeDescriptor getDescriptor() {
        return (DuplicateCodeDescriptor) super.getDescriptor();
    }

}
