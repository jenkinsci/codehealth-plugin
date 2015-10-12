package org.jenkinsci.plugins.codehealth.service;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;
import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.TopLevelItem;
import org.jenkinsci.plugins.codehealth.provider.duplicates.DuplicateCode;
import org.jenkinsci.plugins.codehealth.model.DuplicateCodeEntity;
import org.jenkinsci.plugins.database.jpa.PersistenceService;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.io.IOException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Michael Prankl
 */
@Extension
public class JPADuplicateCodeRepository extends DuplicateCodeRepository {

    private final static Logger LOG = Logger.getLogger(JPADuplicateCodeRepository.class.getName());

    @Inject
    private transient PersistenceService persistenceService;

    public JPADuplicateCodeRepository() {
    }

    @VisibleForTesting
    public JPADuplicateCodeRepository(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }


    @Override
    public void save(DuplicateCode duplicateCode, AbstractBuild<?, ?> build) {
        this.getInjector().injectMembers(this);
        DuplicateCodeEntity entity = map(duplicateCode, build);
        TopLevelItem topLevelItem = (TopLevelItem) build.getProject();
        LOG.log(Level.INFO, "Persisting duplicate code information...");
        try {
            EntityManager em = persistenceService.getPerItemEntityManagerFactory(topLevelItem).createEntityManager();
            em.getTransaction().begin();
            em.persist(entity);
            em.getTransaction().commit();
            em.close();
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "Unable to save duplicate code.", e);
        } catch (IOException e) {
            LOG.log(Level.WARNING, "Unable to save duplicate code.", e);
        }
    }

    private DuplicateCodeEntity map(DuplicateCode duplicateCode, AbstractBuild<?, ?> build) {
        DuplicateCodeEntity entity = new DuplicateCodeEntity();
        entity.setDuplicateFiles(duplicateCode.getNumberOfDuplicateFiles());
        entity.setDuplicateLines(duplicateCode.getNumberOfDuplicateLines());
        entity.setBuildNr(build.getNumber());
        return entity;
    }

    @Override
    public DuplicateCodeEntity loadForBuild(TopLevelItem topLevelItem, int buildNr) {
        this.getInjector().injectMembers(this);
        return queryForBuildNr(this.persistenceService, topLevelItem, buildNr);
    }

    @Override
    public DuplicateCodeEntity loadLatest(AbstractProject<?, ?> project) {
        this.getInjector().injectMembers(this);
        Integer lastBuildNr = null;
        TopLevelItem topLevelItem = (TopLevelItem) project;
        if (project.getLastBuild() != null) {
            lastBuildNr = project.getLastBuild().getNumber();
        }
        if (lastBuildNr != null) {
            return queryForBuildNr(this.persistenceService, topLevelItem, lastBuildNr);
        }
        return null;
    }

    private DuplicateCodeEntity queryForBuildNr(PersistenceService persistenceService, TopLevelItem topLevelItem, int buildNr) {
        try {
            EntityManager em = persistenceService.getPerItemEntityManagerFactory(topLevelItem).createEntityManager();
            Query query = em.createNamedQuery(DuplicateCodeEntity.FIND_BY_BUILD_NR);
            query.setParameter("buildNr", buildNr);
            try {
                return (DuplicateCodeEntity) query.getSingleResult();
            } catch (NoResultException e) {
            }
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "Unable to query duplicate code.", e);
        } catch (IOException e) {
            LOG.log(Level.WARNING, "Unable to query duplicate code.", e);
        }
        return null;
    }
}
