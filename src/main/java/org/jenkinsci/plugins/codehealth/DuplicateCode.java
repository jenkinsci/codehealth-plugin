package org.jenkinsci.plugins.codehealth;

/**
 * @author Michael Prankl
 */
public class DuplicateCode {

    private int numberOfDuplicateLines;
    private int numberOfDuplicateFiles;

    public DuplicateCode(int numberOfDuplicateLines, int numberOfDuplicateFiles) {
        this.numberOfDuplicateLines = numberOfDuplicateLines;
        this.numberOfDuplicateFiles = numberOfDuplicateFiles;
    }

    public int getNumberOfDuplicateLines() {
        return numberOfDuplicateLines;
    }

    public void setNumberOfDuplicateLines(int numberOfDuplicateLines) {
        this.numberOfDuplicateLines = numberOfDuplicateLines;
    }

    public int getNumberOfDuplicateFiles() {
        return numberOfDuplicateFiles;
    }

    public void setNumberOfDuplicateFiles(int numberOfDuplicateFiles) {
        this.numberOfDuplicateFiles = numberOfDuplicateFiles;
    }
}
