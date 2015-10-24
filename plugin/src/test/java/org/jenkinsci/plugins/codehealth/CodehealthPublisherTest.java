package org.jenkinsci.plugins.codehealth;

import hudson.ExtensionList;
import hudson.ExtensionPoint;
import hudson.Launcher;
import hudson.model.*;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.codehealth.provider.Priority;
import org.jenkinsci.plugins.codehealth.provider.issues.Issue;
import org.jenkinsci.plugins.codehealth.provider.issues.IssueProvider;
import org.jenkinsci.plugins.codehealth.provider.loc.LinesOfCodeProvider;
import org.jenkinsci.plugins.codehealth.service.JPABuildRepository;
import org.jenkinsci.plugins.codehealth.service.JPAIssueRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.util.*;

import static org.mockito.Matchers.anyCollection;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * @author Michael Prankl
 */
public class CodehealthPublisherTest {

    private Jenkins jenkins;
    private CodehealthPublisher publisher;
    private ExtensionList<IssueProvider> issueProviders;
    private ExtensionList<LinesOfCodeProvider> locProviders;
    private JPAIssueRepository issueRepository;
    private JPABuildRepository buildRepository;
    private FreeStyleProject topLevelItem;
    private AbstractBuild<?, ?> build;
    private Launcher launcher;
    private BuildListener buildListener;
    private List<IssueProvider> issueProviderList;
    private List<LinesOfCodeProvider> locProviderList;

    @Before
    public void setup() {
        this.issueProviderList = new ArrayList<IssueProvider>();
        this.locProviderList = new ArrayList<LinesOfCodeProvider>();
        this.topLevelItem = mock(FreeStyleProject.class);
        this.build = mock(AbstractBuild.class);
        when(this.build.getProject()).thenReturn((AbstractProject) this.topLevelItem);
        this.launcher = mock(Launcher.class);
        this.buildListener = mock(BuildListener.class);
        when(this.buildListener.getLogger()).thenReturn(System.out);
        this.jenkins = mock(Hudson.class);
        this.issueProviders = setupIssueProviders(this.jenkins, this.issueProviderList);
        this.locProviders = setupLocProviders(this.jenkins, this.locProviderList);
        this.issueRepository = mock(JPAIssueRepository.class);
        this.buildRepository = mock(JPABuildRepository.class);
        this.publisher = new TestingCodehealthPublisher(this.issueRepository, this.buildRepository, this.issueProviders, this.locProviders);
    }

