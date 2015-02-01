package org.jenkinsci.plugins.scoverage;

public enum ActionUrls {
    PROJECT_URL("scoverage"), BUILD_URL("scoverage-report");

    private final String text;

    ActionUrls(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }
}
