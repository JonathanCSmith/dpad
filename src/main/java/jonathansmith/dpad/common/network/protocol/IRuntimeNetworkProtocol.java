package jonathansmith.dpad.common.network.protocol;

import jonathansmith.dpad.common.network.packet.play.dataset.DatasetAdministrationPacket;
import jonathansmith.dpad.common.network.packet.play.experiment.ExperimentAdministrationPacket;

/**
 * Created by Jon on 22/05/2014.
 * <p/>
 * Interface for runtime network protocols
 */
public interface IRuntimeNetworkProtocol extends INetworkProtocol {

    /**
     * Handle a keep alive packet. Common to both client and server
     */
    void handleKeepAlive();

    /**
     * Method for handling experiment administration packets
     *
     * @param experimentAdministrationPacket
     */
    void handleExperimentAdministration(ExperimentAdministrationPacket experimentAdministrationPacket);

    void handleDatasetAdministrationPacket(DatasetAdministrationPacket datasetAdministrationPacket);
}
