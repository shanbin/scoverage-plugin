package org.jenkinsci.plugins.scoverage;

import hudson.FilePath;
import hudson.model.AbstractBuild;
import hudson.model.Action;
import hudson.model.DirectoryBrowserSupport;
import org.kohsuke.stapler.StaplerProxy;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import javax.servlet.ServletException;
import java.io.IOException;

public class ScoverageBuildAction implements Action, StaplerProxy {

    private final AbstractBuild<?, ?> build;
    private final FilePath buildPath;

    public ScoverageBuildAction(AbstractBuild<?, ?> build, FilePath buildPath) {
        this.build = build;
        this.buildPath = buildPath;
    }

    public String getIconFileName() {
        return "";
    }

    public String getDisplayName() {
        return "Scoverage HTML Report";
    }

    public String getUrlName() {
        return "scoverage-report";
    }

    public Object getTarget() {
        return null;
    }

    public AbstractBuild<?, ?> getBuild() {
        return build;
    }

    public DirectoryBrowserSupport doDynamic(StaplerRequest req, StaplerResponse rsp) throws IOException, ServletException, InterruptedException {
        return new DirectoryBrowserSupport(this, buildPath.child(getUrlName()), "Scoverage HTML Report", "", false);
    }
}
