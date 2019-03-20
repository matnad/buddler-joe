package net.packets.lobby;

import net.playerhandling.ServerPlayerList;
import net.ServerLogic;
import net.packets.Packet;

public class PacketCreateLobbyStatus extends Packet{
    private String status;
    private String[] in;
    /**
     * Package to inform the client over the result of the lobby-creation attempt
     * @param clientId to find the player in the list
     * @param data a String with "code;lobbyId"
     */

    public PacketCreateLobbyStatus(int clientId, String data) {
        //Server builds
        super(Packet.PacketTypes.CREATE_LOBBY_STATUS);
        setData(data);
        setClientId(clientId);
        validate();
    }

    public PacketCreateLobbyStatus(String data) {
        //client builds
        super(Packet.PacketTypes.CREATE_LOBBY_STATUS);
        setData(data);
        status = getData();
        in = data.split("║");
        validate();
    }


    @Override
    public void validate() {
        if(status != null) {
            isExtendedAscii(status);
        }else{
            addError("No Status found.");
        }
    }

    @Override
    public void processData() {
        if(hasErrors()){
            System.out.println(createErrorMessage());
        }else if(status.startsWith("OK")){
            System.out.println("Lobby-Creation Successful");
        }else{
            for (String s : in) {
                System.out.println(s);
            }
        }
    }
}
