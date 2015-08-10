package org.jenkinsci.plugins.codehealth.service;

import hudson.model.AbstractBuild;
import hudson.model.TopLevelItem;
import org.jenkinsci.plugins.codehealth.LinesOfCode;
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
}
