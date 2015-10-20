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
public class StateHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private Date timestamp;

    @Enumerated(value = EnumType.ORDINAL)
    private State state;

    @ManyToOne(optional = false)
    @JoinColumn(name = "buildNr")
    private Build build;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Exported
    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @Exported
    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    @Exported
    public Build getBuild() {
        return build;
    }

    public void setBuild(Build build) {
        this.build = build;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StateHistory that = (StateHistory) o;

        if (id != that.id) return false;
        if (!build.equals(that.build)) return false;
        if (state != that.state) return false;
        if (!timestamp.equals(that.timestamp)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + timestamp.hashCode();
        result = 31 * result + state.hashCode();
        result = 31 * result + build.hashCode();
        return result;
    }
}
