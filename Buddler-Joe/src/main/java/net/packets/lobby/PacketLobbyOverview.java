package net.packets.lobby;

import net.ServerLogic;
import net.packets.Packet;

public class PacketLobbyOverview extends Packet {

    String[] in;
    /**
     * A packed which is sent to the client before joining a lobby.
     * It should contain information to all open lobbys that are available
     * on the server and not full. (Maximum 10)
     */

    public PacketLobbyOverview(String data) {
        //Client receives
        super(PacketTypes.LOBBY_OVERVIEW);
        //System.out.print(data);
        in = data.split("║");
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
        System.out.println("-------------------------------------");
        System.out.println("Available Lobbies:");
        for (String s : in) {
            System.out.println(s);
        }
        System.out.println("-------------------------------------");
    }
}
