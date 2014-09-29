package jonathansmith.microplatetxtloader.events;

import java.io.File;
import java.util.LinkedList;

import jonathansmith.dpad.api.plugins.events.Event;

/**
 * Created by Jon on 29/09/2014.
 */
public class MicroplateLoaderFirstStageFinishEvent extends Event {

    private final LinkedList<File>    file_list;
    private final LinkedList<Integer> times;

    public MicroplateLoaderFirstStageFinishEvent(LinkedList<File> file_list, LinkedList<Integer> times) {
        this.file_list = file_list;
        this.times = times;
    }

    public LinkedList<File> getFiles() {
        return this.file_list;
    }

    public LinkedList<Integer> getTimes() {
        return this.times;
    }
}
