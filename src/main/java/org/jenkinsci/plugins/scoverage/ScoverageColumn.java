package org.jenkinsci.plugins.scoverage;

import hudson.Extension;
import hudson.model.Descriptor;
import hudson.model.Job;
import hudson.model.Run;
import hudson.views.ListViewColumn;
import hudson.views.ListViewColumnDescriptor;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

public class ScoverageColumn extends ListViewColumn {

    @DataBoundConstructor
    public ScoverageColumn() {
    }

    public boolean scoverageExists(final Job<?, ?> job) {
        final Run<?, ?> lastSuccessfulBuild = job.getLastSuccessfulBuild();

        if (lastSuccessfulBuild == null) {
            return false;
        } else if (lastSuccessfulBuild.getAction(ScoverageBuildAction.class) == null) {
            return false;
        }

        return true;
    }

    public String getData(final Job<?, ?> job) {
        if (!scoverageExists(job)) {
            return "N/A";
        } else {
            return job.getLastSuccessfulBuild().getAction(ScoverageBuildAction.class).getResult().toString();
        }
    }

    @Extension
    public static final Descriptor<ListViewColumn> DESCRIPTOR = new DescriptorImpl();

    @Override
    public Descriptor<ListViewColumn> getDescriptor() {
        return DESCRIPTOR;
    }

    private static class DescriptorImpl extends ListViewColumnDescriptor {
        @Override
        public ListViewColumn newInstance(final StaplerRequest req, final JSONObject formData) throws FormException {
            return new ScoverageColumn();
        }

        @Override
        public boolean shownByDefault() {
            return false;
        }

        @Override
        public String getDisplayName() {
            return "Scoverage Result";
        }
    }
}
