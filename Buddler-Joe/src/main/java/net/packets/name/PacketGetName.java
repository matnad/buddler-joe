package net.packets.name;

import net.ServerLogic;
import net.packets.Packet;


public class PacketGetName extends Packet {

    private String playerId;

    /**
     * Constructor when the package gets created from the server side
     * @param clientId The clientId of the client requesting an username
     * @param playerId The Id of the requested player to be looked up in the playerList
     */

    public PacketGetName(int clientId, String playerId) {
        super(PacketTypes.GET_NAME);
        setClientId(clientId);
        setData(playerId);
        this.playerId = playerId;
        validate();
    }

    /**
     * Constructor when the package gets created from the client side to be sent to the Server
     * @param data Contains the playerId of the player to be looked up
     */

    public PacketGetName(String data){
        super(PacketTypes.GET_NAME);
        setData(data);
        this.playerId = data;
        validate();
    }

    /**
     * Implementation of the abstract method validate to check the data
     * Checks whether the String is an Integer with the isInt method, then checks whether the player is on the
     * Server or not an adds Errors to the ErrorList if any occurs.
     */

    @Override
    public void validate() {
        if(this.playerId != null){
            if(!isInt(playerId)){
                addError("The clientId is not a number");
                return;
            } else {
                try {
                    if (!ServerLogic.getPlayerList().isClientIdInList(Integer.parseInt(playerId))) {
                        addError("Player is not on the server");
                    }
                } catch (NumberFormatException nfe){
                    addError("The clientId is not a number");
                }
            }
        }
    }

    /**
     * Implementation of the abstract processData method to process the data from the server side
     * Creates a String which gets passed on to a PacketSendName class and then sent to client.
     * The String either contains OK and the username of the searched for player or an error message
     * to be displayed to the client.
     */

    @Override
    public void processData() {
        String playerName;
        if(hasErrors()){
            playerName = createErrorMessage();
        }else{
            playerName = ServerLogic.getPlayerList().getUsername(Integer.parseInt(playerId));
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
