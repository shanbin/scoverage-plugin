package org.jenkinsci.plugins.scoverage;

import hudson.FilePath;
import hudson.Launcher;
import hudson.Extension;
import hudson.Util;
import hudson.model.*;
import hudson.tasks.*;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

/**
 */
public class ScoveragePublisher extends Recorder {

    private final String reportDir;
    private final String reportFile;

    @DataBoundConstructor
    public ScoveragePublisher(String reportDir, String reportFile) {
        this.reportDir = reportDir;
        this.reportFile = reportFile;
    }

    public String getReportDir() {
        return reportDir;
    }

    public String getReportFile() {
        return reportFile == null || reportFile.trim().length() == 0 ? "scoverage.xml" : reportFile;
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {
        final File buildDir = build.getRootDir();
        final FilePath buildPath = new FilePath(buildDir);
        final FilePath workspace = build.getWorkspace();
        FilePath coverageReportDir = workspace.child(reportDir);

        try {
            listener.getLogger().println("Publishing Scoverage XML and HTML report...");

            final boolean reportExists = copyReport(coverageReportDir, buildPath, listener);
            if (!reportExists) {
                listener.getLogger().println("ERROR: cannot find scoverage report");
            }

            ScoverageResult result = processReport(buildPath);
            build.addAction(new ScoverageBuildAction(build, buildPath));

        } catch (IOException e) {
            Util.displayIOException(e, listener);
            listener.getLogger().println("Unable to copy scoverage from " + coverageReportDir + " to " + buildPath);
        } catch (Exception e) {
            listener.getLogger().println(e.getCause());
        }

        return true;
    }

    private boolean copyReport(FilePath coverageReport, FilePath buildPath, BuildListener listener) throws IOException, InterruptedException {
        if (coverageReport.exists()) {
            final FilePath parentDir = coverageReport.getParent();
            parentDir.copyRecursiveTo("**/*", buildPath);
            return true;
        } else {
            return false;
        }
    }

    private ScoverageResult processReport(FilePath path) {
        String[] ext = {"html"};
        try {
            Collection<File> list = FileUtils.listFiles(new File(path.toURI()), ext, true);
            for (File f : list) {
                String content = FileUtils.readFileToString(f);
                String pattern = f.getParent().replaceAll(".*scoverage-report", "scoverage-report");
                FileUtils.writeStringToFile(f, content.replaceAll("href=\".*" + pattern + "/", "href=\""));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ScoverageResult();
    }

    @Override
    public BuildStepDescriptor<Publisher> getDescriptor() {
        return DESCRIPTOR;
    }

    @Extension
    public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        DescriptorImpl() {
            super(ScoveragePublisher.class);
            load();
        }

        public String getDisplayName() {
            return "Publish Scoverage Report";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            save();
            return super.configure(req, formData);
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> jobType) {
            return true;
        }
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.BUILD;
    }

    @Override
    public Action getProjectAction(AbstractProject<?, ?> project) {
        return new ScoverageProjectAction(project);
    }
}

