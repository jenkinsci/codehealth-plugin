package org.jenkinsci.plugins.codehealth.model;


import org.jenkinsci.plugins.codehealth.provider.Priority;
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
@NamedNativeQueries(
        @NamedNativeQuery(name = IssueEntity.NATIVE_FIND_OPEN_ISSUE_COUNT_PER_BUILD,
                query = "select b.number as buildnumber , count(i.id) as issuecount from Build b " +
                        "join StateHistory sh on sh.buildNr = b.number " +
                        "join Issue_StateHistory ish on ish.statehistory_id = sh.id " +
                        "join Issue i on i.id = ish.issue_id " +
                        "where sh.state <> 2 " +
                        "group by b.number " +
                        "order by b.number ",
                resultSetMapping = "openIssueCountMapping"
        )
)
@SqlResultSetMapping(name = "openIssueCountMapping", columns = {@ColumnResult(name = "buildnumber"), @ColumnResult(name = "issuecount")})
@ExportedBean
public class IssueEntity implements Cloneable {

    public static final String FIND_BY_HASH_AND_ORIGIN = "Issue.findByHashAndOrigin";
    public static final String FIND_ALL = "Issue.findAll";
    public static final String FIND_BY_STATE_AND_BUILD = "Issue.findByStateAndBuild";
    public static final String FIND_BY_STATE = "Issue.findByState";
    public static final String FIND_BY_ORIGIN_AND_STATE = "Issue.findByOriginAndState";
    public static final String NATIVE_FIND_OPEN_ISSUE_COUNT_PER_BUILD = "Native_Issue.findOpenIssueCountPerBuild";

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
    @JoinColumn(name = "currentstate_id")
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

    //CHECKSTYLE:OFF
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IssueEntity that = (IssueEntity) o;

        if (id != that.id) return false;
        if (contextHashCode != that.contextHashCode) return false;
        if (!message.equals(that.message)) return false;
        if (priority != that.priority) return false;
        return origin.equals(that.origin);

    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + message.hashCode();
        result = 31 * result + (int) (contextHashCode ^ (contextHashCode >>> 32));
        result = 31 * result + priority.hashCode();
        result = 31 * result + origin.hashCode();
        return result;
    }
    //CHECKSTYLE:ON
}
