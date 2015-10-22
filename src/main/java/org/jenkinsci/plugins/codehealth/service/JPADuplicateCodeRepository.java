package org.jenkinsci.plugins.codehealth.service;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;
import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.TopLevelItem;
import org.jenkinsci.plugins.codehealth.model.Build;
import org.jenkinsci.plugins.codehealth.model.DuplicateCodeEntity;
import org.jenkinsci.plugins.codehealth.model.LatestBuilds;
import org.jenkinsci.plugins.codehealth.provider.duplicates.DuplicateCode;
import org.jenkinsci.plugins.database.jpa.PersistenceService;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
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

    @Inject
    private transient JPABuildRepository jpaBuildRepository;

    public JPADuplicateCodeRepository() {
    }

    @VisibleForTesting
    public JPADuplicateCodeRepository(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }


    @Override
    public void save(DuplicateCode duplicateCode, AbstractBuild<?, ?> build) {
        this.getInjector().injectMembers(this);
        TopLevelItem topLevelItem = (TopLevelItem) build.getProject();
        Build codehealthBuild = jpaBuildRepository.loadBuild(build.getNumber(), topLevelItem);
        DuplicateCodeEntity entity = map(duplicateCode, codehealthBuild);
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

    private DuplicateCodeEntity map(DuplicateCode duplicateCode, Build build) {
        DuplicateCodeEntity entity = new DuplicateCodeEntity();
        entity.setDuplicateFiles(duplicateCode.getFilesWithDuplicates());
        entity.setDuplicateLines(duplicateCode.getDuplicateLines());
        entity.setBuild(build);
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

    @Override
    public LatestBuilds getLatestBuildsWithDuplicates(TopLevelItem topLevelItem) {
        this.getInjector().injectMembers(this);
        Integer latestBuildNr;
        Integer prevToLatestBuildNr;
        try {
            EntityManager em = persistenceService.getPerItemEntityManagerFactory(topLevelItem).createEntityManager();
            List<Integer> resultListLatest = em.createNamedQuery(DuplicateCodeEntity.LATEST_BUILD_NR).getResultList();
            if (!resultListLatest.isEmpty()) {
                latestBuildNr = resultListLatest.get(0);
                if (latestBuildNr != null) {
                    List<Integer> resultListPrev = em.createNamedQuery(DuplicateCodeEntity.PREVIOUS_TO_LATEST_BUILD_NR).getResultList();
                    if (!resultListPrev.isEmpty()) {
                        prevToLatestBuildNr = resultListPrev.get(0);
                        if (prevToLatestBuildNr != null) {
                            LatestBuilds latestBuilds = new LatestBuilds(jpaBuildRepository.loadBuild(latestBuildNr, topLevelItem), jpaBuildRepository.loadBuild(prevToLatestBuildNr, topLevelItem));
                            return latestBuilds;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "Unable to query latest build with duplicate code.", e);
        } catch (IOException e) {
            LOG.log(Level.WARNING, "Unable to query latest build with duplicate code.", e);
        }
        return null;
    }

    @Override
    public List<DuplicateCodeEntity> getDuplicatesTrend(TopLevelItem topLevelItem) {
        try {
            EntityManager em = persistenceService.getPerItemEntityManagerFactory(topLevelItem).createEntityManager();
            return em.createNamedQuery(DuplicateCodeEntity.FIND_ALL).getResultList();
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "Unable to query duplicate code trend.", e);
        } catch (IOException e) {
            LOG.log(Level.WARNING, "Unable to query duplicate code trend.", e);
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
