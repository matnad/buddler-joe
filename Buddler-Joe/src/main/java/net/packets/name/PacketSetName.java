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
    }

    @Override
    public void processData() {
        String status = "";
        if (hasErrors()) {
            status = createErrorMessage();
        } else {
            try {
                if (ServerLogic.getPlayerList().isUsernameInList(username)) {
                    int counter = 1;
                    String name = username;
                    while(ServerLogic.getPlayerList().isUsernameInList(name)) {
                        name = username + "_" + counter;
                        counter++;
                    }
                    ServerLogic.getPlayerList().getPlayer(getClientId()).setUsername(name);
                    status = "Changed to: " + name + ". Because your chosen name is already in use.";
                } else {
                    ServerLogic.getPlayerList().getPlayer(getClientId()).setUsername(username);
                    status = "Successfully changed the name to: " + username;
                }
            } catch (NullPointerException e) {
                status = "Player not logged in";
            }
            PacketSetNameStatus p = new PacketSetNameStatus(getClientId(), status);
            p.sendToClient(getClientId());
        }
    }
}
