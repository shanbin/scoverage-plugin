package org.jenkinsci.plugins.scoverage;

import hudson.model.AbstractProject;
import org.mockito.Mockito;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class ScoverageProjectActionTest {
    AbstractProject project = Mockito.mock(AbstractProject.class);
    ScoverageProjectAction action = new ScoverageProjectAction(project);

    @Test
    public void urlTest() {
        assertEquals(action.getUrlName(), ActionUrls.PROJECT_URL.toString());
    }

}
