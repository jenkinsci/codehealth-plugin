package org.jenkinsci.plugins.codehealth.model;

import org.jenkinsci.plugins.database.jpa.PerItemTable;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import javax.persistence.*;

/**
 * @author Michael Prankl
 */
@Entity(name = "DuplicateCode")
@PerItemTable
@ExportedBean
@Table(uniqueConstraints = {@UniqueConstraint(name = "DUP_UNI_BUILDNR", columnNames = {"buildNr"})})
@NamedQueries({@NamedQuery(name = DuplicateCodeEntity.FIND_BY_BUILD_NR, query = "select dup from DuplicateCode dup where dup.buildNr = :buildNr")})
public class DuplicateCodeEntity {

    public static final String FIND_BY_BUILD_NR = "DuplicateCode.findByBuildNr";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private int duplicateLines;

    @Column(nullable = false)
    private int duplicateFiles;

    @Column(nullable = false)
    private int buildNr;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Exported
    public int getDuplicateLines() {
        return duplicateLines;
    }

    public void setDuplicateLines(int duplicateLines) {
        this.duplicateLines = duplicateLines;
    }

    @Exported
    public int getDuplicateFiles() {
        return duplicateFiles;
    }

    public void setDuplicateFiles(int duplicateFiles) {
        this.duplicateFiles = duplicateFiles;
    }

    @Exported
    public int getBuildNr() {
        return buildNr;
    }

    public void setBuildNr(int buildNr) {
        this.buildNr = buildNr;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DuplicateCodeEntity that = (DuplicateCodeEntity) o;

        if (buildNr != that.buildNr) return false;
        if (duplicateFiles != that.duplicateFiles) return false;
        if (duplicateLines != that.duplicateLines) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = duplicateLines;
        result = 31 * result + duplicateFiles;
        result = 31 * result + buildNr;
        return result;
    }
}
