package org.jenkinsci.plugins.codehealth.action.loc;

import hudson.model.AbstractProject;
import hudson.model.TopLevelItem;
import org.jenkinsci.plugins.codehealth.model.LatestBuilds;
import org.jenkinsci.plugins.codehealth.model.LinesOfCodeEntity;
import org.jenkinsci.plugins.codehealth.provider.loc.LinesOfCode;
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

    @Exported(name = "trend")
    public LinesOfCodeTrend getLinesOfCodeTrend() {
        LatestBuilds latestBuildsWithLoC = getLinesOfCodeRepository().getLatestBuildsWithLoC(getTopLevelItem());
        if (latestBuildsWithLoC != null) {
            LinesOfCode linesOfCodeDelta = getLinesOfCodeRepository().readDelta(this.getTopLevelItem(), latestBuildsWithLoC.getLatestBuild(), latestBuildsWithLoC.getPreviousToLatestBuild());
            LinesOfCodeTrend trend = new LinesOfCodeTrend();
            trend.setBuilds(latestBuildsWithLoC);
            trend.setLoc(linesOfCodeDelta);
            return trend;
        }
        return null;
    }
}
