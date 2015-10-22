package org.jenkinsci.plugins.codehealth.service;

import hudson.model.AbstractBuild;
import hudson.model.TopLevelItem;
import org.jenkinsci.plugins.codehealth.model.Build;
import org.jenkinsci.plugins.codehealth.model.LatestBuilds;
import org.jenkinsci.plugins.codehealth.model.LinesOfCodeEntity;
import org.jenkinsci.plugins.codehealth.provider.loc.LinesOfCode;

import java.util.List;
import java.util.Map;

/**
 * @author Michael Prankl
 */
public abstract class LinesOfCodeRepository extends BaseRepository {

    /**
     * @return the LOC for the given build nr
     */
    public abstract LinesOfCodeEntity read(TopLevelItem topLevelItem, int buildNr);

    /**
     * @param loc   the LOC for the build
     * @param build the build
     */
    public abstract void save(LinesOfCode loc, AbstractBuild<?, ?> build);

    /**
     * @return the LOC delta between the given build numbers
     */
    public abstract LinesOfCode readDelta(TopLevelItem topLevelItem, Build toBuild, Build fromBuild);

    /**
     * @return the LatestBuilds which have LoC information
     */
    public abstract LatestBuilds getLatestBuildsWithLoC(TopLevelItem topLevelItem);

    /**
     * @return the line count to each build (key is build nr)
     */
    public abstract List<LinesOfCodeEntity> getLineTrend(TopLevelItem topLevelItem);
}
