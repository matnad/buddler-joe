package net.packets.lobby;

import net.ServerLogic;
import net.packets.Packet;

public class PacketLobbyOverview extends Packet {

    /**
     * A packed which is sent to the client before joining a lobby.
     * It should contain information to all open lobbys that are available
     * on the server and not full. (Maximum 10)
     */

    public PacketLobbyOverview(String data) {
        //Client receives
        super(PacketTypes.LOBBY_OVERVIEW);
        setData(data);
        validate();
    }

    public PacketLobbyOverview(int clientId, String data) {
        //server builds
        super(PacketTypes.LOBBY_OVERVIEW);
        setClientId(clientId);
        setData(data);
    }


    @Override
    public void validate() {
        if(getData() != null) {
            isExtendedAscii(getData());
        }else{
            addError("No data has been found");
        }
    }

    @Override
    public void processData() {
        System.out.println(getData());
    }
}
