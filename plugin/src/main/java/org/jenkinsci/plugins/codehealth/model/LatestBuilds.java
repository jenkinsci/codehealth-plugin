package org.jenkinsci.plugins.codehealth.model;

import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

/**
 * @author Michael Prankl
 */
@ExportedBean
public class LatestBuilds {

    private Build latestBuild;
    private Build previousToLatestBuild;

    public LatestBuilds(Build latestBuild, Build previousToLatestBuild) {
        this.latestBuild = latestBuild;
        this.previousToLatestBuild = previousToLatestBuild;
    }

    @Exported
    public Build getLatestBuild() {
        return latestBuild;
    }

    public void setLatestBuild(Build latestBuild) {
        this.latestBuild = latestBuild;
    }

    @Exported(name = "previousBuild")
    public Build getPreviousToLatestBuild() {
        return previousToLatestBuild;
    }

    public void setPreviousToLatestBuild(Build previousToLatestBuild) {
        this.previousToLatestBuild = previousToLatestBuild;
    }
}
