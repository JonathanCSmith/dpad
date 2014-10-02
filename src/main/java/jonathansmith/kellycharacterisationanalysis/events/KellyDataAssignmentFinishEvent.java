package jonathansmith.kellycharacterisationanalysis.events;

import java.util.LinkedList;

import jonathansmith.dpad.api.plugins.events.Event;

import jonathansmith.kellycharacterisationanalysis.data.KellyDeconvolutionMask;

/**
 * Created by Jon on 29/09/2014.
 */
public class KellyDataAssignmentFinishEvent extends Event {

    private final LinkedList<KellyDeconvolutionMask> ordererdMasks;

    public KellyDataAssignmentFinishEvent(LinkedList<KellyDeconvolutionMask> orderedMasks) {
        this.ordererdMasks = orderedMasks;
    }

    public LinkedList<KellyDeconvolutionMask> getOrdererdMasks() {
        return this.ordererdMasks;
    }
}
