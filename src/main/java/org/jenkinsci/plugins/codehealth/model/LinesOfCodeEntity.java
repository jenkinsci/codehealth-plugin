package org.jenkinsci.plugins.codehealth.model;

import org.jenkinsci.plugins.database.jpa.PerItemTable;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import javax.persistence.*;

/**
 * @author Michael Prankl
 */
@Entity(name = "LinesOfCode")
@PerItemTable
@Table(uniqueConstraints = {@UniqueConstraint(name = "UNI_BUILDNR", columnNames = {"buildNr"})})
@NamedQueries({
        @NamedQuery(name = LinesOfCodeEntity.FIND_BY_BUILD_NR, query = "select loc from LinesOfCode loc where loc.buildNr = :buildNr")
})
@ExportedBean
public class LinesOfCodeEntity {

    public static final String FIND_BY_BUILD_NR = "LinesOfCode.findByBuildNr";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private int buildNr;

    @Column(nullable = false)
    private long linesOfCode;

    @Column(nullable = false)
    private long files;

    @Exported
    public int getBuildNr() {
        return buildNr;
    }

    public void setBuildNr(int buildNr) {
        this.buildNr = buildNr;
    }

    @Exported
    public long getLinesOfCode() {
        return linesOfCode;
    }

    public void setLinesOfCode(long linesOfCode) {
        this.linesOfCode = linesOfCode;
    }

    @Exported(name = "fileCount")
    public long getFiles() {
        return files;
    }

    public void setFiles(long files) {
        this.files = files;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LinesOfCodeEntity that = (LinesOfCodeEntity) o;

        if (buildNr != that.buildNr) return false;
        if (files != that.files) return false;
        if (id != that.id) return false;
        if (linesOfCode != that.linesOfCode) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + buildNr;
        result = 31 * result + (int) (linesOfCode ^ (linesOfCode >>> 32));
        result = 31 * result + (int) (files ^ (files >>> 32));
        return result;
    }
}
