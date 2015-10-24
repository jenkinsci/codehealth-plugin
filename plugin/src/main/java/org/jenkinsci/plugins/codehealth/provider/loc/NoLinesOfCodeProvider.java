package org.jenkinsci.plugins.codehealth.provider.loc;

import hudson.DescriptorExtensionList;
import hudson.Extension;
import hudson.model.AbstractBuild;
import jenkins.model.Jenkins;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nullable;

/**
 * @author Michael Prankl
 */
public class NoLinesOfCodeProvider extends LinesOfCodeProvider {

    @DataBoundConstructor
    public NoLinesOfCodeProvider() {
    }

    @Override
    public LinesOfCode getLOC(AbstractBuild<?, ?> build) {
        return null;
    }

    @Override
    public String getOrigin() {
        return "NoLinesOfCodeProvider";
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
    public static class DescriptorImpl extends LinesOfCodeDescriptor {
        @Override
        public String getDisplayName() {
            return "(None)";
        }
    }

    public static DescriptorExtensionList<LinesOfCodeProvider, LinesOfCodeDescriptor> all() {
        return Jenkins.getInstance().getDescriptorList(LinesOfCodeProvider.class);
    }
}
