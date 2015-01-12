package jonathansmith.kellycharacterisationanalysis.data;

/**
 * Created by Jon on 29/09/2014.
 */
public class DeconvolutedData {

    private final int sampleIdentifier;
    private final int maxNumberOfIndependents;
    private final int maxNumberOfDependents;

    private boolean isReference;

    private int[][][] gfpData;
    private int[][][] gfpTimes;

    private double[] averagedGFP;
    private double[] averagedTimes;
    private double   relativeStrength;
    private String   name;

    public DeconvolutedData(int i, int independentNumber, int dependentNumber) {
        this.sampleIdentifier = i;
        this.maxNumberOfIndependents = independentNumber;
        this.maxNumberOfDependents = dependentNumber;

        this.gfpData = new int[this.maxNumberOfIndependents][this.maxNumberOfDependents][];
        this.gfpTimes = new int[this.maxNumberOfIndependents][this.maxNumberOfDependents][];
    }

    public int getSampleIdentifier() {
        return this.sampleIdentifier;
    }

    public boolean getIsReference() {
        return this.isReference;
    }

    public void setIsReference(boolean reference) {
        this.isReference = reference;
    }

    public void setGFPValues(int independentNumber, int dependentNumber, int[][] data) {
        int[] times = new int[data.length];
        int[] values = new int[data.length];
        for (int i = 0; i < data.length; i++) {
            times[i] = data[i][0];
            values[i] = data[i][1];
        }

        this.gfpData[independentNumber][dependentNumber] = values;
        this.gfpTimes[independentNumber][dependentNumber] = values;
    }

    public void average() {
        int time = this.gfpTimes[0][0].length;
        double[] gfps = new double[time];
        double[] gfpts = new double[time];
        double currentGFP = 0;
        double currentGFPT = 0;
        for (int t = 0; t < time; t++) {
            double averageGFP = 0;
            double averageGFPTime = 0;
            for (int i = 0; i < this.maxNumberOfIndependents; i++) {
                for (int j = 0; j < this.maxNumberOfDependents; j++) {
                    averageGFP += this.gfpData[i][j][t];
                    averageGFPTime += this.gfpTimes[i][j][t];
                }

                averageGFP /= this.maxNumberOfDependents;
                averageGFPTime /= this.maxNumberOfDependents;

                currentGFP += averageGFP;
                currentGFPT += averageGFPTime;
            }

            currentGFP /= this.maxNumberOfIndependents;
            currentGFPT /= this.maxNumberOfIndependents;

            gfps[t] = currentGFP;
            gfpts[t] = currentGFPT;
        }

        this.averagedGFP = gfps;
        this.averagedTimes = gfpts;
    }

    public double[] getAveragedTimes() {
        return this.averagedTimes;
    }

    public double[] getAveragedGFP() {
        return this.averagedGFP;
    }

    public double getRelativeStrength() {
        return relativeStrength;
    }

    public void setRelativeStrength(double relativeStrength) {
        this.relativeStrength = relativeStrength;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
