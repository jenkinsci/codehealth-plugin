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

    @Exported(visibility = 1)
    public int getDuplicateLines() {
        return numberOfDuplicateLines;
    }

    public void setDuplicateLines(int numberOfDuplicateLines) {
        this.numberOfDuplicateLines = numberOfDuplicateLines;
    }

    @Exported(visibility = 1)
    public int getFilesWithDuplicates() {
        return numberOfDuplicateFiles;
    }

    public void setFilesWithDuplicates(int numberOfDuplicateFiles) {
        this.numberOfDuplicateFiles = numberOfDuplicateFiles;
    }

    public static DuplicateCode deltaOf(DuplicateCodeEntity fromDup, DuplicateCodeEntity toDup) {
        return new DuplicateCode(toDup.getDuplicateLines() - fromDup.getDuplicateLines(), toDup.getDuplicateFiles() - fromDup.getDuplicateFiles());
    }
}
