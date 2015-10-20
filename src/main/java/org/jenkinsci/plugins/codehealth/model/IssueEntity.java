package org.jenkinsci.plugins.codehealth.model;


import org.jenkinsci.plugins.codehealth.provider.issues.Issue;
import org.jenkinsci.plugins.database.jpa.PerItemTable;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import javax.persistence.*;
import java.util.Set;

/**
 * @author Michael Prankl
 */
@Entity(name = "Issue")
@PerItemTable
@Table(uniqueConstraints = {
        @UniqueConstraint(name = "UNI_HASH_ORIGIN", columnNames = {"contextHashCode", "origin"})
})
@NamedQueries({
        @NamedQuery(name = IssueEntity.FIND_BY_HASH_AND_ORIGIN, query = "select i from Issue i where i.contextHashCode = :contextHashCode and i.origin = :origin"),
        @NamedQuery(name = IssueEntity.FIND_ALL, query = "select i from Issue i"),
        @NamedQuery(name = IssueEntity.FIND_BY_STATE_AND_BUILD, query = "select i from Issue i join i.stateHistory sh where sh.build.number = :buildNr and sh.state = :state"),
        @NamedQuery(name = IssueEntity.FIND_BY_STATE, query = "select i from Issue i where i.currentState.state in :state"),
        @NamedQuery(name = IssueEntity.FIND_BY_ORIGIN_AND_STATE, query = "select i from  Issue i where i.currentState.state in :states and i.origin = :origin")
})
@ExportedBean
public class IssueEntity implements Cloneable {

    public static final String FIND_BY_HASH_AND_ORIGIN = "Issue.findByHashAndOrigin";
    public static final String FIND_ALL = "Issue.findAll";
    public static final String FIND_BY_STATE_AND_BUILD = "Issue.findByStateAndBuild";
    public static final String FIND_BY_STATE = "Issue.findByState";
    public static final String FIND_BY_ORIGIN_AND_STATE = "Issue.findByOriginAndState";

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

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private StateHistory currentState;

    @Column
    private String origin;

    @Exported
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Exported
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Exported
    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    @Exported
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

        IssueEntity issue = (IssueEntity) o;

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

    @Exported(name = "state")
    public StateHistory getCurrentState() {
        return currentState;
    }

    public void setCurrentState(StateHistory currentState) {
        this.currentState = currentState;
    }

    @Exported
    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
