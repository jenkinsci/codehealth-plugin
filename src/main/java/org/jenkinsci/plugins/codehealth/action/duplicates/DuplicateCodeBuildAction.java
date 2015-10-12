package org.jenkinsci.plugins.codehealth.action.duplicates;

import hudson.model.TopLevelItem;
import org.jenkinsci.plugins.codehealth.model.DuplicateCodeEntity;
import org.jenkinsci.plugins.codehealth.service.DuplicateCodeRepository;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

/**
 * @author Michael Prankl
 */
@ExportedBean
public class DuplicateCodeBuildAction extends AbstractDuplicateCodeAction {

    private int buildNr;

    public DuplicateCodeBuildAction(int buildNr, TopLevelItem topLevelItem, DuplicateCodeRepository duplicateCodeRepository) {
        super(topLevelItem, duplicateCodeRepository);
        this.buildNr = buildNr;
    }

    @Exported
    public DuplicateCodeEntity duplicateCode() {
        return getDuplicateCodeRepository().loadForBuild(getTopLevelItem(), this.buildNr);
    }

}
