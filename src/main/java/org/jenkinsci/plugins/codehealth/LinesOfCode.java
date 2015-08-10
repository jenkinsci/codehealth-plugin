package org.jenkinsci.plugins.codehealth;

/**
 * @author Michael Prankl
 */
public class LinesOfCode {

    private int linesOfCode;
    private int fileCount;


    public LinesOfCode(int linesOfCode, int fileCount) {
        this.linesOfCode = linesOfCode;
        this.fileCount = fileCount;
    }

    public int getLinesOfCode() {
        return linesOfCode;
    }

    public void setLinesOfCode(int linesOfCode) {
        this.linesOfCode = linesOfCode;
    }

    public int getFileCount() {
        return fileCount;
    }

    public void setFileCount(int fileCount) {
        this.fileCount = fileCount;
    }
}
