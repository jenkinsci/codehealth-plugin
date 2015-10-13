package org.jenkinsci.plugins.codehealth.action.duplicates;

import hudson.model.TopLevelItem;
import hudson.util.HttpResponses;
import org.jenkinsci.plugins.codehealth.action.ResultUrlNameProvider;
import org.jenkinsci.plugins.codehealth.model.DuplicateCodeEntity;
import org.jenkinsci.plugins.codehealth.provider.duplicates.DuplicateCodeProvider;
import org.jenkinsci.plugins.codehealth.service.DuplicateCodeRepository;
import org.kohsuke.stapler.HttpResponse;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Michael Prankl
 */
@ExportedBean
public class DuplicateCodeBuildAction extends AbstractDuplicateCodeAction implements ResultUrlNameProvider {

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

    @Override
    @Exported
    public String getResultUrlName() {
        return getDuplicateCodeProvider().getBuildResultUrl();
    }

    public void doResultUrlName(StaplerRequest request, StaplerResponse response) throws IOException {
        OutputStream os = response.getCompressedOutputStream(request);
        response.setContentType("text/plain;charset=UTF-8");
        try {
            os.write(getDuplicateCodeProvider().getBuildResultUrl().getBytes("UTF-8"));
        } finally {
            os.close();
        }
    }

}
