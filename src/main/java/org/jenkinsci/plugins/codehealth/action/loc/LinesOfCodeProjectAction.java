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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
    public List<List<Long>> getSeries(){
        Random random = new Random();
        List<Long> v1 = new ArrayList<Long>();
        v1.add(1L);
        v1.add(random.nextLong());
        List<Long> v2 = new ArrayList<Long>();
        v2.add(2L);
        v2.add(random.nextLong());
        List<Long> v3 = new ArrayList<Long>();
        v3.add(3L);
        v3.add(random.nextLong());
        List<List<Long>> data = new ArrayList<List<Long>>();
        data.add(v1);
        data.add(v2);
        data.add(v3);
        return data;
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
