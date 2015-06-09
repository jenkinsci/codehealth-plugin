package org.jenkinsci.plugins.codehealth.service;

import com.google.inject.Injector;
import org.jenkinsci.plugins.database.jpa.PersistenceService;
import org.mockito.Mockito;

/**
 * Subclass JPAIssueRepository to mock out the Jenkins singleton instance for testing purposes.
 *
 * @author Michael Prankl
 */
public class TestingJPAIssueRepository extends JPAIssueRepository {

    public TestingJPAIssueRepository(PersistenceService persistenceService) {
        super(persistenceService);
    }

    @Override
    public Injector getInjector() {
        return Mockito.mock(Injector.class);
    }
}
