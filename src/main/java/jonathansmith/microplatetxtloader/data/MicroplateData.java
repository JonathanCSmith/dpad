package jonathansmith.microplatetxtloader.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Jon on 29/09/2014.
 */
public class MicroplateData {

    private String                        source;
    private Integer                       time;
    private String                        observationConditions;
    private ArrayList<ArrayList<Integer>> dataValues;

    public MicroplateData() {
    }

    public boolean buildFromFile(File file, Integer time) throws IOException {
        this.source = file.getName();
        this.time = time;
        BufferedReader reader = new BufferedReader(new FileReader(file.getAbsoluteFile()));

        this.dataValues = new ArrayList<ArrayList<Integer>>();
        String line;
        int lineNumber = 0;
        while ((line = reader.readLine()) != null) {
            if (lineNumber == 1) {
                this.observationConditions = line;
            }

            else if (lineNumber > 2) {
                this.dataValues.add(new ArrayList<Integer>());
                String[] stringValues = line.split("\t");
                for (int i = 0; i < stringValues.length; i++) {
                    if (i == 0) {
                        continue;
                    }

                    this.dataValues.get(lineNumber - 3).add(Integer.parseInt(stringValues[i]));
                }
            }

            lineNumber++;
        }

        reader.close();

        int currentValue = -1;
        for (ArrayList<Integer> value : this.dataValues) {
            if (currentValue == -1) {
                currentValue = value.size();
            }

            else {
                if (currentValue != value.size()) {
                    return false;
                }
            }
        }

        return true;
    }

    public int getHeight() {
        return this.dataValues.size();
    }

    public int getWidth() {
        return this.dataValues.get(0).size();
    }

    public String getSource() {
        return this.source;
    }

    public Integer getTime() {
        return this.time;
    }

    public String getSampleMeasurementCondition() {
        return this.observationConditions;
    }

    public int getMeasurementAt(int i, int j) {
        return this.dataValues.get(i).get(j);
    }
}
