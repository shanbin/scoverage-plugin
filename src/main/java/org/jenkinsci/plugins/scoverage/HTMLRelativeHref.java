package org.jenkinsci.plugins.scoverage;

import hudson.FilePath;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

/**
 * Created by j.salmon on 27/06/2017.
 */
public class HTMLRelativeHref {
    private FilePath path;
    private String[] ext = {"html"};

    public HTMLRelativeHref(FilePath path){
         this.path =path;
    }

    public void process() throws IOException, InterruptedException {
        Collection<File> list = FileUtils.listFiles(new File(path.toURI()), ext, true);
        String separator = (File.separator.equals("\\")?"\\":"")+File.separator;

        for (File f : list) {
            String content = FileUtils.readFileToString(f);

            // Get filename with HTML styled path separator


            String pattern = f.getParent().replaceAll(separator, "/")
                    .replaceAll(".*scoverage-report", "scoverage-report");

            String relativeFix = content.replaceAll("href=\"/", "href=\"")
                    .replaceAll("href=\".*" + pattern + "/", "href=\"");

            FileUtils.writeStringToFile(f, relativeFix);
        }
    }
}
