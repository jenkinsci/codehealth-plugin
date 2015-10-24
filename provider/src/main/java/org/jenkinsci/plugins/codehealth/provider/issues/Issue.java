package org.jenkinsci.plugins.codehealth.provider.issues;


import org.jenkinsci.plugins.codehealth.provider.Priority;

/**
 * Transfer object between contributing plugins and codehealth.
 *
 * @author Michael Prankl
 */
public class Issue {

    private long contextHashCode;
    private String message;
    private Priority priority;

    public Issue(long contextHashCode, String message, Priority priority) {
        this.contextHashCode = contextHashCode;
        this.message = message;
        this.priority = priority;
    }

    /**
     * @return unique identifier for an issue
     */
    public long getContextHashCode() {
        return contextHashCode;
    }

    public void setContextHashCode(final long contextHashCode) {
        this.contextHashCode = contextHashCode;
    }

    /**
     * @return the issue message
     */
    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

    /**
     * @return priority of the issue
     */
    public Priority getPriority() {
        return priority;
    }

    public void setPriority(final Priority priority) {
        this.priority = priority;
    }

}
