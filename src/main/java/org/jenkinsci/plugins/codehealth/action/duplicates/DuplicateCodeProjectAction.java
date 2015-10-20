package org.jenkinsci.plugins.codehealth.action.duplicates;

import hudson.model.AbstractProject;
import hudson.model.TopLevelItem;
import hudson.util.HttpResponses;
import org.jenkinsci.plugins.codehealth.action.ResultUrlNameProvider;
import org.jenkinsci.plugins.codehealth.model.DuplicateCodeEntity;
import org.jenkinsci.plugins.codehealth.model.LatestBuilds;
import org.jenkinsci.plugins.codehealth.provider.duplicates.DuplicateCode;
import org.jenkinsci.plugins.codehealth.provider.duplicates.DuplicateCodeProvider;
import org.jenkinsci.plugins.codehealth.service.DuplicateCodeRepository;
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
public class DuplicateCodeProjectAction extends AbstractDuplicateCodeAction implements ResultUrlNameProvider {

    private transient AbstractProject abstractProject;

    public DuplicateCodeProjectAction(AbstractProject abstractProject, DuplicateCodeRepository duplicateCodeRepository, DuplicateCodeProvider duplicateCodeProvider) {
        super((TopLevelItem) abstractProject, duplicateCodeRepository, duplicateCodeProvider);
        this.abstractProject = abstractProject;
    }

    @Exported
    public DuplicateCodeEntity getDuplicateCode() {
        return getDuplicateCodeRepository().loadLatest(this.abstractProject);
    }

    @Exported(name = "trend")
    public DuplicateCodeTrend duplicateCodeTrend() {
        LatestBuilds latestBuildsWithDuplicates = getDuplicateCodeRepository().getLatestBuildsWithDuplicates(getTopLevelItem());
        if (latestBuildsWithDuplicates != null) {
            DuplicateCodeEntity latestDup = getDuplicateCodeRepository().loadForBuild(getTopLevelItem(), latestBuildsWithDuplicates.getLatestBuild().getNumber());
            DuplicateCodeEntity prevDup = getDuplicateCodeRepository().loadForBuild(getTopLevelItem(), latestBuildsWithDuplicates.getPreviousToLatestBuild().getNumber());
            DuplicateCode dupTrend = DuplicateCode.deltaOf(prevDup, latestDup);
            return makeTrend(latestBuildsWithDuplicates, dupTrend);
        }
        return null;
    }

    @Exported
    public List<List<Long>> getSeries(){
        Random random = new Random();
        List<Long> v1 = new ArrayList<Long>();
        v1.add(1L);
        v1.add((long) random.nextInt());
        List<Long> v2 = new ArrayList<Long>();
        v2.add(2L);
        v2.add((long) random.nextInt());
        List<Long> v3 = new ArrayList<Long>();
        v3.add(3L);
        v3.add((long) random.nextInt());
        List<List<Long>> data = new ArrayList<List<Long>>();
        //data.add(v1);
        data.add(v2);
        data.add(v3);
        return data;
    }

    private DuplicateCodeTrend makeTrend(LatestBuilds builds, DuplicateCode trend) {
        DuplicateCodeTrend duplicateCodeTrend = new DuplicateCodeTrend();
        duplicateCodeTrend.setBuilds(builds);
        duplicateCodeTrend.setDuplicateCode(trend);
        return duplicateCodeTrend;
    }

    @Override
    public HttpResponse doGoToResult() {
        return HttpResponses.redirectTo("../" + getDuplicateCodeProvider().getProjectResultUrl());
    }

    @Override
    @Exported
    public String getResultUrlName() {
        return getDuplicateCodeProvider().getProjectResultUrl();
    }

}
