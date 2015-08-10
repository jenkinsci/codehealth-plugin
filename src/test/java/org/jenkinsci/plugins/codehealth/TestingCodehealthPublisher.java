package org.jenkinsci.plugins.codehealth;

import com.google.inject.Injector;
import hudson.ExtensionList;
import org.jenkinsci.plugins.codehealth.service.JPAIssueRepository;
import org.mockito.Mockito;

/**
 * Subclassed CodehealthPublisher to mock out Jenkins singletons for testing purposes.
 *
 * @author Michael Prankl
 */
public class TestingCodehealthPublisher extends CodehealthPublisher {

    private final ExtensionList<IssueProvider> issueProviders;
    private final ExtensionList<LinesOfCodeProvider> locProviders;

    /**
     * @param issueRepository the issue repository
     * @param issueProviders  registered issue providers
     */
    public TestingCodehealthPublisher(final JPAIssueRepository issueRepository, final ExtensionList<IssueProvider> issueProviders, final ExtensionList<LinesOfCodeProvider> locProviders) {
        super(issueRepository);
        this.issueProviders = issueProviders;
        this.locProviders = locProviders;
    }

    @Override
    ExtensionList<IssueProvider> getIssueProviders() {
        return this.issueProviders;
    }

    @Override
    ExtensionList<LinesOfCodeProvider> getLoCProviders() {
        return this.locProviders;
    }

    @Override
    Injector getInjector() {
        return Mockito.mock(Injector.class);
    }
}
