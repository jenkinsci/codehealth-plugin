package org.jenkinsci.plugins.codehealth.service;

import com.google.inject.Inject;
import hudson.Extension;
import hudson.model.TopLevelItem;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.codehealth.model.Issue;
import org.jenkinsci.plugins.codehealth.model.State;
import org.jenkinsci.plugins.codehealth.model.StateHistory;
import org.jenkinsci.plugins.database.jpa.PersistenceService;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
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
    public void newIssues(Collection<Issue> issues, TopLevelItem topLevelItem) {
        LOG.log(Level.INFO, "Saving " + issues.size() + " new Issues for Top-Level-Item " + topLevelItem.getDisplayName() + ".");
        try {
            EntityManager em = persistenceService.getPerItemEntityManagerFactory(topLevelItem).createEntityManager();
            em.getTransaction().begin();
            for (Issue issue : issues) {
                if (issue.getCurrentState() == null) {
                    StateHistory stateNew = new StateHistory();
                    stateNew.setTimestamp(new Date());
                    stateNew.setState(State.NEW);
                    stateNew.setIssue(issue);
                    issue.setStateHistory(new HashSet<StateHistory>());
                    issue.getStateHistory().add(stateNew);
                }
                em.persist(issue);
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
