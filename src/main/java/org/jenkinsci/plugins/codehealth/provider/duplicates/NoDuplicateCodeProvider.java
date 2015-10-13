package org.jenkinsci.plugins.codehealth.provider.duplicates;

import hudson.model.AbstractBuild;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Michael Prankl
 */
public class NoDuplicateCodeProvider extends DuplicateCodeProvider {

    @DataBoundConstructor
    public NoDuplicateCodeProvider() {
    }

    @Override
    public DuplicateCode getDuplicateCode(AbstractBuild<?, ?> build) {
        return null;
    }

    @Override
    public String getOrigin() {
        return "NoDuplicateCodeProvider";
    }

    @Override
    public String getBuildResultUrl() {
        return null;
    }

    public static class DescriptorImpl extends DuplicateCodeDescriptor {
        @Override
        public String getDisplayName() {
            return "(None)";
        }
    }

    public static List<DuplicateCodeDescriptor> allPlusNone() {
        final List<DuplicateCodeDescriptor> descriptors = new ArrayList<DuplicateCodeDescriptor>();
        descriptors.add(new DescriptorImpl());
        descriptors.addAll(DuplicateCodeDescriptor.all());
        return descriptors;
    }
}
