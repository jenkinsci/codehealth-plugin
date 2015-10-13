package org.jenkinsci.plugins.codehealth.action.duplicates;

import org.jenkinsci.plugins.codehealth.model.LatestBuilds;
import org.jenkinsci.plugins.codehealth.provider.duplicates.DuplicateCode;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

/**
 * @author Michael Prankl
 */
@ExportedBean
public class DuplicateCodeTrend {

    private LatestBuilds builds;
    private DuplicateCode duplicateCode;

    @Exported(visibility = 1)
    public LatestBuilds getBuilds() {
        return builds;
    }

    public void setBuilds(LatestBuilds builds) {
        this.builds = builds;
    }

    @Exported(visibility = 1)
    public DuplicateCode getDuplicateCode() {
        return duplicateCode;
    }

    public void setDuplicateCode(DuplicateCode duplicateCode) {
        this.duplicateCode = duplicateCode;
    }
}
