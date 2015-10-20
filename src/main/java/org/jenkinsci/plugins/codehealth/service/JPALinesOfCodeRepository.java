package org.jenkinsci.plugins.codehealth.service;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.TopLevelItem;
import org.jenkinsci.plugins.codehealth.model.Build;
import org.jenkinsci.plugins.codehealth.model.LatestBuilds;
import org.jenkinsci.plugins.codehealth.model.LinesOfCodeEntity;
import org.jenkinsci.plugins.codehealth.provider.loc.LinesOfCode;
import org.jenkinsci.plugins.database.jpa.PersistenceService;

import javax.annotation.Nullable;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
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

    @Inject
    private transient JPABuildRepository jpaBuildRepository;

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
        Build codehealthBuild = jpaBuildRepository.loadBuild(buildNr, topLevelItem);
        try {
            EntityManager entityManager = this.persistenceService.getPerItemEntityManagerFactory(topLevelItem).createEntityManager();
            entityManager.getTransaction().begin();
            LinesOfCodeEntity locEntity = map(loc, codehealthBuild);
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
    public LinesOfCode readDelta(TopLevelItem topLevelItem, Build toBuild, Build fromBuild) {
        final LinesOfCodeEntity baseLocs = this.read(topLevelItem, fromBuild.getNumber());
        final LinesOfCodeEntity targetLocs = this.read(topLevelItem, toBuild.getNumber());
        if (baseLocs != null && targetLocs != null) {
            return new LinesOfCode(targetLocs.getLinesOfCode() - baseLocs.getLinesOfCode(), targetLocs.getFiles() - baseLocs.getFiles());
        } else {
            return null;
        }
    }

    @Override
    public LatestBuilds getLatestBuildsWithLoC(TopLevelItem topLevelItem) {
        this.getInjector().injectMembers(this);
        Integer latestBuildNr = null;
        Integer prevToLatestBuildNr = null;
        try {
            EntityManager entityManager = persistenceService.getPerItemEntityManagerFactory(topLevelItem).createEntityManager();
            List<Integer> resultListLatest = entityManager.createNamedQuery(LinesOfCodeEntity.LATEST_BUILD_NR).getResultList();
            if (!resultListLatest.isEmpty()) {
                latestBuildNr = resultListLatest.get(0);
                if (latestBuildNr != null) {
                    List<Integer> resultListPrev = entityManager.createNamedQuery(LinesOfCodeEntity.PREVIOUS_TO_LATEST_BUILD_NR).getResultList();
                    if (!resultListPrev.isEmpty()) {
                        prevToLatestBuildNr = resultListPrev.get(0);
                        if (prevToLatestBuildNr != null) {
                            return new LatestBuilds(jpaBuildRepository.loadBuild(latestBuildNr, topLevelItem),
                                    jpaBuildRepository.loadBuild(prevToLatestBuildNr, topLevelItem));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "Unable to retrieve latest builds with lines of code.", e);
        } catch (IOException e) {
            LOG.log(Level.WARNING, "Unable to retrieve latest builds with lines of code.", e);
        }
        return null;
    }

    @Override
    public Map<Integer, LinesOfCodeEntity> getLineTrend(TopLevelItem topLevelItem) {
        this.getInjector().injectMembers(this);
        try {
            EntityManager entityManager = this.persistenceService.getPerItemEntityManagerFactory(topLevelItem).createEntityManager();
            List<LinesOfCodeEntity> resultList = entityManager.createNamedQuery(LinesOfCodeEntity.FIND_ALL).getResultList();
            return Maps.uniqueIndex(resultList, new Function<LinesOfCodeEntity, Integer>() {
                @Override
                public Integer apply(@Nullable LinesOfCodeEntity linesOfCodeEntity) {
                    return linesOfCodeEntity.getBuild().getNumber();
                }
            });
        } catch (SQLException e) {
            LOG.log(Level.WARNING, "Unable to save lines of code.", e);
        } catch (IOException e) {
            LOG.log(Level.WARNING, "Unable to save lines of code.", e);
        }
        return null;
    }

    private LinesOfCodeEntity map(LinesOfCode loc, Build build) {
        LinesOfCodeEntity locEntity = new LinesOfCodeEntity();
        locEntity.setBuild(build);
        locEntity.setFiles(loc.getFileCount());
        locEntity.setLinesOfCode(loc.getLinesOfCode());
        return locEntity;
    }
}
