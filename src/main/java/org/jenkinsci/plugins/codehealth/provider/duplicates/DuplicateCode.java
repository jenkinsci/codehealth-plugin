package org.jenkinsci.plugins.codehealth.provider.duplicates;

import org.jenkinsci.plugins.codehealth.model.DuplicateCodeEntity;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

/**
 * @author Michael Prankl
 */
@ExportedBean
public class DuplicateCode {

    private int numberOfDuplicateLines;
    private int numberOfDuplicateFiles;

    public DuplicateCode(int numberOfDuplicateLines, int numberOfDuplicateFiles) {
        this.numberOfDuplicateLines = numberOfDuplicateLines;
        this.numberOfDuplicateFiles = numberOfDuplicateFiles;
    }

    @Exported
    public int getNumberOfDuplicateLines() {
        return numberOfDuplicateLines;
    }

    public void setNumberOfDuplicateLines(int numberOfDuplicateLines) {
        this.numberOfDuplicateLines = numberOfDuplicateLines;
    }

    @Exported
    public int getNumberOfFilesWithDuplicates() {
        return numberOfDuplicateFiles;
    }

    public void setNumberOfFilesWithDuplicates(int numberOfDuplicateFiles) {
        this.numberOfDuplicateFiles = numberOfDuplicateFiles;
    }

    public static DuplicateCode deltaOf(DuplicateCodeEntity fromDup, DuplicateCodeEntity toDup) {
        return new DuplicateCode(toDup.getDuplicateLines() - fromDup.getDuplicateLines(), toDup.getDuplicateFiles() - fromDup.getDuplicateFiles());
    }
}
