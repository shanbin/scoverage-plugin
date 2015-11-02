package org.jenkinsci.plugins.scoverage;

import hudson.model.Job;
import hudson.model.Run;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import static org.mockito.Mockito.when;
import static org.testng.AssertJUnit.assertEquals;

public class ScoverageColumnTest {
    Job job = Mockito.mock(Job.class);
    Run run = Mockito.mock(Run.class);
    ScoverageBuildAction action = Mockito.mock(ScoverageBuildAction.class);
    ScoverageColumn column = new ScoverageColumn();

    @Test
    public void getDataTest1() {
        assertEquals(column.getData(job), "N/A");
    }

    @Test
    public void getDataTest2() {
        when(job.getLastSuccessfulBuild()).thenReturn(run);
        assertEquals(column.getData(job), "N/A");
    }

    @Test
    public void scoverageExistsTest() {
        when(job.getLastSuccessfulBuild().getAction(ScoverageBuildAction.class)).thenReturn(action);
        assertEquals(column.scoverageExists(job), true);
    }

    @Test
    public void getDescriptorTest() {
        assertEquals(column.getDescriptor().getDisplayName(), "Scoverage Result");
    }
}
