package net.packets.lobby;

import net.packets.Packet;

public class PacketLeaveLobbyStatus extends Packet {

    String status;

    public PacketLeaveLobbyStatus(int clientId, String data) {
        //Server builds
        super(PacketTypes.LEAVE_LOBBY_STATUS);
        setClientId(clientId);
        setData(data);
        status = getData();
        validate();
    }

    public PacketLeaveLobbyStatus(String data) {
        //Client builds
        super(PacketTypes.LEAVE_LOBBY_STATUS);
        setData(data);
        status = getData();
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
        if(hasErrors()){//Errors on Client
            System.out.println(createErrorMessage());
        }else if(status.startsWith("OK")){
            System.out.println("Successfully left lobby");
        }else{//Errors on Server
            System.out.println(status);
        }
    }
}
