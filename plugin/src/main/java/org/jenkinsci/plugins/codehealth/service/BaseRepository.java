package org.jenkinsci.plugins.codehealth.service;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Injector;
import jenkins.model.Jenkins;

/**
 * @author Michael Prankl
 */
public class BaseRepository {

    /**
     * Can be overriden in testing subclasses to get rid of the singleton dependency.
     *
     * @return the Guice Injector
     */
    @VisibleForTesting
    public Injector getInjector() {
        return Jenkins.getInstance().getInjector();
    }

}
