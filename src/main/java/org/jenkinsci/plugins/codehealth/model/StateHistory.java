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

    @ManyToOne
    private Issue issue;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StateHistory that = (StateHistory) o;

        if (id != that.id) return false;
        if (state != that.state) return false;
        if (!timestamp.equals(that.timestamp)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + timestamp.hashCode();
        result = 31 * result + state.hashCode();
        return result;
    }

    public Issue getIssue() {
        return issue;
    }

    public void setIssue(Issue issue) {
        this.issue = issue;
    }
}
