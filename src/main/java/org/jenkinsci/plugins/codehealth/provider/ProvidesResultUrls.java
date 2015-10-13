package org.jenkinsci.plugins.codehealth.provider;

import javax.annotation.Nullable;

/**
 * @author Michael Prankl
 */
public interface ProvidesResultUrls {
    /**
     * @return the url name for the build result of the plugin
     */
    @Nullable
    String getBuildResultUrl();

    /**
     * @return the url name fpr the project result of the plugin
     */
    @Nullable
    String getProjectResultUrl();
}
