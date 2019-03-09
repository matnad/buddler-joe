package net.packets;

import net.ServerLogic;
import net.ServerPlayerList;

public class PacketGetName extends Packet{

    private int clientId;
    private String username;
    private int getClientId;
    private ServerPlayerList playerList;

    public PacketGetName(int clientId, String data) {
        super("GETNM");
        this.clientId = clientId;
        if(!validate(data)){
            return;
        }
        this.playerList = ServerLogic.getPlayerList();
        String getClientName = playerList.searchName(getClientId);

        playerList.searchThread(clientId).sendToClient(getClientName);

    }

    /**
     * Method to validate the data which is forwarded to the package to ensure its safety.
     *
     * @param data The data from the Buffered reader.
     * @return True or false to determine the further action of the class.
     */

    //TODO: Exceptions!


    public boolean validate(String data){
        //TODO: Write the validation method

        getClientId = Integer.parseInt(data);
        return true;
    }

    public void processData(String data){

    }

    @Override
    public String getData() {
        return this.toString();
    }

    @Override
    public String toString() {
        return "PacketGetName{" +
                "clientId=" + clientId +
                ", username='" + username + '\'' +
                ", getClientId=" + getClientId +
                '}';
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public int getGetClientId() {
        return getClientId;
    }

    public void setGetClientId(int getClientId) {
        this.getClientId = getClientId;
    }

    public ServerPlayerList getPlayerList() {
        return playerList;
    }

    public void setPlayerList(ServerPlayerList playerList) {
        this.playerList = playerList;
    }
}
