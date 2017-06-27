package org.jenkinsci.plugins.scoverage;

import hudson.FilePath;
import org.testng.annotations.Test;

import java.io.File;

/**
 * Created by j.salmon on 27/06/2017.
 */
public class HTMLRelativeHrefTest {
    @Test
    public void testProcess() throws Exception {
        HTMLRelativeHref html = new  HTMLRelativeHref(new FilePath(new File(this.getClass().getClassLoader().getResource("dir").getPath())));
        html.process();
        //no error expected with windows OS
    }

}