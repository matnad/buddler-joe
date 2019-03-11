package net.packets.login_logout;

import net.*;
import net.playerhandling.ClientThread;
import net.playerhandling.Player;
import net.playerhandling.ServerPlayerList;
import net.packets.Packet;

public class PacketLogin extends Packet {

    private Player player;
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
            setPacketId(PacketTypes.INVALID);
            return;
        }
        this.playerList = ServerLogic.getPlayerList();
        setClientId(clientId);
        setData(data);
        processData();
    }

    public boolean validate(){
        return true;
    }

    public void processData(){
        this.player = new Player(getData(), getClientId(), thread, 0);
        int result = playerList.addPlayer(player);
        PacketLoginStatus status = new PacketLoginStatus(getClientId(), Integer.toString(result));
        if(!addPlayerToSentToPlayer(getClientId())){
            setPacketId(PacketTypes.INVALID);
            return;
        }
        status.sendToClient(getClientId());
    }

    @Override
    public String toString() {
        return "PacketLogin{" +
                "player=" + player +
                ", clientId=" + getClientId() +
                ", playerList=" + playerList +
                ", thread=" + thread +
                '}';
    }

    @Override
    public Packet getPackage() {
        return null;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
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
