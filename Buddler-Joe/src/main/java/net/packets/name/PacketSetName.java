package net.packets.name;

import net.ClientLogic;
import net.ServerLogic;
import net.packets.Packet;
import net.packets.login_logout.PacketLoginStatus;

public class PacketSetName extends Packet {

    private int clientId;
    private String data;
    private String username;

    public PacketSetName(int clientId, String data) {
        super(PacketTypes.SET_NAME);
        setData(data);
        setClientId(clientId);
        username = getData();
        validate();
    }


    @Override
    public void validate() {
        checkUsername(username);
        if(ServerLogic.getPlayerList().isUsernameInList(username)){
            addError("Username already taken");
        }
    }

    @Override
    public void processData() {
        String status="";
        if(hasErrors()){
            status = createErrorMessage();
        }else{
            try {
                ServerLogic.getPlayerList().getPlayer(getClientId()).setUsername(username);
                status = "Successfully changed the name to: " + username;
            } catch (NullPointerException e){
                status = "Player not logged in";
            }
        }
        PacketSetNameStatus p = new PacketSetNameStatus(getClientId(),status);
        p.sendToClient(getClientId());
    }
}
