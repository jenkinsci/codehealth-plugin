package org.jenkinsci.plugins.codehealth.service;

import com.google.inject.Inject;
import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.TopLevelItem;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.codehealth.model.Issue;
import org.jenkinsci.plugins.codehealth.model.State;
import org.jenkinsci.plugins.codehealth.model.StateHistory;
import org.jenkinsci.plugins.codehealth.util.AbstractIssueMapper;
import org.jenkinsci.plugins.database.jpa.PersistenceService;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
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
    private PersistenceService persistenceService;

    public JPAIssueRepository() {
        Jenkins.getInstance().getInjector().injectMembers(this);
    }


    @Override
    public void updateIssues(Collection data, AbstractBuild build, AbstractIssueMapper issueMapper) {
        final TopLevelItem topLevelItem = (TopLevelItem) build.getProject();
        final int buildNr = build.getNumber();
        LOG.log(Level.INFO, "Updating " + data.size() + " Issues for Top-Level-Item " + topLevelItem.getDisplayName() + " and Build #" + buildNr);
        try {
            final EntityManager em = persistenceService.getPerItemEntityManagerFactory(topLevelItem).createEntityManager();
            em.getTransaction().begin();
            for (Object item : data) {
                final Issue issue = issueMapper.map(item);
                Query q = em.createNamedQuery(Issue.FIND_BY_HASH_AND_ORIGIN);
                q.setParameter("contextHashCode", issue.getContextHashCode());
                q.setParameter("origin", issue.getOrigin());
                List<Issue> resultList = (List<Issue>) q.getResultList();
                if (resultList.size() == 1) {
                    Issue result = resultList.get(0);
                    if (result.getCurrentState().getState().equals(State.NEW)) {
                        openIssue(buildNr, em, result);

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
     * Transition from NEW to OPEN.
     *
     * @param buildNr the build nr
     * @param em      the entity manager
     * @param result  the corresponding issue
     */
    private void openIssue(int buildNr, EntityManager em, Issue result) {
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
    private void newIssue(int buildNr, EntityManager em, Issue issue) {
        final StateHistory stateNew = buildHistory(buildNr, State.NEW);
        issue.setStateHistory(new HashSet<StateHistory>());
        issue.getStateHistory().add(stateNew);
        issue.setCurrentState(stateNew);
        em.persist(issue);
    }

    private StateHistory buildHistory(int buildNr, State state) {
        final StateHistory stateHistory = new StateHistory();
        stateHistory.setBuildNr(buildNr);
        stateHistory.setState(state);
        stateHistory.setTimestamp(new Date());
        return stateHistory;
    }
}
