package net.packets.name;

import net.ServerLogic;
import net.packets.Packet;


public class PacketGetName extends Packet {

    private String player;
    private int playerId;


    public PacketGetName(int clientId, String playerId) {
        super(PacketTypes.GET_NAME);
        setClientId(clientId);
        setData(playerId);
        this.player = playerId;
        validate();
    }

    public PacketGetName(String data){
        super(PacketTypes.GET_NAME);
        setData(data);
        this.player = data;
        validate();
    }

    @Override
    public void validate() {
        if(this.player != null){
            if(!isInt(player)){
                addError("The clientId is not a number");
                return;
            } else {
                try {
                    if (!ServerLogic.getPlayerList().isPlayerIdInList(Integer.parseInt(player))) {
                        addError("Player is not on the server");
                    }
                } catch (NumberFormatException nfe){
                    addError("The clientId is not a number");
                }
            }
        }
    }

    @Override
    public void processData() {
        String playerName;
        if(hasErrors()){
            playerName = createErrorMessage();
        }else{
            playerName = ServerLogic.getPlayerList().getUsername(Integer.parseInt(player));
            if (playerName != null){
                playerName = "OK " + playerName;
            } else {
                playerName = "There is no player with this player Id";
            }
        }
        PacketSendName sendName = new PacketSendName(getClientId(),playerName);
        sendName.sendToClient(getClientId());
    }
}
