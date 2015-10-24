package org.jenkinsci.plugins.codehealth.provider.duplicates;

import hudson.DescriptorExtensionList;
import hudson.Extension;
import hudson.model.AbstractBuild;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nullable;

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

    @Nullable
    @Override
    public String getBuildResultUrl() {
        return null;
    }

    @Nullable
    @Override
    public String getProjectResultUrl() {
        return null;
    }

    @Extension
    public static class DescriptorImpl extends DuplicateCodeDescriptor {
        @Override
        public String getDisplayName() {
            return "(None)";
        }
    }

    public static DescriptorExtensionList<DuplicateCodeProvider, DuplicateCodeDescriptor> all() {
        return Jenkins.getInstance().getDescriptorList(DuplicateCodeProvider.class);
    }
}
