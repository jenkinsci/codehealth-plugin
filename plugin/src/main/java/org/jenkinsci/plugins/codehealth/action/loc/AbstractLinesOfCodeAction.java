package org.jenkinsci.plugins.codehealth.action.loc;

import hudson.model.TopLevelItem;
import org.jenkinsci.plugins.codehealth.action.AbstractCodehealthAction;
import org.jenkinsci.plugins.codehealth.provider.loc.LinesOfCodeProvider;
import org.jenkinsci.plugins.codehealth.service.LinesOfCodeRepository;

/**
 * Abstract Action for Lines of Code.
 * @author Michael Prankl
 */
public abstract class AbstractLinesOfCodeAction extends AbstractCodehealthAction {

    private transient LinesOfCodeRepository linesOfCodeRepository;
    private transient LinesOfCodeProvider linesOfCodeProvider;

    /**
     * Public constructor.
     * @param topLevelItem the top-level-item
     * @param linesOfCodeRepository the LoC Repository to use
     * @param linesOfCodeProvider the LoC Provider
     */
    public AbstractLinesOfCodeAction(TopLevelItem topLevelItem, LinesOfCodeRepository linesOfCodeRepository, LinesOfCodeProvider linesOfCodeProvider) {
        super(topLevelItem);
        this.linesOfCodeRepository = linesOfCodeRepository;
        this.linesOfCodeProvider = linesOfCodeProvider;
    }

    @Override
    public String getUrlName() {
        return "loc-api";
    }

    /**
     * @return the LoC repository
     */
    public LinesOfCodeRepository getLinesOfCodeRepository() {
        return linesOfCodeRepository;
    }

    /**
     * @param linesOfCodeRepository a LoC repository
     */
    public void setLinesOfCodeRepository(LinesOfCodeRepository linesOfCodeRepository) {
        this.linesOfCodeRepository = linesOfCodeRepository;
    }

    /**
     * @return the LoC provider
     */
    public LinesOfCodeProvider getLinesOfCodeProvider() {
        return linesOfCodeProvider;
    }

    /**
     * @param linesOfCodeProvider a LoC provider
     */
    public void setLinesOfCodeProvider(LinesOfCodeProvider linesOfCodeProvider) {
        this.linesOfCodeProvider = linesOfCodeProvider;
    }
}
