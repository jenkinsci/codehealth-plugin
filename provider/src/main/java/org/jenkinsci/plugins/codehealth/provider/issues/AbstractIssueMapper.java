package org.jenkinsci.plugins.codehealth.provider.issues;

/**
 * Abstract class to support the mapping of annotations/warnings to codehealth Issues.
 *
 * @author Michael Prankl
 */
public abstract class AbstractIssueMapper<E extends Object> {

    /**
     * @param e your domain representation of an issue (annotation/warning)
     * @return the corresponding Issue
     */
    public abstract Issue map(E e);

}
