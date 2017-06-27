package org.jenkinsci.plugins.scoverage;

import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

import java.util.Locale;

@ExportedBean
public class ScoverageResult {
    final private double statement;
    final private double condition;
    final private int num;

    public ScoverageResult(double statement, double condition, int num) {
        this.statement = statement;
        this.condition = condition;
        this.num = num;
    }

    @Exported(visibility = 2)
    public double getStatement() {
        return statement;
    }

    @Exported(name = "branch", visibility = 2)
    public double getCondition() {
        return condition;
    }

    public int getNum() {
        return num;
    }

    @Override
    public String toString() {
        return String.format(Locale.US,"%.1f%% / %.1f%%", statement, condition);
    }
}
