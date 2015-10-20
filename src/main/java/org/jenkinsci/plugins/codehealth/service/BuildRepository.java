package org.jenkinsci.plugins.codehealth.service;

import hudson.model.AbstractBuild;
import hudson.model.TopLevelItem;
import org.jenkinsci.plugins.codehealth.model.Build;

import javax.annotation.Nullable;

/**
 * @author Michael Prankl
 */
public abstract class BuildRepository extends BaseRepository {

    /**
     * Persist the given Build.
     *
     * @param newBuild     the build
     * @param topLevelItem the top-level-item
     * @return the persisted Build
     */
    @Nullable
    public abstract Build save(AbstractBuild<?, ?> newBuild, TopLevelItem topLevelItem);

    /**
     * @param buildNr the build nr
     * @return the Build with this number or null
     */
    @Nullable
    public abstract Build loadBuild(int buildNr, TopLevelItem topLevelItem);
}
