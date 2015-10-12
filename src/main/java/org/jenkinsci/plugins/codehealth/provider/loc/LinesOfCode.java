package org.jenkinsci.plugins.codehealth.provider.loc;

import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

/**
 * @author Michael Prankl
 */
@ExportedBean
public class LinesOfCode {

    private int linesOfCode;
    private int fileCount;


    public LinesOfCode(int linesOfCode, int fileCount) {
        this.linesOfCode = linesOfCode;
        this.fileCount = fileCount;
    }

    @Exported
    public int getLinesOfCode() {
        return linesOfCode;
    }

    public void setLinesOfCode(int linesOfCode) {
        this.linesOfCode = linesOfCode;
    }

    @Exported
    public int getFileCount() {
        return fileCount;
    }

    public void setFileCount(int fileCount) {
        this.fileCount = fileCount;
    }
}
