package org.jenkinsci.plugins.codehealth.action.loc;

import hudson.model.Api;
import hudson.model.TopLevelItem;
import org.jenkinsci.plugins.codehealth.action.AbstractCodehealthAction;
import org.jenkinsci.plugins.codehealth.service.LinesOfCodeRepository;

/**
 * @author Michael Prankl
 */
public abstract class AbstractLinesOfCodeAction extends AbstractCodehealthAction {

    private transient LinesOfCodeRepository linesOfCodeRepository;

    public AbstractLinesOfCodeAction(TopLevelItem topLevelItem, LinesOfCodeRepository linesOfCodeRepository) {
        super(topLevelItem);
        this.linesOfCodeRepository = linesOfCodeRepository;
    }

    @Override
    public String getUrlName() {
        return "loc-api";
    }

    public LinesOfCodeRepository getLinesOfCodeRepository() {
        return linesOfCodeRepository;
    }

    public void setLinesOfCodeRepository(LinesOfCodeRepository linesOfCodeRepository) {
        this.linesOfCodeRepository = linesOfCodeRepository;
    }
}
