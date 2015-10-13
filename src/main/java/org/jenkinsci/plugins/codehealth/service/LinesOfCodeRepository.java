package org.jenkinsci.plugins.codehealth.service;

import hudson.model.AbstractBuild;
import hudson.model.TopLevelItem;
import org.jenkinsci.plugins.codehealth.model.LatestBuilds;
import org.jenkinsci.plugins.codehealth.provider.loc.LinesOfCode;
import org.jenkinsci.plugins.codehealth.model.LinesOfCodeEntity;

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
    public abstract LinesOfCode readDelta(TopLevelItem topLevelItem, int toBuildNr, int fromBuildNr);

    /**
     * @return the LatestBuilds which have LoC information
     */
    public abstract LatestBuilds getLatestBuildsWithLoC(TopLevelItem topLevelItem);
}
