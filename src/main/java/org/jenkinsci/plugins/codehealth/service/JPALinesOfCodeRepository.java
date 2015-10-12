package org.jenkinsci.plugins.codehealth.service;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;
import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.TopLevelItem;
import org.jenkinsci.plugins.codehealth.LinesOfCode;
import org.jenkinsci.plugins.codehealth.model.LinesOfCodeEntity;
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
public class JPALinesOfCodeRepository extends LinesOfCodeRepository {

    private final static Logger LOG = Logger.getLogger(JPALinesOfCodeRepository.class.getName());

    @Inject
    private transient PersistenceService persistenceService;

    public JPALinesOfCodeRepository() {
    }

    @VisibleForTesting
    public JPALinesOfCodeRepository(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    @Override
    public LinesOfCodeEntity read(TopLevelItem topLevelItem, int buildNr) {
        this.getInjector().injectMembers(this);
        try {
            EntityManager entityManager = this.persistenceService.getPerItemEntityManagerFactory(topLevelItem).createEntityManager();
            Query query = entityManager.createNamedQuery(LinesOfCodeEntity.FIND_BY_BUILD_NR);
            query.setParameter("buildNr", buildNr);
            try {
                LinesOfCodeEntity result = (LinesOfCodeEntity) query.getSingleResult();
                return result;
            } catch (NoResultException e) {
            }
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "Unable to query lines of code.", e);
        } catch (IOException e) {
            LOG.log(Level.WARNING, "Unable to query lines of code.", e);
        }
        return null;
    }

    @Override
    public void save(LinesOfCode loc, AbstractBuild<?, ?> build) {
        this.getInjector().injectMembers(this);
        final TopLevelItem topLevelItem = (TopLevelItem) build.getProject();
        final int buildNr = build.getNumber();
        try {
            EntityManager entityManager = this.persistenceService.getPerItemEntityManagerFactory(topLevelItem).createEntityManager();
            entityManager.getTransaction().begin();
            LinesOfCodeEntity locEntity = map(loc, buildNr);
            entityManager.persist(locEntity);
            entityManager.getTransaction().commit();
            entityManager.close();
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "Unable to save lines of code.", e);
        } catch (IOException e) {
            LOG.log(Level.WARNING, "Unable to save lines of code.", e);
        }
    }

    @Override
    public LinesOfCode readDelta(TopLevelItem topLevelItem, int toBuildNr, int fromBuildNr) {
        final LinesOfCodeEntity baseLocs = this.read(topLevelItem, fromBuildNr);
        final LinesOfCodeEntity targetLocs = this.read(topLevelItem, toBuildNr);
        if (baseLocs != null && targetLocs != null) {
            return new LinesOfCode(targetLocs.getLinesOfCode() - baseLocs.getLinesOfCode(), targetLocs.getFiles() - baseLocs.getLinesOfCode());
        } else {
            return null;
        }
    }

    private LinesOfCodeEntity map(LinesOfCode loc, int buildNr) {
        LinesOfCodeEntity locEntity = new LinesOfCodeEntity();
        locEntity.setBuildNr(buildNr);
        locEntity.setFiles(loc.getFileCount());
        locEntity.setLinesOfCode(loc.getLinesOfCode());
        return locEntity;
    }
}
