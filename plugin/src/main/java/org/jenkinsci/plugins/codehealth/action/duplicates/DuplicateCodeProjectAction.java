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

import java.util.List;

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
            DuplicateCode dupTrend = DuplicateCodeEntity.deltaOf(prevDup, latestDup);
            return makeTrend(latestBuildsWithDuplicates, dupTrend);
        }
        return null;
    }

    @Exported
    public List<DuplicateCodeEntity> getSeries() {
        return getDuplicateCodeRepository().getDuplicatesTrend(this.getTopLevelItem());
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
