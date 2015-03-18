package org.jenkinsci.plugins.scoverage;

import hudson.FilePath;
import hudson.Functions;
import hudson.model.AbstractBuild;
import hudson.model.Action;
import hudson.model.DirectoryBrowserSupport;
import hudson.model.Result;
import org.kohsuke.stapler.StaplerProxy;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import javax.servlet.ServletException;
import java.io.IOException;

@ExportedBean
public class ScoverageBuildAction implements Action, StaplerProxy {

    private final AbstractBuild<?, ?> build;
    private final FilePath buildPath;
    private final ScoverageResult result;

    public ScoverageBuildAction(AbstractBuild<?, ?> build, FilePath buildPath, ScoverageResult result) {
        this.build = build;
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

    public AbstractBuild<?, ?> getBuild() {
        return build;
    }

    public ScoverageBuildAction getPreviousBuildAction() {
        AbstractBuild<?, ?> b = build;
        ScoverageBuildAction action = null;
        while (true) {
            b = b.getPreviousBuild();
            if (b != null) {
                if (b.getResult() != Result.SUCCESS) {
                    continue;
                }
                ScoverageBuildAction act = b.getAction(ScoverageBuildAction.class);
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
}
