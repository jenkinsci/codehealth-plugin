package org.jenkinsci.plugins.codehealth.model;


import org.jenkinsci.plugins.database.jpa.PerItemTable;

import javax.persistence.*;
import java.util.Set;

/**
 * @author Michael Prankl
 */
@Entity
@PerItemTable
public class Issue {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(length = 2048, nullable = false)
    private String message;

    @Column(nullable = false)
    private long contextHashCode;

    @Enumerated(value = EnumType.ORDINAL)
    private Priority priority;

    @OneToMany(cascade = CascadeType.ALL)
    private Set<StateHistory> stateHistory;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private StateHistory currentState;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public long getContextHashCode() {
        return contextHashCode;
    }

    public void setContextHashCode(long contextHashCode) {
        this.contextHashCode = contextHashCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Issue issue = (Issue) o;

        if (contextHashCode != issue.contextHashCode) return false;
        if (id != issue.id) return false;
        if (!message.equals(issue.message)) return false;
        if (priority != issue.priority) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + message.hashCode();
        result = 31 * result + (int) (contextHashCode ^ (contextHashCode >>> 32));
        result = 31 * result + priority.hashCode();
        return result;
    }

    public Set<StateHistory> getStateHistory() {
        return stateHistory;
    }

    public void setStateHistory(Set<StateHistory> stateHistory) {
        this.stateHistory = stateHistory;
    }

    public StateHistory getCurrentState() {
        return currentState;
    }

    public void setCurrentState(StateHistory currentState) {
        this.currentState = currentState;
    }
}
