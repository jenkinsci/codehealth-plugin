package org.jenkinsci.plugins.codehealth.action.loc;

import hudson.model.Api;
import hudson.model.TopLevelItem;
import org.jenkinsci.plugins.codehealth.action.AbstractCodehealthAction;
import org.jenkinsci.plugins.codehealth.provider.loc.LinesOfCodeProvider;
import org.jenkinsci.plugins.codehealth.service.LinesOfCodeRepository;

/**
 * @author Michael Prankl
 */
public abstract class AbstractLinesOfCodeAction extends AbstractCodehealthAction {

    private transient LinesOfCodeRepository linesOfCodeRepository;
    private transient LinesOfCodeProvider linesOfCodeProvider;

    public AbstractLinesOfCodeAction(TopLevelItem topLevelItem, LinesOfCodeRepository linesOfCodeRepository, LinesOfCodeProvider linesOfCodeProvider) {
        super(topLevelItem);
        this.linesOfCodeRepository = linesOfCodeRepository;
        this.linesOfCodeProvider = linesOfCodeProvider;
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

    public LinesOfCodeProvider getLinesOfCodeProvider() {
        return linesOfCodeProvider;
    }

    public void setLinesOfCodeProvider(LinesOfCodeProvider linesOfCodeProvider) {
        this.linesOfCodeProvider = linesOfCodeProvider;
    }
}
