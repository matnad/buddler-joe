package net.packets.name;

import net.ServerLogic;
import net.packets.Packet;

public class PacketSetName extends Packet {

    private String username;

    /**
     * Constructor to be called by the server to set a username for a corresponding player
     * @param clientId The clientId of the player that wants to set their username
     * @param data The username the player would like to set
     */

    public PacketSetName(int clientId, String data) {
        super(PacketTypes.SET_NAME);
        setData(data);
        setClientId(clientId);
        username = getData();
        validate();
    }

    /**
     * Constructor to be called by the client to create a setName packet and then pass it to the server
     * @param username The username the player would like to set for himself
     */

    public PacketSetName(String username) {
        super(PacketTypes.SET_NAME);
        setData(username);
        this.username = username;
        validate();
    }

    /**
     * Implementation of the abstract validate method to check whether the data is in fact a username or not
     * Calls the checkUsername method to validate the username. Adds errors to the errorList if there are any
     */

    @Override
    public void validate() {
        checkUsername(username);
    }

    /**
     * Implementation of the abstract processData method to be called by the server which sets the new username
     * Created a String status that returns that status of the name change. If there have occurred any errors, these
     * get turned into an error message, then the method checks whether the username is already in the playerList.
     * If yes then it starts a counter to try as long as it takes to set the username in the list until it is unique.
     * It then sets the changed username to the List and created a status message to be sent to the player.
     * If no then it sets the username right away and returns a successful status message.
     * It then creates a PacketSetName class instance and returns it to the player.
     */

    @Override
    public void processData() {
        String status;
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
        }
        PacketSetNameStatus p = new PacketSetNameStatus(getClientId(), status);
        p.sendToClient(getClientId());
    }
}
