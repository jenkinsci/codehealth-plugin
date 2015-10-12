package org.jenkinsci.plugins.codehealth.action.duplicates;

import hudson.model.TopLevelItem;
import org.jenkinsci.plugins.codehealth.action.AbstractCodehealthAction;
import org.jenkinsci.plugins.codehealth.service.DuplicateCodeRepository;

/**
 * @author Michael Prankl
 */
public abstract class AbstractDuplicateCodeAction extends AbstractCodehealthAction {

    private transient DuplicateCodeRepository duplicateCodeRepository;

    public AbstractDuplicateCodeAction(TopLevelItem topLevelItem, DuplicateCodeRepository duplicateCodeRepository) {
        super(topLevelItem);
        this.duplicateCodeRepository = duplicateCodeRepository;
    }

    @Override
    public String getUrlName() {
        return "duplicates-api";
    }

    public DuplicateCodeRepository getDuplicateCodeRepository() {
        return duplicateCodeRepository;
    }

    public void setDuplicateCodeRepository(DuplicateCodeRepository duplicateCodeRepository) {
        this.duplicateCodeRepository = duplicateCodeRepository;
    }


}
