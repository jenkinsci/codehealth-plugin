package org.jenkinsci.plugins.codehealth.action.loc;

import hudson.model.AbstractProject;
import hudson.model.Api;
import hudson.model.TopLevelItem;
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


}
