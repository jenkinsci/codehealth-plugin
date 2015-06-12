package org.jenkinsci.plugins.codehealth;

import com.google.inject.Inject;
import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.codehealth.model.IssueEntity;
import org.jenkinsci.plugins.codehealth.service.JPAIssueRepository;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Publisher which asks contributing plugins to report their issues.
 *
 * @author Michael Prankl
 */
public class CodehealthPublisher extends Recorder {

    @Inject
    private transient JPAIssueRepository issueRepository;

    @DataBoundConstructor
    public CodehealthPublisher() {
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        Jenkins.getInstance().getInjector().injectMembers(this);
        final List<IssueEntity> newIssues = new ArrayList<IssueEntity>();
        final List<IssueEntity> fixedIssues = new ArrayList<IssueEntity>();
        for (IssueProvider issueProvider : IssueProvider.all()) {
            final String origin = issueProvider.getOrigin();
            logConsole(listener, "Getting Issues from " + issueProvider.getClass().getName() + ", origin=" + origin);
            for (Issue issue : issueProvider.getExistingIssues(build)) {
                newIssues.add(mapToInternal(issue, origin));
            }
            for (Issue issue : issueProvider.getFixedIssues(build)) {
                fixedIssues.add(mapToInternal(issue, origin));
            }
        }
        logConsole(listener, String.format("%s new/open issues, %s fixed issues.", newIssues.size(), fixedIssues.size()));
        issueRepository.updateIssues(newIssues, build);
        issueRepository.fixedIssues(fixedIssues, build);
        return true;
    }

    private IssueEntity mapToInternal(final Issue issue, final String origin) {
        IssueEntity issueEntity;
        try {
            issueEntity = IssueFactory.getIssue(origin);
            issueEntity.setContextHashCode(issue.getContextHashCode());
            issueEntity.setPriority(issue.getPriority());
            issueEntity.setMessage(issue.getMessage());
        } catch (CloneNotSupportedException e) {
            issueEntity = new IssueEntity();
            issueEntity.setOrigin(origin);
            issueEntity.setContextHashCode(issue.getContextHashCode());
            issueEntity.setPriority(issue.getPriority());
            issueEntity.setMessage(issue.getMessage());
        }
        return issueEntity;
    }

    @Override
    public BuildStepDescriptor getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Extension(ordinal = Double.MIN_VALUE)
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Activate Codehealth";
        }
    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    private void logConsole(final BuildListener listener, final String message) {
        listener.getLogger().println(String.format("[CODEHEALTH] %s", message));
    }
}
