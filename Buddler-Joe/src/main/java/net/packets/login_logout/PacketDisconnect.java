package net.packets.login_logout;

import net.packets.Packet;
import net.playerhandling.Player;
import net.ServerLogic;
import net.packets.lobby.PacketLeaveLobby;
import net.packets.chat.PacketChatMessageToServer;

public class PacketDisconnect extends Packet {

    /**
     * Disconnect package to disconnect a player from the server.
     * @param data data contains the username which is to be deleted from
     *             the ServerPlayerList. Calls the removePlayer function from
     *             the ServerPlayerList.
     */

    public PacketDisconnect(int clientId, String data) {
        super(PacketTypes.DISCONNECT);
        setClientId(clientId);
        setData(data);
        validate();
    }

    @Override
    public void validate() {

    }

    @Override
    public void processData() {
        Player client = ServerLogic.getPlayerList().getPlayer(getClientId());

        PacketChatMessageToServer sendMessage = new PacketChatMessageToServer("left the lobby");
        sendMessage.setClientId(client.getClientId());
        sendMessage.processData();

        PacketLeaveLobby leaveLobby = new PacketLeaveLobby(getClientId());
        leaveLobby.processData();


        ServerLogic.getPlayerList().removePlayer(getClientId());

//        PacketLeaveLobby leaveLobby = new PacketLeaveLobby(client.getClientId());
//        leaveLobby.processData();

//        ServerLogic.getPlayerList().removePlayer(getClientId());

    }
}