    /**
     * Given provider "findbugs" with 5 existing issues and 2 fixed issues
     * And provider "checkstyle" with 10 existing issues and that can not provide fixed issues
     * When the codehealth publisher collects these issues from these providers
     * Then in total 15 existing issues are reported
     * And in total 2 fixed issues are reported
     * And for provider "checkstyle" fixed issues are calculated
     *
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void issues_roundtrip() throws IOException, InterruptedException {
        // setup
        this.issueProviderList.add(buildIssueProvider("findbugs", 5, 2, true));
        this.issueProviderList.add(buildIssueProvider("checkstyle", 10, 0, false));
        // act
        this.publisher.perform(this.build, this.launcher, this.buildListener);
        // verify 15 new/open issues are reported
        verify(issueRepository).updateIssues(argThat(hasSizeOf(15)), eq(this.build));
        // verify 2 fixed issues are reported
        verify(issueRepository).fixedIssues(argThat(hasSizeOf(2)), eq(this.build));
        // verify that for checkstyle fixed issues are calculated
        verify(issueRepository).calculateFixedIssues(eq(this.topLevelItem), anyCollection(), eq("checkstyle"));
    }

    /**
     * Given a provider "BROKEN-PROVIDER" that returns null-values instead of collections
     * When the codehealth publisher collects from these providers
     * Then this provider is silently skipped
     * And other providers are handled correctly
     *
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    public void issues_null_tolerance() throws IOException, InterruptedException {
        // setup
        this.issueProviderList.add(new IssueProvider() {
            @Override
            public Collection<Issue> getExistingIssues(AbstractBuild<?, ?> build) {
                return null;
            }

            @Override
            public Collection<Issue> getFixedIssues(AbstractBuild<?, ?> build) {
                return null;
            }

            @Override
            public String getOrigin() {
                return "BROKEN-PROVIDER";
            }

            @Nonnull
            @Override
            public String getOriginPluginName() {
                return "Broken Provider Plugin";
            }

            @Override
            public boolean canProvideFixedIssues() {
                return true;
            }

            @Nullable
            @Override
            public String getProjectResultUrlName() {
                return getOrigin();
            }

            @Nullable
            @Override
            public String getBuildResultUrlName() {
                return getOrigin();
            }
        });
        this.issueProviderList.add(buildIssueProvider("checkstyle", 10, 0, false));
        // act
        this.publisher.perform(this.build, this.launcher, this.buildListener);
        // verify 15 new/open issues are reported
        verify(issueRepository).updateIssues(argThat(hasSizeOf(10)), eq(this.build));
        // verify 2 fixed issues are reported
        verify(issueRepository).fixedIssues(argThat(hasSizeOf(0)), eq(this.build));
        // verify that for checkstyle fixed issues are calculated
        verify(issueRepository).calculateFixedIssues(eq(this.topLevelItem), anyCollection(), eq("checkstyle"));
    }

    private CollectionSizeArgumentMatcher hasSizeOf(final int size) {
        return new CollectionSizeArgumentMatcher(size);
    }

    private ExtensionList<IssueProvider> setupIssueProviders(final Jenkins jenkins, final List<IssueProvider> issueProviderList) {
        return new MockExtensionList(jenkins, IssueProvider.class, issueProviderList);
    }

    private ExtensionList<LinesOfCodeProvider> setupLocProviders(Jenkins jenkins, List<LinesOfCodeProvider> locProviderList) {
        return new MockExtensionList(jenkins, LinesOfCodeProvider.class, locProviderList);
    }

    private IssueProvider buildIssueProvider(final String origin, final int existingIssues, final int fixedIssues, final boolean canProvideFixedIssues) {
        final List<Issue> existingIssueList = buildIssues(existingIssues);
        final List<Issue> fixedIssueList = buildIssues(fixedIssues);
        return new IssueProvider() {
            @Override
            public Collection<Issue> getExistingIssues(AbstractBuild<?, ?> build) {
                return existingIssueList;
            }

            @Override
            public Collection<Issue> getFixedIssues(AbstractBuild<?, ?> build) {
                if (canProvideFixedIssues) {
                    return fixedIssueList;
                } else {
                    return Collections.emptyList();
                }
            }

            @Override
            public String getOrigin() {
                return origin;
            }

            @Nonnull
            @Override
            public String getOriginPluginName() {
                return origin;
            }

            @Override
            public boolean canProvideFixedIssues() {
                return canProvideFixedIssues;
            }

            @Nullable
            @Override
            public String getProjectResultUrlName() {
                return getOrigin();
            }

            @Nullable
            @Override
            public String getBuildResultUrlName() {
                return getOrigin();
            }
        };
    }

    private List<Issue> buildIssues(final int issueCount) {
        final List<Issue> issues = new ArrayList<Issue>(issueCount);
        for (int i = 0; i < issueCount; i++) {
            issues.add(new Issue(i, "some message", Priority.HIGH));
        }
        return issues;
    }


    private class MockExtensionList<T extends ExtensionPoint> extends ExtensionList<T> {

        private List<T> providers;

        protected MockExtensionList(final Jenkins jenkins, final Class<T> extensionType, final List<T> providers) {
            super(jenkins, extensionType);
            this.providers = providers;
        }

        @Override
        public Iterator<T> iterator() {
            return this.providers.iterator();
        }

        @Override
        public int size() {
            return providers.size();
        }
    }

    /**
     * Mockito argument matcher that only checks for collection size.
     */
    private class CollectionSizeArgumentMatcher extends ArgumentMatcher<Collection> {

        private final int size;

        public CollectionSizeArgumentMatcher(final int size) {
            this.size = size;
        }

        @Override
        public boolean matches(Object o) {
            return ((Collection) o).size() == this.size;
        }
    }
}
