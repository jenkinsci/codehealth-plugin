package org.jenkinsci.plugins.codehealth.service;

import hudson.model.Run;
import hudson.model.TopLevelItem;
import org.jenkinsci.plugins.codehealth.model.IssueEntity;
import org.jenkinsci.plugins.codehealth.model.State;
import org.jenkinsci.plugins.codehealth.provider.issues.Issue;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Michael Prankl
 */
public abstract class IssueRepository extends BaseRepository {

    /**
     * Updates new or open issues.
     *
     * @param data the collection of found issues
     * @param run  the Run which produced the issues
     */
    public abstract void updateIssues(Collection<IssueEntity> data, Run<?, ?> run);

    /**
     * Mark some issues as fixed.
     *
     * @param data the issues that have been fixed
     * @param run  the corresponding Run
     */
    public abstract void fixedIssues(List<IssueEntity> data, Run<?, ?> run);

    /**
     * Load all issues for this top-level-item.
     *
     * @param topLevelItem the top-level-item (job)
     * @return all Issues for this top-level-item
     */
    public abstract Collection<IssueEntity> loadIssues(TopLevelItem topLevelItem);

    /**
     * Load state-specific issues for this top-level-item.
     *
     * @param topLevelItem the top-level-item (job)
     * @param states       the allowed states (open, new, closed)
     * @return the found issues
     */
    public abstract Collection<IssueEntity> loadIssues(TopLevelItem topLevelItem, List<State> states);

    /**
     * Load all issues which have been in this state for this build.
     *
     * @param topLevelItem the top-level-item (job)
     * @param buildNr      the corresponding build number
     * @param state        the corresponding state
     * @return the found issues
     */
    public abstract Collection<IssueEntity> loadIssues(TopLevelItem topLevelItem, int buildNr, State state);


    /**
     * Find all Issues that are in state New/Open and are not part of the existing issues.
     *
     * @param topLevelItem   the top-level-item (job)
     * @param existingIssues the existing issues reported for a build
     * @param origin         origin of the issues
     * @return issues that have been fixed
     */
    public abstract Collection<IssueEntity> calculateFixedIssues(TopLevelItem topLevelItem,
                                                                 Collection<Issue> existingIssues, String origin);

    /**
     * @param topLevelItem the top-level-item (job)
     * @return new/open issue count by priority, grouped by build number
     */
    public abstract Map<Integer, JPAIssueRepository.IssuesByPriority> loadIssueCountPerBuild(TopLevelItem topLevelItem);
}
