package org.jenkinsci.plugins.codehealth.action.issues;

import org.jenkinsci.plugins.codehealth.model.LatestBuilds;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

/**
 * @author Michael Prankl
 */
@ExportedBean
public class IssuesTrend {

    @Exported(visibility = 1)
    public LatestBuilds getBuild() {
        return build;
    }

    public void setBuild(LatestBuilds build) {
        this.build = build;
    }

    @Exported(name = "delta")
    public int getIssueDelta() {
        return issueDelta;
    }

    public void setIssueDelta(int issueDelta) {
        this.issueDelta = issueDelta;
    }

    private LatestBuilds build;
    private int issueDelta;
}
