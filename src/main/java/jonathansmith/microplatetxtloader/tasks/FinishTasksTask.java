package jonathansmith.microplatetxtloader.tasks;

import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import jonathansmith.dpad.api.plugins.data.Dataset;
import jonathansmith.dpad.api.plugins.runtime.IPluginRuntime;
import jonathansmith.dpad.api.plugins.tasks.IPluginTask;

import jonathansmith.microplatetxtloader.MicroplateTXTLoader;
import jonathansmith.microplatetxtloader.data.MicroplateData;

/**
 * Created by Jon on 29/09/2014.
 */
public class FinishTasksTask implements IPluginTask {

    private static final String TASK_NAME = "Finish up!";

    private final MicroplateTXTLoader core;

    public FinishTasksTask(MicroplateTXTLoader microplateTXTLoader) {
        this.core = microplateTXTLoader;
    }

    @Override
    public String getTaskName() {
        return TASK_NAME;
    }

    @Override
    public void runTask(IPluginRuntime engine) {
        if (this.core.isQuittingEarly()) {
            return;
        }

        else {
            LinkedList<MicroplateData> data = this.core.getMicroplateData();
            String[][] sampleInformation = this.core.getDataMask();

            TreeMap<Integer, MicroplateData> map = new TreeMap<Integer, MicroplateData>();
            for (MicroplateData info : data) {
                map.put(info.getTime(), info);
            }

            int count = 0;
            for (int i = 0; i < sampleInformation.length; i++) {
                for (int j = 0; j < sampleInformation[i].length; j++) {
                    if (sampleInformation[i][j].contentEquals("NONE")) {
                        continue;
                    }

                    Dataset dataset = new Dataset();
                    dataset.setSampleName(sampleInformation[i][j]);
                    dataset.setSampleMeasurementCondition(data.get(0).getSampleMeasurementCondition());
                    for (Map.Entry<Integer, MicroplateData> entry : map.entrySet()) {
                        dataset.addMeasurement(entry.getKey(), entry.getValue().getMeasurementAt(i, j));
                    }
                    dataset.setPluginSource(this.core.getPluginRecord());

                    engine.sumbitDataset(dataset);
                    count++;
                }
            }


        }
    }

    @Override
    public void killTask(IPluginRuntime engine) {
    }
}
