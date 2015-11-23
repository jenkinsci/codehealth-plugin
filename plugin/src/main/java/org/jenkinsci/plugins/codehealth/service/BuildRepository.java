package org.jenkinsci.plugins.codehealth.service;

import hudson.model.Run;
import hudson.model.TopLevelItem;
import org.jenkinsci.plugins.codehealth.model.Build;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * @author Michael Prankl
 */
public abstract class BuildRepository extends BaseRepository {

    /**
     * Persist the given Build.
     *
     * @param run     the build
     * @param topLevelItem the top-level-item
     * @return the persisted Build
     */
    @Nullable
    public abstract Build save(Run<?, ?> run, TopLevelItem topLevelItem);

    /**
     * @param buildNr the build nr
     * @return the Build with this number or null
     */
    @Nullable
    public abstract Build loadBuild(int buildNr, TopLevelItem topLevelItem);

    /**
     * @param topLevelItem the top-level-item
     * @return all persisted Builds
     */
    @Nonnull
    public abstract List<Build> findAllBuilds(TopLevelItem topLevelItem);
}
