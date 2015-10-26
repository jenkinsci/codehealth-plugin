package org.jenkinsci.plugins.codehealth.provider.loc;

import hudson.DescriptorExtensionList;
import hudson.ExtensionList;
import hudson.ExtensionPoint;
import hudson.model.AbstractBuild;
import hudson.model.AbstractDescribableImpl;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.codehealth.provider.ProvidesResultUrls;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * Extension point for code analysis plugins which want to contribute a LOC metric.
 *
 * @author Michael Prankl
 */
public abstract class LinesOfCodeProvider extends AbstractDescribableImpl<LinesOfCodeProvider>
        implements ExtensionPoint, ProvidesResultUrls {

    /**
     * @return the LOC of the build
     */
    public abstract LinesOfCode getLOC(AbstractBuild<?, ?> build);

    /**
     * @return unique identifier of the contributing plugin
     */
    @Nonnull
    public abstract String getOrigin();

    @Override
    public LinesOfCodeDescriptor getDescriptor() {
        return (LinesOfCodeDescriptor) super.getDescriptor();
    }

    /**
     * @return all registered {@link LinesOfCodeProvider}s.
     */
    public static ExtensionList<LinesOfCodeProvider> all() {
        return Jenkins.getInstance().getExtensionList(LinesOfCodeProvider.class);
    }

    /**
     * @return all registered Descriptor for type LinesOfCodeProvider
     */
    public static List<LinesOfCodeDescriptor> allDescriptors() {
        return Jenkins.getInstance().getDescriptorList(LinesOfCodeProvider.class);
    }

}
