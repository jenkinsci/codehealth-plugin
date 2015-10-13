package org.jenkinsci.plugins.codehealth.provider.loc;

import hudson.model.AbstractBuild;
import org.kohsuke.stapler.DataBoundConstructor;

import java.util.ArrayList;
import java.util.List;

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

    public static class DescriptorImpl extends LinesOfCodeDescriptor {
        @Override
        public String getDisplayName() {
            return "(None)";
        }
    }

    public static List<LinesOfCodeDescriptor> allPlusNone() {
        final List<LinesOfCodeDescriptor> descriptors = new ArrayList<LinesOfCodeDescriptor>();
        descriptors.add(new DescriptorImpl());
        descriptors.addAll(LinesOfCodeDescriptor.all());
        return descriptors;
    }
}
