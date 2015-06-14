package org.jenkinsci.plugins.codehealth.service;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;
import com.google.inject.Injector;
import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.TopLevelItem;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.codehealth.Issue;
import org.jenkinsci.plugins.codehealth.model.IssueEntity;
import org.jenkinsci.plugins.codehealth.model.State;
import org.jenkinsci.plugins.codehealth.model.StateHistory;
import org.jenkinsci.plugins.database.jpa.PersistenceService;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of IssueRepository based on JPA and the database-(h2-)plugin.
 *
 * @author Michael Prankl
 */
@Extension
public class JPAIssueRepository extends IssueRepository {

    private final static Logger LOG = Logger.getLogger(JPAIssueRepository.class.getName());

    @Inject
    private transient PersistenceService persistenceService;

    public JPAIssueRepository() {
    }

    @VisibleForTesting
    public JPAIssueRepository(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }


    @Override
    public void updateIssues(Collection<IssueEntity> data, AbstractBuild<?, ?> build) {
        this.getInjector().injectMembers(this);
        final TopLevelItem topLevelItem = (TopLevelItem) build.getProject();
        final int buildNr = build.getNumber();
        LOG.log(Level.INFO, "Updating " + data.size() + " Issues for Top-Level-Item " + topLevelItem.getDisplayName() + " and Build #" + buildNr);
        try {
            final EntityManager em = persistenceService.getPerItemEntityManagerFactory(topLevelItem).createEntityManager();
            em.getTransaction().begin();
            for (IssueEntity issue : data) {
                List<IssueEntity> resultList = queryIssue(em, issue);
                if (resultList.size() == 1) {
                    IssueEntity result = resultList.get(0);
                    if (result.getCurrentState().getState().equals(State.NEW)) {
                        openIssue(buildNr, em, result);
                    } else if (result.getCurrentState().getState().equals(State.CLOSED)) {
                        // reopen issue -> State NEW
                        reopenIssue(buildNr, em, result);
                    }
                } else {
                    if (issue.getCurrentState() == null) {
                        newIssue(buildNr, em, issue);
                    }
                }
            }
            em.getTransaction().commit();
            em.close();
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "Unable to update issues.", e);
        } catch (IOException e) {
            LOG.log(Level.WARNING, "Unable to update issues.", e);
        }
    }

    /**
     * Transition from CLOSED to NEW (reopen issue).
     *
     * @param buildNr the build nr
     * @param em      the entity manager
     * @param result  the corresponding issue
     */
    private void reopenIssue(int buildNr, EntityManager em, IssueEntity result) {
        final StateHistory stateNew = buildHistory(buildNr, State.NEW);
        result.getStateHistory().add(stateNew);
        result.setCurrentState(stateNew);
        em.persist(stateNew);
    }

    /**
     * Transition from NEW to OPEN.
     *
     * @param buildNr the build nr
     * @param em      the entity manager
     * @param result  the corresponding issue
     */
    private void openIssue(int buildNr, EntityManager em, IssueEntity result) {
        final StateHistory stateOpen = buildHistory(buildNr, State.OPEN);
        result.getStateHistory().add(stateOpen);
        result.setCurrentState(stateOpen);
        em.persist(result);
    }

    /**
     * Persists a new Issue.
     *
     * @param buildNr the build nr
     * @param em      the entity manager
     * @param issue   the new Issue
     */
    private void newIssue(int buildNr, EntityManager em, IssueEntity issue) {
        final StateHistory stateNew = buildHistory(buildNr, State.NEW);
        issue.setStateHistory(new HashSet<StateHistory>());
        issue.getStateHistory().add(stateNew);
        issue.setCurrentState(stateNew);
        em.persist(issue);
    }

    /**
     * Construct a StateHistory.
     *
     * @param buildNr the build nr
     * @param state   the state
     * @return the constructed StateHistory
     */
    private StateHistory buildHistory(int buildNr, State state) {
        final StateHistory stateHistory = new StateHistory();
        stateHistory.setBuildNr(buildNr);
        stateHistory.setState(state);
        stateHistory.setTimestamp(new Date());
        return stateHistory;
    }

    @Override
    public void fixedIssues(Collection<IssueEntity> data, AbstractBuild<?, ?> build) {
        this.getInjector().injectMembers(this);
        final TopLevelItem topLevelItem = (TopLevelItem) build.getProject();
        final int buildNr = build.getNumber();
        LOG.log(Level.INFO, data.size() + " Issues for Top-Level-Item " + topLevelItem.getDisplayName() + " and Build #" + buildNr + " have been marked as fixed.");
        try {
            final EntityManager em = persistenceService.getPerItemEntityManagerFactory(topLevelItem).createEntityManager();
            em.getTransaction().begin();
            for (IssueEntity issue : data) {
                List<IssueEntity> resultList = queryIssue(em, issue);
                if (resultList.size() == 1) {
                    IssueEntity result = resultList.get(0);
                    if (result.getCurrentState().getState().equals(State.NEW) || result.getCurrentState().getState().equals(State.OPEN)) {
                        closeIssue(buildNr, em, result);
                    } else if (result.getCurrentState().getState().equals(State.CLOSED)) {
                        LOG.log(Level.INFO, String.format("Issue(ID=%s) has already been closed with build #%s", result.getId(), result.getCurrentState().getBuildNr()));
                    }
                } else {
                    LOG.log(Level.WARNING, "Couldn't find corresponding issue to close.");
                }
            }
            em.getTransaction().commit();
            em.close();
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "Unable to update issues.", e);
        } catch (IOException e) {
            LOG.log(Level.WARNING, "Unable to update issues.", e);
        }
    }

    private List<IssueEntity> queryIssue(EntityManager em, IssueEntity issue) {
        Query q = em.createNamedQuery(IssueEntity.FIND_BY_HASH_AND_ORIGIN);
        q.setParameter("contextHashCode", issue.getContextHashCode());
        q.setParameter("origin", issue.getOrigin());
        return (List<IssueEntity>) q.getResultList();
    }

    private void closeIssue(int buildNr, EntityManager em, IssueEntity result) {
        StateHistory closed = buildHistory(buildNr, State.CLOSED);
        result.getStateHistory().add(closed);
        result.setCurrentState(closed);
        em.persist(result);
    }


    @Override
    public Collection<IssueEntity> loadIssues(TopLevelItem topLevelItem) {
        this.getInjector().injectMembers(this);
        try {
            final EntityManager em = persistenceService.getPerItemEntityManagerFactory(topLevelItem).createEntityManager();
            Query q = em.createNamedQuery(IssueEntity.FIND_ALL);
            return q.getResultList();
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "Unable to query issues.", e);
        } catch (IOException e) {
            LOG.log(Level.WARNING, "Unable to query issues.", e);
        }
        return Collections.emptyList();
    }

    @Override
    public Collection<IssueEntity> loadIssues(TopLevelItem topLevelItem, List<State> states) {
        this.getInjector().injectMembers(this);
        try {
            final EntityManager em = persistenceService.getPerItemEntityManagerFactory(topLevelItem).createEntityManager();
            Query q = em.createNamedQuery(IssueEntity.FIND_BY_STATE);
            q.setParameter("state", states);
            return q.getResultList();
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "Unable to query issues.", e);
        } catch (IOException e) {
            LOG.log(Level.WARNING, "Unable to query issues.", e);
        }
        return Collections.emptyList();
    }

    @Override
    public Collection<IssueEntity> loadIssues(TopLevelItem topLevelItem, int buildNr, State state) {
        this.getInjector().injectMembers(this);
        try {
            final EntityManager em = persistenceService.getPerItemEntityManagerFactory(topLevelItem).createEntityManager();
            Query q = em.createNamedQuery(IssueEntity.FIND_BY_STATE_AND_BUILD);
            q.setParameter("buildNr", buildNr);
            q.setParameter("state", state);
            return q.getResultList();
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "Unable to query issues.", e);
        } catch (IOException e) {
            LOG.log(Level.WARNING, "Unable to query issues.", e);
        }
        return Collections.emptyList();
    }

    @Override
    public Collection<IssueEntity> calculateFixedIssues(TopLevelItem topLevelItem, Collection<Issue> existingIssues, String origin) {
        this.getInjector().injectMembers(this);
        Collection<IssueEntity> fixedIssues = new ArrayList<IssueEntity>();
        List<State> openStates = new ArrayList<State>();
        openStates.add(State.OPEN);
        openStates.add(State.NEW);
        try {
            final EntityManager em = persistenceService.getPerItemEntityManagerFactory(topLevelItem).createEntityManager();
            Query q = em.createNamedQuery(IssueEntity.FIND_BY_ORIGIN_AND_STATE);
            q.setParameter("states", openStates);
            q.setParameter("origin", origin);
            List<IssueEntity> openIssues = q.getResultList();
            for (IssueEntity openIssue : openIssues) {
                boolean fixed = true;
                for (Issue existingIssue : existingIssues) {
                    if (existingIssue.getContextHashCode() == openIssue.getContextHashCode()) {
                        fixed = false;
                        break;
                    }
                }
                if (fixed) {
                    fixedIssues.add(openIssue);
                }
            }
            return fixedIssues;
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "Unable to query issues.", e);
        } catch (IOException e) {
            LOG.log(Level.WARNING, "Unable to query issues.", e);
        }
        return Collections.emptyList();
    }

    /**
     * Can be overriden in testing subclasses to get rid of the singleton dependency.
     *
     * @return the Guice Injector
     */
    @VisibleForTesting
    public Injector getInjector() {
        return Jenkins.getInstance().getInjector();
    }
}
