package org.jenkinsci.plugins.codehealth.action.loc;

import hudson.model.AbstractProject;
import hudson.model.TopLevelItem;
import org.jenkinsci.plugins.codehealth.provider.loc.LinesOfCode;
import org.jenkinsci.plugins.codehealth.model.LinesOfCodeEntity;
import org.jenkinsci.plugins.codehealth.service.LinesOfCodeRepository;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

/**
 * @author Michael Prankl
 */
@ExportedBean
public class LinesOfCodeProjectAction extends AbstractLinesOfCodeAction {

    private transient AbstractProject abstractProject;

    public LinesOfCodeProjectAction(AbstractProject abstractProject, LinesOfCodeRepository linesOfCodeRepository) {
        super((TopLevelItem) abstractProject, linesOfCodeRepository);
        this.abstractProject = abstractProject;
    }

    @Exported
    public LinesOfCodeEntity getLinesOfCode() {
        if (this.abstractProject.getLastBuild() != null) {
            return getLinesOfCodeRepository().read(this.getTopLevelItem(), this.abstractProject.getLastBuild().getNumber());
        } else {
            return null;
        }
    }

    @Exported
    public LinesOfCode getLinesOfCodeDelta() {
        if (this.abstractProject.getLastBuild() != null && this.abstractProject.getLastBuild().getPreviousBuild() != null) {
            return getLinesOfCodeRepository().readDelta(this.getTopLevelItem(), this.abstractProject.getLastBuild().getNumber(), this.abstractProject.getLastBuild().getPreviousBuild().getNumber());
        }
        return null;
    }


}
