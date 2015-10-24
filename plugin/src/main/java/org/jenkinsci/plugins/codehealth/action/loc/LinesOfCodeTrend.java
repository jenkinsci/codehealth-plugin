package org.jenkinsci.plugins.codehealth.action.loc;

import org.jenkinsci.plugins.codehealth.model.LatestBuilds;
import org.jenkinsci.plugins.codehealth.provider.loc.LinesOfCode;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

/**
 * @author Michael Prankl
 */
@ExportedBean
public class LinesOfCodeTrend {

    private LatestBuilds builds;
    private LinesOfCode loc;

    @Exported(visibility = 1)
    public LatestBuilds getBuilds() {
        return builds;
    }

    public void setBuilds(LatestBuilds builds) {
        this.builds = builds;
    }

    @Exported(visibility = 1)
    public LinesOfCode getLoc() {
        return loc;
    }

    public void setLoc(LinesOfCode loc) {
        this.loc = loc;
    }
}
