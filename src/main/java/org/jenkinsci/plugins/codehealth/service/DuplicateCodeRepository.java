package org.jenkinsci.plugins.codehealth.service;

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.TopLevelItem;
import org.jenkinsci.plugins.codehealth.model.DuplicateCodeEntity;
import org.jenkinsci.plugins.codehealth.model.LatestBuilds;
import org.jenkinsci.plugins.codehealth.provider.duplicates.DuplicateCode;

import java.util.List;
import java.util.Map;

/**
 * @author Michael Prankl
 */
public abstract class DuplicateCodeRepository extends BaseRepository {

    /**
     * Save the duplicate code to the repository.
     *
     * @param duplicateCode the duplicate Code
     * @param build         the corresponding build
     */
    public abstract void save(DuplicateCode duplicateCode, AbstractBuild<?, ?> build);

    public abstract DuplicateCodeEntity loadForBuild(TopLevelItem topLevelItem, int buildNr);

    public abstract DuplicateCodeEntity loadLatest(AbstractProject<?, ?> project);

    public abstract LatestBuilds getLatestBuildsWithDuplicates(TopLevelItem topLevelItem);

    public abstract List<DuplicateCodeEntity> getDuplicatesTrend(TopLevelItem topLevelItem);
}
