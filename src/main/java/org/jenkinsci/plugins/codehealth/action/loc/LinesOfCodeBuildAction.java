package org.jenkinsci.plugins.codehealth.action.loc;

import hudson.model.TopLevelItem;
import org.jenkinsci.plugins.codehealth.LinesOfCode;
import org.jenkinsci.plugins.codehealth.model.LinesOfCodeEntity;
import org.jenkinsci.plugins.codehealth.service.LinesOfCodeRepository;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

/**
 * @author Michael Prankl
 */
@ExportedBean
public class LinesOfCodeBuildAction extends AbstractLinesOfCodeAction {

    private int buildNr;

    public LinesOfCodeBuildAction(int buildNr, TopLevelItem topLevelItem, LinesOfCodeRepository linesOfCodeRepository) {
        super(topLevelItem, linesOfCodeRepository);
        this.buildNr = buildNr;
    }

    @Exported
    public LinesOfCodeEntity getLinesOfCode() {
        return getLinesOfCodeRepository().read(getTopLevelItem(), this.buildNr);
    }

    @Exported
    public LinesOfCode getLinesOfCodeDelta() {
        return getLinesOfCodeRepository().readDelta(getTopLevelItem(), this.buildNr, this.buildNr - 1);
    }

    public int getBuildNr() {
        return buildNr;
    }

    public void setBuildNr(int buildNr) {
        this.buildNr = buildNr;
    }
}
