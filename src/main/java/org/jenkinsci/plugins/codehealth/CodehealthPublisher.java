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
import org.jenkinsci.plugins.codehealth.model.Issue;
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
        final List<Issue> newIssues = new ArrayList<Issue>();
        final List<Issue> fixedIssues = new ArrayList<Issue>();
        for (IssueProvider issueProvider : IssueProvider.all()) {
            logConsole(listener, "Getting Issues from " + issueProvider.getClass().getName());
            newIssues.addAll(issueProvider.getExistingIssues(build));
            fixedIssues.addAll(issueProvider.getFixedIssues(build));
        }
        logConsole(listener, String.format("%s new/open issues, %s fixed issues.", newIssues.size(), fixedIssues.size()));
        issueRepository.updateIssues(newIssues, build);
        issueRepository.fixedIssues(fixedIssues, build);
        return true;
    }

    @Override
    public BuildStepDescriptor getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Extension
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

    private void logConsole(BuildListener listener, String message) {
        listener.getLogger().println(String.format("[CODEHEALTH] %s", message));
    }
}
