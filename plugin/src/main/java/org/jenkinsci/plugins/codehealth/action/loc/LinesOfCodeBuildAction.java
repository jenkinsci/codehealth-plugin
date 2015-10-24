package org.jenkinsci.plugins.codehealth.action.loc;

import hudson.model.TopLevelItem;
import hudson.util.HttpResponses;
import org.jenkinsci.plugins.codehealth.action.ResultUrlNameProvider;
import org.jenkinsci.plugins.codehealth.model.LinesOfCodeEntity;
import org.jenkinsci.plugins.codehealth.provider.loc.LinesOfCodeProvider;
import org.jenkinsci.plugins.codehealth.service.LinesOfCodeRepository;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

/**
 * @author Michael Prankl
 */
@ExportedBean
public class LinesOfCodeBuildAction extends AbstractLinesOfCodeAction implements ResultUrlNameProvider {

    private int buildNr;

    public LinesOfCodeBuildAction(int buildNr, TopLevelItem topLevelItem, LinesOfCodeRepository linesOfCodeRepository, LinesOfCodeProvider linesOfCodeProvider) {
        super(topLevelItem, linesOfCodeRepository, linesOfCodeProvider);
        this.buildNr = buildNr;
    }

    @Exported
    public LinesOfCodeEntity getLinesOfCode() {
        return getLinesOfCodeRepository().read(getTopLevelItem(), this.buildNr);
    }

    public int getBuildNr() {
        return buildNr;
    }

    public void setBuildNr(int buildNr) {
        this.buildNr = buildNr;
    }

    @Override
    public HttpResponse doGoToResult() {
        return HttpResponses.redirectTo("../" + getLinesOfCodeProvider().getBuildResultUrl());
    }

    @Override
    @Exported
    public String getResultUrlName() {
        return getLinesOfCodeProvider().getBuildResultUrl();
    }
}
