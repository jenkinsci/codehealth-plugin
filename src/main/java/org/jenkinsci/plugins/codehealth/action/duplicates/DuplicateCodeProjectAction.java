package org.jenkinsci.plugins.codehealth.action.duplicates;

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.TopLevelItem;
import org.jenkinsci.plugins.codehealth.model.DuplicateCodeEntity;
import org.jenkinsci.plugins.codehealth.model.LatestBuilds;
import org.jenkinsci.plugins.codehealth.provider.duplicates.DuplicateCode;
import org.jenkinsci.plugins.codehealth.service.DuplicateCodeRepository;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

/**
 * @author Michael Prankl
 */
@ExportedBean
public class DuplicateCodeProjectAction extends AbstractDuplicateCodeAction {

    private transient AbstractProject abstractProject;

    public DuplicateCodeProjectAction(AbstractProject abstractProject, DuplicateCodeRepository duplicateCodeRepository) {
        super((TopLevelItem) abstractProject, duplicateCodeRepository);
        this.abstractProject = abstractProject;
    }

    @Exported
    public DuplicateCodeEntity getDuplicateCode() {
        return getDuplicateCodeRepository().loadLatest(this.abstractProject);
    }

    @Exported(name = "trend")
    public DuplicateCode duplicateCodeTrend() {
        LatestBuilds latestBuildsWithDuplicates = getDuplicateCodeRepository().getLatestBuildsWithDuplicates(getTopLevelItem());
        if (latestBuildsWithDuplicates != null) {
            DuplicateCodeEntity latestDup = getDuplicateCodeRepository().loadForBuild(getTopLevelItem(), latestBuildsWithDuplicates.getLatestBuild());
            DuplicateCodeEntity prevDup = getDuplicateCodeRepository().loadForBuild(getTopLevelItem(), latestBuildsWithDuplicates.getPreviousToLatestBuild());
            return DuplicateCode.deltaOf(prevDup, latestDup);
        }
        return null;
    }

}
