package org.jenkinsci.plugins.codehealth;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;
import com.google.inject.Injector;
import hudson.Extension;
import hudson.ExtensionList;
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
import org.jenkinsci.plugins.codehealth.provider.duplicates.DuplicateCode;
import org.jenkinsci.plugins.codehealth.provider.duplicates.DuplicateCodeProvider;
import org.jenkinsci.plugins.codehealth.provider.duplicates.NoDuplicateCodeProvider;
import org.jenkinsci.plugins.codehealth.provider.issues.Issue;
import org.jenkinsci.plugins.codehealth.provider.issues.IssueFactory;
import org.jenkinsci.plugins.codehealth.provider.issues.IssueProvider;
import org.jenkinsci.plugins.codehealth.provider.loc.LinesOfCode;
import org.jenkinsci.plugins.codehealth.provider.loc.LinesOfCodeProvider;
import org.jenkinsci.plugins.codehealth.provider.loc.NoLinesOfCodeProvider;
import org.jenkinsci.plugins.codehealth.service.JPABuildRepository;
import org.jenkinsci.plugins.codehealth.service.JPADuplicateCodeRepository;
import org.jenkinsci.plugins.codehealth.service.JPAIssueRepository;
import org.jenkinsci.plugins.codehealth.service.JPALinesOfCodeRepository;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Publisher which asks contributing plugins to report their issues.
 *
 * @author Michael Prankl
 */
public class CodehealthPublisher extends Recorder {

    private LinesOfCodeProvider linesOfCodeProvider;
    private DuplicateCodeProvider duplicateCodeProvider;

    @Inject
    private transient JPAIssueRepository issueRepository;

    @Inject
    private transient JPALinesOfCodeRepository locRepository;

    @Inject
    private transient JPADuplicateCodeRepository duplicateCodeRepository;

    @Inject
    private transient JPABuildRepository buildRepository;

    @DataBoundConstructor
    public CodehealthPublisher(LinesOfCodeProvider linesOfCodeProvider, DuplicateCodeProvider duplicateCodeProvider) {
        this.linesOfCodeProvider = linesOfCodeProvider;
        this.duplicateCodeProvider = duplicateCodeProvider;
    }

    @VisibleForTesting
    public CodehealthPublisher(JPAIssueRepository issueRepository, JPABuildRepository jpaBuildRepository) {
        this.issueRepository = issueRepository;
        this.buildRepository = jpaBuildRepository;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        this.getInjector().injectMembers(this);
        handleNewBuild(build, listener);
        handleIssues(build, listener);
        handleLinesOfCode(build, listener);
        handleDuplicateCode(build, listener);
        return true;
    }

    private void handleNewBuild(AbstractBuild<?, ?> build, BuildListener listener) {
        buildRepository.save(build, (hudson.model.TopLevelItem) build.getProject());
    }

    private void handleDuplicateCode(AbstractBuild<?, ?> build, BuildListener listener) {
        DuplicateCodeProvider dupProvider = getDuplicateCodeProvider();
        if (dupProvider != null) {
            logConsole(listener, "Getting duplicate code from " + dupProvider.getClass().getName());
            DuplicateCode duplicateCode = dupProvider.getDuplicateCode(build);
            if (duplicateCode != null) {
                logConsole(listener, "Duplicate lines: " + duplicateCode.getDuplicateLines() + ", files: " + duplicateCode.getFilesWithDuplicates());
                duplicateCodeRepository.save(duplicateCode, build);
            }
        } else {
            logConsole(listener, "No provider for Duplicate Code specified.");
        }
    }

    private void handleLinesOfCode(AbstractBuild<?, ?> build, BuildListener listener) {
        LinesOfCodeProvider locProvider = getLinesOfCodeProvider();
        if (locProvider != null) {
            logConsole(listener, "Getting LoC from " + locProvider.getClass().getName());
            LinesOfCode loc = locProvider.getLOC(build);
            if (loc != null) {
                logConsole(listener, "Line count: " + loc.getLinesOfCode() + ", File count: " + loc.getFileCount());
                locRepository.save(loc, build);
            }
        } else {
            logConsole(listener, "No provider for Lines Of Code (LOC) specified.");
        }

    }

    private void handleIssues(AbstractBuild<?, ?> build, BuildListener listener) {
        final List<IssueEntity> newIssues = new ArrayList<IssueEntity>();
        final List<IssueEntity> fixedIssues = new ArrayList<IssueEntity>();
        for (IssueProvider issueProvider : getIssueProviders()) {
            final String origin = issueProvider.getOrigin();
            logConsole(listener, "Getting Issues from " + issueProvider.getClass().getName() + ", origin=" + origin);
            Collection<Issue> existingIssues = issueProvider.getExistingIssues(build);
            if (existingIssues != null) {
                for (Issue issue : existingIssues) {
                    newIssues.add(mapToInternal(issue, origin));
                }
            }
            if (issueProvider.canProvideFixedIssues()) {
                Collection<Issue> issueProviderFixedIssues = issueProvider.getFixedIssues(build);
                if (issueProviderFixedIssues != null) {
                    for (Issue issue : issueProviderFixedIssues) {
                        fixedIssues.add(mapToInternal(issue, origin));
                    }
                }
            } else {
                fixedIssues.addAll(calculateFixedIssues(build, existingIssues, origin));
            }
        }
        logConsole(listener, String.format("%s new/open issues, %s fixed issues.", newIssues.size(), fixedIssues.size()));
        issueRepository.updateIssues(newIssues, build);
        issueRepository.fixedIssues(fixedIssues, build);
    }

    private Collection<? extends IssueEntity> calculateFixedIssues(final AbstractBuild<?, ?> build,
                                                                   final Collection<Issue> existingIssues, final String origin) {
        final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
        try {
            return issueRepository.calculateFixedIssues((hudson.model.TopLevelItem) build.getProject(), existingIssues, origin);
        } finally {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
        }
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
        return DESCRIPTOR;
    }

    public LinesOfCodeProvider getLinesOfCodeProvider() {
        return linesOfCodeProvider;
    }

    public void setLinesOfCodeProvider(LinesOfCodeProvider linesOfCodeProvider) {
        if (linesOfCodeProvider instanceof NoLinesOfCodeProvider) {
            linesOfCodeProvider = null;
        }
        this.linesOfCodeProvider = linesOfCodeProvider;
    }

    public DuplicateCodeProvider getDuplicateCodeProvider() {
        return duplicateCodeProvider;
    }

    public void setDuplicateCodeProvider(DuplicateCodeProvider duplicateCodeProvider) {
        if (duplicateCodeProvider instanceof NoDuplicateCodeProvider) {
            duplicateCodeProvider = null;
        }
        this.duplicateCodeProvider = duplicateCodeProvider;
    }

    @Extension(ordinal = Double.MIN_VALUE)
    public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

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

    @VisibleForTesting
    Injector getInjector() {
        return Jenkins.getInstance().getInjector();
    }

    @VisibleForTesting
    ExtensionList<IssueProvider> getIssueProviders() {
        return IssueProvider.all();
    }

}
