package org.jenkinsci.plugins.codehealth.model;

import org.jenkinsci.plugins.database.jpa.PerItemTable;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Michael Prankl
 */
@Entity
@PerItemTable
@ExportedBean
@NamedQueries(
        @NamedQuery(name = Build.FIND_BY_NUMBER, query = "select b from Build b where b.number = :buildNr")
)
public class Build {

    public static final String FIND_BY_NUMBER = "Build.findByNumber";

    @Id
    private int number;

    @Column(nullable = false)
    private Date timestamp;

    @Exported
    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    @Exported
    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Build build = (Build) o;

        if (number != build.number) return false;
        if (!timestamp.equals(build.timestamp)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = number;
        result = 31 * result + timestamp.hashCode();
        return result;
    }
}
