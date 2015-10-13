package org.jenkinsci.plugins.codehealth.action;

import org.kohsuke.stapler.HttpResponse;

/**
 * @author Michael Prankl
 */
public interface ResultRedirect {

    /**
     * @return HTTP redirect to the result page of the contributing plugin
     */
    HttpResponse doGoToResult();
}
