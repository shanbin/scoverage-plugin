package org.jenkinsci.plugins.scoverage;

public class ScoverageResult {
    final private double statement;
    final private double condition;
    final private int num;

    public ScoverageResult(double statement, double condition, int num) {
        this.statement = statement;
        this.condition = condition;
        this.num = num;
    }

    public double getStatement() {
        return statement;
    }

    public double getCondition() {
        return condition;
    }

    public int getNum() {
        return num;
    }
}
