package org.jenkinsci.plugins.codehealth.provider.duplicates;

import hudson.DescriptorExtensionList;
import hudson.ExtensionList;
import hudson.ExtensionPoint;
import hudson.model.AbstractBuild;
import hudson.model.AbstractDescribableImpl;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.codehealth.provider.ProvidesResultUrls;
import org.jenkinsci.plugins.codehealth.provider.loc.LinesOfCodeDescriptor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

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

    /**
     * All registered {@link DuplicateCodeProvider}s.
     */
    public static ExtensionList<DuplicateCodeProvider> all(){
        return Jenkins.getInstance().getExtensionList(DuplicateCodeProvider.class);
    }

    /**
     * @return all registered Descriptor for type DuplicateCodeProvider
     */
    public static List<DuplicateCodeDescriptor> allDescriptors() {
        return Jenkins.getInstance().getDescriptorList(DuplicateCodeProvider.class);
    }

}
