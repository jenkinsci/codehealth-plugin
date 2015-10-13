package org.jenkinsci.plugins.codehealth.provider.loc;

import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

/**
 * @author Michael Prankl
 */
@ExportedBean
public class LinesOfCode {

    private long linesOfCode;
    private long fileCount;


    public LinesOfCode(long linesOfCode, long fileCount) {
        this.linesOfCode = linesOfCode;
        this.fileCount = fileCount;
    }

    @Exported
    public long getLinesOfCode() {
        return linesOfCode;
    }

    public void setLinesOfCode(long linesOfCode) {
        this.linesOfCode = linesOfCode;
    }

    @Exported
    public long getFileCount() {
        return fileCount;
    }

    public void setFileCount(long fileCount) {
        this.fileCount = fileCount;
    }
}
