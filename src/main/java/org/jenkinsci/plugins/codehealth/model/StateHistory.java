package org.jenkinsci.plugins.codehealth.model;

import org.jenkinsci.plugins.database.jpa.PerItemTable;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Michael Prankl
 */
@Entity
@PerItemTable
public class StateHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(nullable = false)
    private Date timestamp;

    @Enumerated(value = EnumType.ORDINAL)
    private State state;

    @Column(nullable =  false)
    private int buildNr;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

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

        StateHistory that = (StateHistory) o;

        if (buildNr != that.buildNr) return false;
        if (id != that.id) return false;
        if (state != that.state) return false;
        if (timestamp != null ? !timestamp.equals(that.timestamp) : that.timestamp != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        result = 31 * result + buildNr;
        return result;
    }
}
