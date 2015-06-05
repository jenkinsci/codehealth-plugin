package org.jenkinsci.plugins.codehealth.service;

import hudson.model.AbstractBuild;
import hudson.model.FreeStyleProject;
import hudson.model.TopLevelItem;
import org.jenkinsci.plugins.codehealth.model.Issue;
import org.jenkinsci.plugins.codehealth.model.Priority;
import org.jenkinsci.plugins.codehealth.model.State;
import org.jenkinsci.plugins.codehealth.model.StateHistory;
import org.jenkinsci.plugins.codehealth.util.AbstractIssueMapper;
import org.jenkinsci.plugins.database.jpa.PersistenceService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

import static org.mockito.Mockito.*;

/**
 * @author Michael Prankl
 */
public class JPAIssueRepositoryTest {

    private static final String ORIGIN = "TEST";

    private IssueRepository issueRepository;
    private PersistenceService mockedPersistenceService;
    private AbstractBuild mockedBuild;
    private FreeStyleProject mockedTopLevelItem;
    private EntityManager mockedEntityManager;
    private Query mockedQuery;
    private AbstractIssueMapper issueMapper;

    @Before
    public void setUp() throws IOException, SQLException {
        issueMapper = new TestIssueMapper();
        mockedTopLevelItem = mock(FreeStyleProject.class);
        when(mockedTopLevelItem.getDisplayName()).thenReturn("mock job");
        mockedBuild = mock(AbstractBuild.class);
        when(mockedBuild.getNumber()).thenReturn(33);
        when(mockedBuild.getProject()).thenReturn((hudson.model.AbstractProject) mockedTopLevelItem);
        mockedPersistenceService = mock(PersistenceService.class);
        mockedEntityManager = mock(EntityManager.class);
        EntityManagerFactory entityManagerFactory = mock(EntityManagerFactory.class);
        when(mockedPersistenceService.getPerItemEntityManagerFactory(Mockito.any(TopLevelItem.class))).thenReturn(entityManagerFactory);
        when(entityManagerFactory.createEntityManager()).thenReturn(mockedEntityManager);
        when(mockedEntityManager.getTransaction()).thenReturn(mock(EntityTransaction.class));
        mockedQuery = mock(Query.class);
        when(mockedEntityManager.createNamedQuery(Mockito.any(String.class))).thenReturn(mockedQuery);
        this.issueRepository = new JPAIssueRepository(this.mockedPersistenceService);
    }

    /**
     * Test persisting 2 new Issues.
     */
    @Test
    public void new_issues() {
        // setup 2 new issues
        Collection<Issue> newIssues = new ArrayList<Issue>();
        newIssues.add(buildIssue(1L, ORIGIN));
        newIssues.add(buildIssue(2L, ORIGIN));
        when(mockedQuery.getResultList()).thenReturn(Collections.emptyList());
        // act
        issueRepository.updateIssues(newIssues, mockedBuild, issueMapper);
        // verify
        verify(mockedEntityManager, times(2)).createNamedQuery(Issue.FIND_BY_HASH_AND_ORIGIN);
        verify(mockedEntityManager, times(2)).persist(Mockito.any());
        verify(mockedEntityManager).close();
    }

    /**
     * Test transition from NEW to OPEN.
     */
    @Test
    public void open_issue() {
        // setup one issue which was present in last build
        Collection<Issue> newIssues = new ArrayList<Issue>();
        Issue issue = buildIssue(1L, ORIGIN);
        addState(issue, State.NEW);
        newIssues.add(issue);
        List<Issue> resultList = new ArrayList<Issue>(newIssues);
        when(mockedQuery.getResultList()).thenReturn(resultList);
        // act
        issueRepository.updateIssues(newIssues, mockedBuild, issueMapper);
        // verify
        verify(mockedEntityManager).createNamedQuery(Issue.FIND_BY_HASH_AND_ORIGIN);
        verify(mockedEntityManager).persist(Mockito.any());
        verify(mockedEntityManager).close();
    }

    private void addState(Issue issue, State state) {
        StateHistory his = new StateHistory();
        his.setTimestamp(new Date());
        his.setBuildNr(32);
        his.setId(1);
        his.setState(state);
        issue.setCurrentState(his);
        if (issue.getStateHistory() == null) {
            issue.setStateHistory(new HashSet<StateHistory>());
        }
        issue.getStateHistory().add(his);
    }

    /**
     * Test transition from NEW/OPEN to CLOSED.
     */
    @Test
    public void close_issue() {
        // setup
        Collection<Issue> closedIssues = new ArrayList<Issue>();
        Issue closingIssue = buildIssue(1L, ORIGIN);
        addState(closingIssue, State.OPEN);
        closedIssues.add(closingIssue);
        List<Issue> resultList = new ArrayList<Issue>(closedIssues);
        when(mockedQuery.getResultList()).thenReturn(resultList);
        // act
        issueRepository.fixedIssues(closedIssues, mockedBuild, issueMapper);
        // verify
        verify(mockedEntityManager).createNamedQuery(Issue.FIND_BY_HASH_AND_ORIGIN);
        verify(mockedEntityManager).persist(Mockito.any());
        verify(mockedEntityManager).close();

    }

    private class TestIssueMapper extends AbstractIssueMapper<Issue> {
        @Override
        public Issue map(Issue o) {
            return o;
        }
    }

    private Issue buildIssue(long contextHash, String origin) {
        Issue i = new Issue();
        i.setContextHashCode(contextHash);
        i.setOrigin(origin);
        i.setMessage("some message");
        i.setPriority(Priority.NORMAL);
        return i;
    }
}
