package org.jenkinsci.plugins.scoverage;

import hudson.FilePath;
import hudson.Launcher;
import hudson.Extension;
import hudson.Util;
import hudson.model.*;
import hudson.remoting.VirtualChannel;
import hudson.tasks.*;
import net.sf.json.JSONObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.*;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

            ScoverageResult result = processReport(build, buildPath);
            build.addAction(new ScoverageBuildAction(build, buildPath, result));

        } catch (IOException e) {
            Util.displayIOException(e, listener);
            listener.getLogger().println("Unable to copy scoverage from " + coverageReportDir + " to " + buildPath);
        } catch (Exception e) {
            listener.getLogger().println(e.getCause());
        }

        return true;
    }

    private static final class ScovFinder implements FilePath.FileCallable<File> {
        private static final long serialVersionUID = 1;
        public File invoke(File dir, VirtualChannel channel) {
            Collection<File> list = FileUtils.listFiles(dir, new IOFileFilter() {
                public boolean accept(File f) {
                    try {
                        return f.isFile() && f.getName().equals("index.html")
                                          && FileUtils.readFileToString(f).contains("Scoverage Code Coverage");
                    } catch (IOException e) {
                        e.printStackTrace();
                        return false;
                    }
                }
                public boolean accept(File dir, String s) {
                    return dir.isDirectory();
                }
            }, TrueFileFilter.INSTANCE);
            return list.iterator().next();
        }
    }

    private boolean copyReport(FilePath coverageDir, FilePath buildPath, BuildListener listener)
            throws IOException, InterruptedException {
        if (coverageDir.exists()) {
            // search index.html recursively and copy its parent tree
            final FilePath indexPath = new FilePath(coverageDir.act(new ScovFinder()));
            final FilePath parentDir = indexPath.getParent();
            final FilePath toFile = buildPath.child(getReportFile());
            parentDir.copyRecursiveTo("**/*", buildPath.child("scoverage-report")); // copy HTML report
            coverageDir.child(getReportFile()).copyTo(toFile); // copy XML report
            return true;
        } else {
            return false;
        }
    }

    private ScoverageResult processReport(AbstractBuild build, FilePath path) {
        String[] ext = {"html"};
        double statement = 0;
        double condition = 0;
        try {
            // Fix HTML reports to use relative href
            Collection<File> list = FileUtils.listFiles(new File(path.toURI()), ext, true);
            for (File f : list) {
                String content = FileUtils.readFileToString(f);
                String pattern = f.getParent().replaceAll(".*scoverage-report", "scoverage-report");
                String relativeFix = content.replaceAll("href=\"/", "href=\"")
                                            .replaceAll("href=\".*" + pattern + "/", "href=\"");
                FileUtils.writeStringToFile(f, relativeFix);
            }
            // Parse scoverage.xml
            File report = new File(path.child(reportFile).toURI());
            Pattern pattern = Pattern.compile("^.* statement-rate=\"(.+?)\" branch-rate=\"(.+?)\"");
            BufferedReader in = new BufferedReader(new FileReader(report));
            String line;
            while ((line = in.readLine()) != null) {
                boolean found = false;
                Matcher matcher = pattern.matcher(line);
                while (matcher.find()) {
                    statement = Double.parseDouble(matcher.group(1));
                    condition = Double.parseDouble(matcher.group(2));
                    found = true;
                }
                if (found) {
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ScoverageResult(statement, condition, build.number);
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

