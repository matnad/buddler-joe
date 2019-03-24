package net.packets.login_logout;

import net.packets.Packet;
import net.ServerLogic;

/**
 *  * Packet that gets send from the Client to the Server, to disconnect the player from the Server.
 *  * Packet-Code: DISCP
 *  * @author Moritz Würth
 */
public class PacketDisconnect extends Packet {


    /**
     * Constructor that is used by the Server to build the Packet.
     * @param clientId of the player who disconnect.
     */
    public PacketDisconnect(int clientId) {
        super(PacketTypes.DISCONNECT);
        setClientId(clientId);
        validate();
    }

    /**
     * Constructor that is used by the Client to build the Packet.
     */
    public PacketDisconnect(){
        super(PacketTypes.DISCONNECT);
        validate();
    }

    /**
     * Dummy method. Since there is no content to validate.
     */
    @Override
    public void validate() {
    }

    /**
     * Remove the player from the server and inform the other players in the lobby.
     */
    @Override
    public void processData() {
        ServerLogic.removePlayer(getClientId());
    }
}
