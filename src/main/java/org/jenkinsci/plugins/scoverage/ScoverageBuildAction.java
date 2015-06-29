package org.jenkinsci.plugins.scoverage;

import hudson.FilePath;
import hudson.Functions;
import hudson.model.Action;
import hudson.model.DirectoryBrowserSupport;
import hudson.model.Result;
import hudson.model.Run;
import jenkins.tasks.SimpleBuildStep;
import org.kohsuke.stapler.StaplerProxy;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

@ExportedBean
public class ScoverageBuildAction implements Action, StaplerProxy, SimpleBuildStep.LastBuildAction {

    private final Run<?, ?> run;
    private final FilePath buildPath;
    private final ScoverageResult result;

    public ScoverageBuildAction(Run<?, ?> run, FilePath buildPath, ScoverageResult result) {
        this.run = run;
        this.buildPath = buildPath;
        this.result = result;
    }

    public String getIconFileName() {
        return Functions.getResourcePath()+"/plugin/scoverage/images/scoverage.png";
    }

    public String getDisplayName() {
        return "Scoverage HTML Report";
    }

    public String getUrlName() {
        return ActionUrls.BUILD_URL.toString();
    }

    public Object getTarget() {
        return null;
    }

    @Exported(name = "scoverage")
    public ScoverageResult getResult() {
        return result;
    }

    public ScoverageBuildAction getPreviousBuildAction() {
        Run<?, ?> cur = run;
        ScoverageBuildAction action = null;
        while (true) {
            cur = cur.getPreviousBuild();
            if (cur != null) {
                if (cur.getResult() != Result.SUCCESS) {
                    continue;
                }
                ScoverageBuildAction act = cur.getAction(ScoverageBuildAction.class);
                if (act != null) {
                    action = act;
                    break;
                }
            } else {
                break;
            }
        }
        return action;
    }

    public DirectoryBrowserSupport doDynamic(StaplerRequest req, StaplerResponse rsp) throws IOException, ServletException, InterruptedException {
        return new DirectoryBrowserSupport(this, buildPath.child(getUrlName()), "Scoverage HTML Report", "", false);
    }

    @Override
    public Collection<? extends Action> getProjectActions() {
        return Collections.<Action>singleton(new ScoverageProjectAction(run.getParent()));

    }
}
