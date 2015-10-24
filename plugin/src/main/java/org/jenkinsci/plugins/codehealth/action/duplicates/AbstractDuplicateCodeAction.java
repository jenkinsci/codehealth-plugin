package org.jenkinsci.plugins.codehealth.action.duplicates;

import hudson.model.TopLevelItem;
import org.jenkinsci.plugins.codehealth.action.AbstractCodehealthAction;
import org.jenkinsci.plugins.codehealth.provider.duplicates.DuplicateCodeProvider;
import org.jenkinsci.plugins.codehealth.service.DuplicateCodeRepository;

/**
 * @author Michael Prankl
 */
public abstract class AbstractDuplicateCodeAction extends AbstractCodehealthAction {

    private transient DuplicateCodeRepository duplicateCodeRepository;
    private transient DuplicateCodeProvider duplicateCodeProvider;

    public AbstractDuplicateCodeAction(TopLevelItem topLevelItem, DuplicateCodeRepository duplicateCodeRepository, DuplicateCodeProvider duplicateCodeProvider) {
        super(topLevelItem);
        this.duplicateCodeRepository = duplicateCodeRepository;
        this.duplicateCodeProvider = duplicateCodeProvider;
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


    public DuplicateCodeProvider getDuplicateCodeProvider() {
        return duplicateCodeProvider;
    }

    public void setDuplicateCodeProvider(DuplicateCodeProvider duplicateCodeProvider) {
        this.duplicateCodeProvider = duplicateCodeProvider;
    }
}
