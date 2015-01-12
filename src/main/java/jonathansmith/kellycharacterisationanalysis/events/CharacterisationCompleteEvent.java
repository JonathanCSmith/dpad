package jonathansmith.kellycharacterisationanalysis.events;

import java.util.LinkedList;

import jonathansmith.dpad.api.plugins.events.Event;

import jonathansmith.kellycharacterisationanalysis.data.DeconvolutedData;

/**
 * Created by Jon on 29/09/2014.
 */
public class CharacterisationCompleteEvent extends Event {

    private final LinkedList<DeconvolutedData> data;

    public CharacterisationCompleteEvent(LinkedList<DeconvolutedData> data) {
        this.data = data;
    }

    public LinkedList<DeconvolutedData> getRelativeStrengths() {
        return this.data;
    }
}
