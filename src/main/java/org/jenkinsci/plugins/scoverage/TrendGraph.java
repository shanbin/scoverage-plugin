package org.jenkinsci.plugins.scoverage;

import hudson.util.ColorPalette;
import hudson.util.Graph;
import hudson.util.ShiftedCategoryAxis;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.ui.RectangleInsets;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.StaplerResponse;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TrendGraph extends Graph {
    private List<ScoverageResult> results = new ArrayList<ScoverageResult>();

    public TrendGraph(List<ScoverageResult> results) {
        super(-1, 400, 200);
        this.results = results;
    }

    @Override
    protected JFreeChart createGraph() {
        int size = results.size();

        final String[] rowKeys = {"Statements", "Conditionals"};
        final String[] columnKeys = new String[size];
        double[][] data = new double[2][size];

        for (int i = 0; i < size; i++) {
            columnKeys[size - i - 1] = "#" + results.get(i).getNum();
            data[0][size - i - 1] = results.get(i).getStatement();
            data[1][size - i - 1] = results.get(i).getCondition();
        }
        final CategoryDataset dataset = DatasetUtilities.createCategoryDataset(rowKeys, columnKeys, data);

        final JFreeChart chart = ChartFactory.createLineChart(
                null, // chart title
                null, // unused
                "%", // range axis label
                dataset, // data
                PlotOrientation.VERTICAL, // orientation
                true, // include legend
                true, // tooltips
                false // urls
        );

        chart.setBackgroundPaint(Color.white);

        final CategoryPlot plot = chart.getCategoryPlot();

        plot.setBackgroundPaint(Color.WHITE);
        plot.setOutlinePaint(null);
        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.black);

        CategoryAxis domainAxis = new ShiftedCategoryAxis(null);
        plot.setDomainAxis(domainAxis);
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_90);
        domainAxis.setLowerMargin(0.0);
        domainAxis.setUpperMargin(0.0);
        domainAxis.setCategoryMargin(0.0);

        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setUpperBound(100);
        rangeAxis.setLowerBound(0);

        final LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
        renderer.setBaseStroke(new BasicStroke(2.0f));
        ColorPalette.apply(renderer);

        plot.setInsets(new RectangleInsets(5.0, 0, 0, 5.0));

        return chart;
    }

    @Override
    public void doPng(StaplerRequest req, StaplerResponse rsp) throws IOException {
        super.doPng(req, rsp);
    }

    @Override
    public void doMap(StaplerRequest req, StaplerResponse rsp) throws IOException {
        super.doMap(req, rsp);
    }
}
