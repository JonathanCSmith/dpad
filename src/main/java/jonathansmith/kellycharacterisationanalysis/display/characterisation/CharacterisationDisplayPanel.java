package jonathansmith.kellycharacterisationanalysis.display.characterisation;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.*;

import jonathansmith.dpad.api.events.dataset.FullDatasetsArrivalEvent;
import jonathansmith.dpad.api.plugins.display.DisplayPanel;
import jonathansmith.dpad.api.plugins.events.Event;
import jonathansmith.dpad.api.plugins.events.IEventListener;
import jonathansmith.dpad.api.plugins.runtime.IPluginRuntime;

import jonathansmith.kellycharacterisationanalysis.KellyCharacterisationAnalysis;
import jonathansmith.kellycharacterisationanalysis.data.DeconvolutedData;
import jonathansmith.kellycharacterisationanalysis.events.CharacterisationCompleteEvent;
import jonathansmith.kellycharacterisationanalysis.events.DeconvolutionCompleteEvent;
import jonathansmith.kellycharacterisationanalysis.events.KellyCharacterisationFinishEvent;
import jonathansmith.kellycharacterisationanalysis.events.RateCalculationCompleteEvent;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 * Created by Jon on 22/07/2014.
 * <p/>
 * Template display panel for all GUI displays
 */
public class CharacterisationDisplayPanel extends DisplayPanel implements IEventListener {

    private static final ArrayList<Class<? extends Event>> EVENTS = new ArrayList<Class<? extends Event>>();

    static {
        EVENTS.add(KellyCharacterisationFinishEvent.class);
        EVENTS.add(FullDatasetsArrivalEvent.class);
        EVENTS.add(DeconvolutionCompleteEvent.class);
        EVENTS.add(RateCalculationCompleteEvent.class);
        EVENTS.add(CharacterisationCompleteEvent.class);
    }

    private final KellyCharacterisationAnalysis core;
    private final IPluginRuntime                runtime;

    private JPanel      contentPane;
    private JTextField  waitingForDataFromTextField;
    private JScrollPane scrollPane;
    private JPanel chartPanel;

    private LinkedList<DeconvolutedData> data;

    public CharacterisationDisplayPanel(KellyCharacterisationAnalysis core, IPluginRuntime runtime) {
        this.core = core;
        this.runtime = runtime;

        this.scrollPane.setVisible(false);
        this.chartPanel.setVisible(false);
    }

    @Override
    public JPanel getContentPane() {
        return this.contentPane;
    }

    @Override
    public void update() {

    }

    @Override
    public List<Class<? extends Event>> getEventsToListenFor() {
        return EVENTS;
    }

    @Override
    public void onEventReceived(Event event) {
        if (event instanceof KellyCharacterisationFinishEvent) {

        }

        else if (event instanceof FullDatasetsArrivalEvent) {
            this.waitingForDataFromTextField.setText("Building datasets and deconvoluting them.");
        }

        else if (event instanceof DeconvolutionCompleteEvent) {
            this.waitingForDataFromTextField.setText("Deconvoluting of data complete. Calculating optimal fluorescence procduction time");
        }

        else if (event instanceof RateCalculationCompleteEvent) {
            this.waitingForDataFromTextField.setText("Optimal fluorescence rate production times have been calculated. Generating characterisation data");
        }

        else if (event instanceof CharacterisationCompleteEvent) {
            this.data = ((CharacterisationCompleteEvent) event).getRelativeStrengths();
            final CategoryDataset categoryDataset = this.createDataset();
            final JFreeChart chart = this.createChart(categoryDataset);
            final ChartPanel chartPanel = new ChartPanel(chart);
            chartPanel.setPreferredSize(new Dimension(500, 270));
            this.scrollPane.setVisible(true);
            this.chartPanel.setVisible(true);
            this.chartPanel.setLayout(new BorderLayout());
            this.chartPanel.add(chartPanel, BorderLayout.CENTER);
            this.chartPanel.validate();
        }
    }

    private JFreeChart createChart(CategoryDataset categoryDataset) {
        JFreeChart chart = ChartFactory.createBarChart("Kelly Characterisation Results:", "Samples", "Relative Strength", categoryDataset, PlotOrientation.VERTICAL, false, false, false);
        chart.setBackgroundPaint(Color.white);
        final CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        final BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(false);
        final GradientPaint gp0 = new GradientPaint(
                0.0f, 0.0f, Color.blue,
                0.0f, 0.0f, Color.lightGray
        );
        final GradientPaint gp1 = new GradientPaint(
                0.0f, 0.0f, Color.green,
                0.0f, 0.0f, Color.lightGray
        );
        final GradientPaint gp2 = new GradientPaint(
                0.0f, 0.0f, Color.red,
                0.0f, 0.0f, Color.lightGray
        );
        renderer.setSeriesPaint(0, gp0);
        renderer.setSeriesPaint(1, gp1);
        renderer.setSeriesPaint(2, gp2);

        final CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(
                CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 6.0)
        );


        return chart;
    }

    private CategoryDataset createDataset() {
        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (DeconvolutedData value : this.data) {
            dataset.addValue(value.getRelativeStrength(), "Samples", value.getName());
        }
        return dataset;
    }
}
