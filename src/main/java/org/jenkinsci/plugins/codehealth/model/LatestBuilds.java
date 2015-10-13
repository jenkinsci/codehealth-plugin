package org.jenkinsci.plugins.codehealth.model;

import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

/**
 * @author Michael Prankl
 */
@ExportedBean
public class LatestBuilds {

    private Integer latestBuild;
    private Integer previousToLatestBuild;

    public LatestBuilds(Integer latestBuild, Integer previousToLatestBuild) {
        this.latestBuild = latestBuild;
        this.previousToLatestBuild = previousToLatestBuild;
    }

    @Exported
    public Integer getLatestBuild() {
        return latestBuild;
    }

    public void setLatestBuild(Integer latestBuild) {
        this.latestBuild = latestBuild;
    }

    @Exported(name = "previousBuild")
    public Integer getPreviousToLatestBuild() {
        return previousToLatestBuild;
    }

    public void setPreviousToLatestBuild(Integer previousToLatestBuild) {
        this.previousToLatestBuild = previousToLatestBuild;
    }
}
