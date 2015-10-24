package org.jenkinsci.plugins.codehealth.service;

import com.google.inject.Inject;
import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.TopLevelItem;
import org.jenkinsci.plugins.codehealth.model.Build;
import org.jenkinsci.plugins.database.jpa.PersistenceService;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Michael Prankl
 */
@Extension
public class JPABuildRepository extends BuildRepository {

    private final static Logger LOG = Logger.getLogger(JPABuildRepository.class.getName());

    @Inject
    private transient PersistenceService persistenceService;

    @Override
    public Build save(AbstractBuild<?, ?> newBuild, TopLevelItem topLevelItem) {
        this.getInjector().injectMembers(this);
        Build newCodehealthBuild = map(newBuild);
        try {
            final EntityManager entityManager = persistenceService.getPerItemEntityManagerFactory(topLevelItem).createEntityManager();
            entityManager.getTransaction().begin();
            entityManager.persist(newCodehealthBuild);
            entityManager.getTransaction().commit();
            return newCodehealthBuild;
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "Unable to save build #" + newBuild.getNumber(), e);
        } catch (IOException e) {
            LOG.log(Level.WARNING, "Unable to save build #" + newBuild.getNumber(), e);
        }
        return null;
    }

    @Nullable
    @Override
    public Build loadBuild(int buildNr, TopLevelItem topLevelItem) {
        this.getInjector().injectMembers(this);
        try {
            final EntityManager entityManager = persistenceService.getPerItemEntityManagerFactory(topLevelItem).createEntityManager();
            List<Build> builds = entityManager.createNamedQuery(Build.FIND_BY_NUMBER).setParameter("buildNr", buildNr).getResultList();
            if (!builds.isEmpty()) {
                return builds.get(0);
            } else {
                return null;
            }
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "Unable to load build #" + buildNr, e);
        } catch (IOException e) {
            LOG.log(Level.WARNING, "Unable to load build #" + buildNr, e);
        }
        return null;
    }

    @Nonnull
    @Override
    public List<Build> findAllBuilds(TopLevelItem topLevelItem) {
        this.getInjector().injectMembers(this);
        try {
            final EntityManager entityManager = persistenceService.getPerItemEntityManagerFactory(topLevelItem).createEntityManager();
            return entityManager.createNamedQuery(Build.FIND_ALL).getResultList();
        } catch (SQLException | IOException e) {
            LOG.log(Level.WARNING, "Unable to load all builds.", e);
        }
        return Collections.emptyList();
    }

    private Build map(AbstractBuild<?, ?> newBuild) {
        final Build codehealthBuild = new Build();
        codehealthBuild.setNumber(newBuild.getNumber());
        codehealthBuild.setTimestamp(newBuild.getTime());
        return codehealthBuild;
    }
}
