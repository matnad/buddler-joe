package net.packets;

import net.*;

public class PacketLogin extends Packet{

    private Player player;
    private int clientId;
    private ServerPlayerList playerList;
    private ClientThread thread;


    /**
     * Login Packet which gets sent first by the client. Validates the Login package, checks whether the
     * Username is already taken and if not adds the player to the ServerPlayerList. Also creates instance
     * of a player with the needed information to forward to the ServerPlayerList.
     * @param clientId of the player to be added to the Player instance
     * @param data username to create the player
     */

    public PacketLogin(int clientId, String data) {
        super(PacketTypes.LOGIN);
        if(!validate()){
            setPacketId(PacketTypes.LOGIN);
            return;
        }
        this.clientId = clientId;
        this.playerList = ServerLogic.getPlayerList();
        processData();
    }

    public boolean validate(){
        return true;
    }

    public void processData(){
        this.player = new Player(getData(), clientId, thread, 0);
        int result = playerList.addPlayer(player);
        PacketLoginStatus status = new PacketLoginStatus(clientId, Integer.toString(result));
        status.sendToClient(clientId);
    }

    @Override
    public String toString() {
        return "PacketLogin{" +
                "player=" + player +
                ", clientId=" + clientId +
                ", playerList=" + playerList +
                ", thread=" + thread +
                '}';
    }

    @Override
    public String getData() {
        return this.toString();
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    @Override
    public int getClientId() {
        return clientId;
    }

    @Override
    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public ServerPlayerList getPlayerList() {
        return playerList;
    }

    public void setPlayerList(ServerPlayerList playerList) {
        this.playerList = playerList;
    }

    public ClientThread getThread() {
        return thread;
    }

    public void setThread(ClientThread thread) {
        this.thread = thread;
    }
}
