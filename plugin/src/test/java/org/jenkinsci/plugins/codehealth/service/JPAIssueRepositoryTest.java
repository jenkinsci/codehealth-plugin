package org.jenkinsci.plugins.codehealth.service;

import hudson.model.AbstractBuild;
import hudson.model.FreeStyleProject;
import hudson.model.TopLevelItem;
import org.jenkinsci.plugins.codehealth.model.*;
import org.jenkinsci.plugins.codehealth.provider.Priority;
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

    private TestingJPAIssueRepository issueRepository;
    private PersistenceService mockedPersistenceService;
    private AbstractBuild mockedBuild;
    private FreeStyleProject mockedTopLevelItem;
    private EntityManager mockedEntityManager;
    private Query mockedQuery;
    private JPABuildRepository mockBuildRepository;
    private Build build;

    @Before
    public void setUp() throws IOException, SQLException {
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
        mockBuildRepository = mock(JPABuildRepository.class);
        build = new Build();
        build.setNumber(33);
        build.setTimestamp(new Date());
        when(mockBuildRepository.loadBuild(any(Integer.class), any(TopLevelItem.class))).thenReturn(build);
        this.issueRepository = new TestingJPAIssueRepository(this.mockedPersistenceService, this.mockBuildRepository);
    }

    /**
     * Test persisting 2 new Issues.
     */
    @Test
    public void new_issues() {
        // setup 2 new issues
        Collection<IssueEntity> newIssues = new ArrayList<IssueEntity>();
        newIssues.add(buildIssue(1L, ORIGIN));
        newIssues.add(buildIssue(2L, ORIGIN));
        when(mockedQuery.getResultList()).thenReturn(Collections.emptyList());
        // act
        issueRepository.updateIssues(newIssues, mockedBuild);
        // verify
        verify(mockedEntityManager, times(2)).createNamedQuery(IssueEntity.FIND_BY_HASH_AND_ORIGIN);
        verify(mockedEntityManager, times(2)).persist(Mockito.any());
        verify(mockedEntityManager).close();
    }

    /**
     * Test transition from NEW to OPEN.
     */
    @Test
    public void open_issue() {
        // setup one issue which was present in last build
        Collection<IssueEntity> newIssues = new ArrayList<IssueEntity>();
        IssueEntity issue = buildIssue(1L, ORIGIN);
        addState(issue, State.NEW);
        newIssues.add(issue);
        List<IssueEntity> resultList = new ArrayList<IssueEntity>(newIssues);
        when(mockedQuery.getResultList()).thenReturn(resultList);
        // act
        issueRepository.updateIssues(newIssues, mockedBuild);
        // verify
        verify(mockedEntityManager).createNamedQuery(IssueEntity.FIND_BY_HASH_AND_ORIGIN);
        verify(mockedEntityManager).persist(Mockito.any());
        verify(mockedEntityManager).close();
    }

    private void addState(IssueEntity issue, State state) {
        StateHistory his = new StateHistory();
        his.setTimestamp(new Date());
        his.setId(1);
        his.setBuild(build);
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
        List<IssueEntity> closedIssues = new ArrayList<IssueEntity>();
        IssueEntity closingIssue = buildIssue(1L, ORIGIN);
        addState(closingIssue, State.OPEN);
        closedIssues.add(closingIssue);
        List<IssueEntity> resultList = new ArrayList<IssueEntity>(closedIssues);
        when(mockedQuery.getResultList()).thenReturn(resultList);
        // act
        issueRepository.fixedIssues(closedIssues, mockedBuild);
        // verify
        verify(mockedEntityManager).createNamedQuery(IssueEntity.FIND_BY_HASH_AND_ORIGIN);
        verify(mockedEntityManager).persist(Mockito.any());
        verify(mockedEntityManager).close();

    }


    private IssueEntity buildIssue(long contextHash, String origin) {
        IssueEntity i = new IssueEntity();
        i.setContextHashCode(contextHash);
        i.setOrigin(origin);
        i.setMessage("some message");
        i.setPriority(Priority.NORMAL);
        return i;
    }
}
