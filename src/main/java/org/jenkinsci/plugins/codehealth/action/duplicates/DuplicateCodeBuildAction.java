package org.jenkinsci.plugins.codehealth.action.duplicates;

import hudson.model.TopLevelItem;
import hudson.util.HttpResponses;
import org.jenkinsci.plugins.codehealth.action.ResultRedirect;
import org.jenkinsci.plugins.codehealth.model.DuplicateCodeEntity;
import org.jenkinsci.plugins.codehealth.provider.duplicates.DuplicateCodeProvider;
import org.jenkinsci.plugins.codehealth.service.DuplicateCodeRepository;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

/**
 * @author Michael Prankl
 */
@ExportedBean
public class DuplicateCodeBuildAction extends AbstractDuplicateCodeAction implements ResultRedirect {

    private int buildNr;

    public DuplicateCodeBuildAction(int buildNr, TopLevelItem topLevelItem, DuplicateCodeRepository duplicateCodeRepository, DuplicateCodeProvider duplicateCodeProvider) {
        super(topLevelItem, duplicateCodeRepository, duplicateCodeProvider);
        this.buildNr = buildNr;
    }

    @Exported
    public DuplicateCodeEntity duplicateCode() {
        return getDuplicateCodeRepository().loadForBuild(getTopLevelItem(), this.buildNr);
    }

    @Override
    public HttpResponse doGoToResult() {
        return HttpResponses.redirectTo("../" + getDuplicateCodeProvider().getBuildResultUrl());
    }

}
