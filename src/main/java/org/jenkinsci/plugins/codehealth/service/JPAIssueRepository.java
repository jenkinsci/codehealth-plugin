package org.jenkinsci.plugins.codehealth.service;

import com.google.inject.Inject;
import hudson.Extension;
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
    public void newIssues(Collection data, TopLevelItem topLevelItem, AbstractIssueMapper issueMapper) {
        LOG.log(Level.INFO, "Saving " + data.size() + " new Issues for Top-Level-Item " + topLevelItem.getDisplayName() + ".");
        try {
            EntityManager em = persistenceService.getPerItemEntityManagerFactory(topLevelItem).createEntityManager();
            em.getTransaction().begin();
            for (Object item : data) {
                Issue issue = issueMapper.map(item);
                Query q = em.createNamedQuery(Issue.FIND_BY_HASH_AND_ORIGIN);
                q.setParameter("contextHashCode", issue.getContextHashCode());
                q.setParameter("origin", issue.getOrigin());
                List<Issue> resultList = (List<Issue>) q.getResultList();
                if (resultList.size() == 1) {
                    Issue result = resultList.get(0);
                    if (result.getCurrentState().getState().equals(State.NEW)) {
                        // transition into OPEN
                        StateHistory stateOpen = new StateHistory();
                        stateOpen.setState(State.OPEN);
                        stateOpen.setTimestamp(new Date());
                        result.getStateHistory().add(stateOpen);
                        result.setCurrentState(stateOpen);
                        em.persist(result);
                    }
                } else {
                    if (issue.getCurrentState() == null) {
                        StateHistory stateNew = new StateHistory();
                        stateNew.setTimestamp(new Date());
                        stateNew.setState(State.NEW);
                        issue.setStateHistory(new HashSet<StateHistory>());
                        issue.getStateHistory().add(stateNew);
                        issue.setCurrentState(stateNew);
                        em.persist(issue);
                    }
                }
            }
            em.getTransaction().commit();
            em.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
