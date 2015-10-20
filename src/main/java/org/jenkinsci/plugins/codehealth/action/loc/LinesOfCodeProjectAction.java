package org.jenkinsci.plugins.codehealth.action.loc;

import hudson.model.AbstractProject;
import hudson.model.TopLevelItem;
import hudson.util.HttpResponses;
import net.sf.json.JSONArray;
import org.jenkinsci.plugins.codehealth.action.ResultUrlNameProvider;
import org.jenkinsci.plugins.codehealth.model.LatestBuilds;
import org.jenkinsci.plugins.codehealth.model.LinesOfCodeEntity;
import org.jenkinsci.plugins.codehealth.provider.loc.LinesOfCode;
import org.jenkinsci.plugins.codehealth.provider.loc.LinesOfCodeProvider;
import org.jenkinsci.plugins.codehealth.service.LinesOfCodeRepository;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import java.util.*;

/**
 * @author Michael Prankl
 */
@ExportedBean
public class LinesOfCodeProjectAction extends AbstractLinesOfCodeAction implements ResultUrlNameProvider {

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

    @Exported
    public Map<Integer, LinesOfCodeEntity> getSeries() {
        return getLinesOfCodeRepository().getLineTrend(this.getTopLevelItem());
    }

    @Override
    public HttpResponse doGoToResult() {
        return HttpResponses.redirectTo("../" + getLinesOfCodeProvider().getProjectResultUrl());
    }

    @Override
    @Exported
    public String getResultUrlName() {
        return getLinesOfCodeProvider().getProjectResultUrl();
    }
}
