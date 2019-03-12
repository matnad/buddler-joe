package net.packets.login_logout;

import net.*;
import net.playerhandling.ClientThread;
import net.playerhandling.Player;
import net.playerhandling.ServerPlayerList;
import net.packets.Packet;

import java.util.StringJoiner;

public class PacketLogin extends Packet {


    private String username;
    /**
     * Login Packet which gets sent first by the client. Validates the Login package, checks whether the
     * Username is already taken and if not adds the player to the ServerPlayerList. Also creates instance
     * of a player with the needed information to forward to the ServerPlayerList.
     * @param clientId of the player to be added to the Player instance
     * @param data username to create the player
     */

    public PacketLogin(int clientId, String data) {
        super(PacketTypes.LOGIN);
        setData(data);
        setClientId(clientId);
        //Split here to class variables
        username = getData();
        validate();
    }

    public void validate(){
        if(username == null){
            addError("No username found.");
            return;
        }
        if(username.length() > 30){
            addError("Username to long. Maximum is 30 Characters.");
        }else if(username.length() < 4){
            addError("Username to short. Minimum is 4 Characters.");
        }
        isExtendedAscii(username);
    }

    public void processData(){
        String status;
        if(hasErrors()){
            StringJoiner statusJ = new StringJoiner("\n","ERRORS:","");
            for (String error : getErrors()) {
                statusJ.add(error);
            }
            status = statusJ.toString();
        }else{
            Player player = new Player(username,getClientId());
            status = ServerLogic.getPlayerList().addPlayer(player);
        }
        PacketLoginStatus p = new PacketLoginStatus(status);
        p.sendToClient(getClientId());
    }


}
