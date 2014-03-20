package org.jenkinsci.plugins.scoverage;

import hudson.FilePath;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Action;
import hudson.model.DirectoryBrowserSupport;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ScoverageProjectAction implements Action {

    private final AbstractProject<?, ?> project;

    public ScoverageProjectAction(AbstractProject<?, ?> project) {
        this.project = project;
    }

    public String getIconFileName() {
        return "";
    }

    public String getDisplayName() {
        return "Scoverage HTML Report";
    }

    public String getUrlName() {
        return "scoverage";
    }

    public String getSearchUrl() {
        return getUrlName();
    }

    public ScoverageBuildAction getLastSuccessfulBuildAction() {
        AbstractBuild<?, ?> last = project.getLastSuccessfulBuild();
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
        if (project.getLastBuild() != null && getDisplayName() != null) {
            String url = getLastSuccessfulBuildAction().getUrlName();
            FilePath path = new FilePath(project.getLastBuild().getRootDir()).child(url);
            return new DirectoryBrowserSupport(this, path, "Scoverage HTML Report",  "", false);
        } else {
            return null;
        }
    }
}
