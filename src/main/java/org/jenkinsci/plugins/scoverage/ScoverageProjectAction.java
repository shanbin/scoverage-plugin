package org.jenkinsci.plugins.scoverage;

import hudson.FilePath;
import hudson.Functions;
import hudson.model.*;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ScoverageProjectAction implements Action {

    private final Job<?, ?> job;

    public ScoverageProjectAction(Job<?, ?> job) {
        this.job = job;
    }

    @Deprecated
    public ScoverageProjectAction(AbstractProject<?, ?> project) {
        this((Job) project);
    }

    public String getIconFileName() {
        return Functions.getResourcePath()+"/plugin/scoverage/images/scoverage.png";
    }

    public String getDisplayName() {
        return "Scoverage HTML Report";
    }

    public String getUrlName() {
        return ActionUrls.PROJECT_URL.toString();
    }

    public String getSearchUrl() {
        return getUrlName();
    }

    public ScoverageBuildAction getLastSuccessfulBuildAction() {
        Run<?, ?> last = job.getLastSuccessfulBuild();
        if (last != null) {
            return last.getAction(ScoverageBuildAction.class);
        }
        return null;
    }

    public TrendGraph getTrendGraph() {
        List<ScoverageResult> results = new ArrayList<ScoverageResult>();
        ScoverageBuildAction action = getLastSuccessfulBuildAction();
        while (action != null) {
            results.add(action.getResult());
            action = action.getPreviousBuildAction();
        }
        return new TrendGraph(results);
    }

    public DirectoryBrowserSupport doDynamic(StaplerRequest req, StaplerResponse rsp) throws IOException, ServletException, InterruptedException {
        if (job.getLastBuild() != null && getDisplayName() != null) {
            ScoverageBuildAction lastSuccessfulAction = getLastSuccessfulBuildAction();

            if (lastSuccessfulAction != null) {
                String url = lastSuccessfulAction.getUrlName();
                FilePath path = new FilePath(job.getLastBuild().getRootDir()).child(url);

            return new DirectoryBrowserSupport(this, path, "Scoverage HTML Report", "", false);
            }
        }

        return null;
    }
}
