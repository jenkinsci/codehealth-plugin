package org.jenkinsci.plugins.codehealth.action;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.Run;
import hudson.model.TopLevelItem;
import jenkins.model.Jenkins;
import jenkins.model.TransientActionFactory;
import org.jenkinsci.plugins.codehealth.CodehealthPublisher;
import org.jenkinsci.plugins.codehealth.action.duplicates.DuplicateCodeBuildAction;
import org.jenkinsci.plugins.codehealth.service.BaseRepository;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Michael Prankl
 */
public abstract class AbstractBuildActionFactory<R extends BaseRepository> extends TransientActionFactory implements CodehealthActiveChecker {

    @Inject
    private R repositoryImplementation;

    public AbstractBuildActionFactory() {
        super();
        injectRepository();
    }

    @VisibleForTesting
    public void injectRepository() {
        Jenkins.getInstance().getInjector().injectMembers(this);
    }

    @Override
    public Class type() {
        return Run.class;
    }

    public R getRepositoryImplementation() {
        return repositoryImplementation;
    }

    @Override
    public boolean isCodehealthActive(AbstractProject project) {
        return project.getPublishersList().contains(CodehealthPublisher.DESCRIPTOR);
    }
}