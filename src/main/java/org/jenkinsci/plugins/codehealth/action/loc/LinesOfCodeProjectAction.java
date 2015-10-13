package org.jenkinsci.plugins.codehealth.action.loc;

import hudson.model.AbstractProject;
import hudson.model.TopLevelItem;
import hudson.util.HttpResponses;
import org.jenkinsci.plugins.codehealth.action.ResultRedirect;
import org.jenkinsci.plugins.codehealth.model.LatestBuilds;
import org.jenkinsci.plugins.codehealth.model.LinesOfCodeEntity;
import org.jenkinsci.plugins.codehealth.provider.loc.LinesOfCode;
import org.jenkinsci.plugins.codehealth.provider.loc.LinesOfCodeProvider;
import org.jenkinsci.plugins.codehealth.service.LinesOfCodeRepository;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

/**
 * @author Michael Prankl
 */
@ExportedBean
public class LinesOfCodeProjectAction extends AbstractLinesOfCodeAction implements ResultRedirect {

    private transient AbstractProject abstractProject;

    public LinesOfCodeProjectAction(AbstractProject abstractProject, LinesOfCodeRepository linesOfCodeRepository, LinesOfCodeProvider linesOfCodeProvider) {
        super((TopLevelItem) abstractProject, linesOfCodeRepository, linesOfCodeProvider);
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

    @Override
    public HttpResponse doGoToResult() {
        return HttpResponses.redirectTo("../" + getLinesOfCodeProvider().getProjectResultUrl());
    }
}
