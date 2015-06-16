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

    /**
     * @param issueRepository the issue repository
     * @param issueProviders  registered issue providers
     */
    public TestingCodehealthPublisher(final JPAIssueRepository issueRepository, final ExtensionList<IssueProvider> issueProviders) {
        super(issueRepository);
        this.issueProviders = issueProviders;
    }

    @Override
    ExtensionList<IssueProvider> getIssueProviders() {
        return this.issueProviders;
    }

    @Override
    Injector getInjector() {
        return Mockito.mock(Injector.class);
    }
}
