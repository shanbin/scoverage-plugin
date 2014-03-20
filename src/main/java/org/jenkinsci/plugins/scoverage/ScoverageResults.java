package org.jenkinsci.plugins.scoverage;

public class ScoverageResults {
    final private double statement;
    final private double condition;
    final private int num;

    public ScoverageResults(double statement, double condition, int num) {
        this.statement = statement;
        this.condition = condition;
        this.num = num;
        System.out.println("statement = " + statement + " condition = " + condition + " build = " + num);
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
