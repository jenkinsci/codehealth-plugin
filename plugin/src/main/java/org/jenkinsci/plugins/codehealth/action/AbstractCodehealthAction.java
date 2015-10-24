package org.jenkinsci.plugins.codehealth.action;

import hudson.model.Action;
import hudson.model.Api;
import hudson.model.TopLevelItem;

/**
 * @author Michael Prankl
 */
public abstract class AbstractCodehealthAction implements Action {

    private transient TopLevelItem topLevelItem;

    public AbstractCodehealthAction(TopLevelItem topLevelItem) {
        this.topLevelItem = topLevelItem;
    }

    @Override
    public String getIconFileName() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return null;
    }

    public Api getApi() {
        return new Api(this);
    }

    public TopLevelItem getTopLevelItem() {
        return topLevelItem;
    }

    public void setTopLevelItem(TopLevelItem topLevelItem) {
        this.topLevelItem = topLevelItem;
    }
}
