package org.jenkinsci.plugins.codehealth.action;

import org.kohsuke.stapler.HttpResponse;

/**
 * @author Michael Prankl
 */
public interface ResultUrlNameProvider {

    /**
     * @return HTTP redirect to the result page of the contributing plugin
     */
    HttpResponse doGoToResult();

    /**
     * @return the result url name of the contributing plugin
     */
    String getResultUrlName();
}
