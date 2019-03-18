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
        if(in[0].equals("OK")) { //the "OK" gets added in PacketCreatLobby.processData and PacketGetLobbies.processData
            System.out.println("-------------------------------------");
            System.out.println("Available Lobbies:");
            for (int i = 1; i < in.length; i++) {
                System.out.println(in[i]);
            }
            System.out.println("-------------------------------------");
        }else{
            System.out.println(in[0]);
        }
    }
}
