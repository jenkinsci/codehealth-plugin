package org.jenkinsci.plugins.codehealth.action;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;
import hudson.model.AbstractProject;
import hudson.model.TransientProjectActionFactory;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.codehealth.CodehealthPublisher;
import org.jenkinsci.plugins.codehealth.service.BaseRepository;

/**
 * @author Michael Prankl
 */
public abstract class AbstractProjectActionFactory<R extends BaseRepository> extends TransientProjectActionFactory implements CodehealthActiveChecker {

    @Inject
    private R repositoryImplementation;

    public AbstractProjectActionFactory() {
        super();
        injectRepository();
    }

    @VisibleForTesting
    public void injectRepository() {
        Jenkins.getInstance().getInjector().injectMembers(this);
    }

    public R getRepositoryImplementation() {
        return repositoryImplementation;
    }

    @Override
    public boolean isCodehealthActive(AbstractProject project) {
        return project.getPublishersList().contains(CodehealthPublisher.DESCRIPTOR);
    }
}
