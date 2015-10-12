package org.jenkinsci.plugins.codehealth.action.duplicates;

import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.TopLevelItem;
import org.jenkinsci.plugins.codehealth.model.DuplicateCodeEntity;
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

    @Exported
    public DuplicateCode duplicateCodeDelta() {
        AbstractBuild lastBuild = this.abstractProject.getLastBuild();
        AbstractBuild previousBuild = null;
        if (lastBuild != null) {
            previousBuild = lastBuild.getPreviousBuild();
        }
        if (lastBuild != null && previousBuild != null) {
            DuplicateCodeEntity toDup = getDuplicateCodeRepository().loadForBuild(getTopLevelItem(), lastBuild.getNumber());
            DuplicateCodeEntity fromDup = getDuplicateCodeRepository().loadForBuild(getTopLevelItem(), previousBuild.getNumber());
            return DuplicateCode.deltaOf(fromDup, toDup);
        }
        return null;
    }

}
