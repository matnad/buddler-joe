package net.packets.login_logout;

import net.packets.Packet;
import net.playerhandling.Player;
import net.ServerLogic;
import net.packets.lobby.PacketLeaveLobby;
import net.packets.chat.PacketChatMessageToServer;

public class PacketDisconnect extends Packet {


    /**
     * Disconnect package to disconnect a player from the server.
     * @param clientId of the player who disconnect.
     */
    public PacketDisconnect(int clientId) {
        super(PacketTypes.DISCONNECT);
        setClientId(clientId);
        validate();
    }
    public PacketDisconnect(){
        super(PacketTypes.DISCONNECT);
        validate();
    }

    @Override
    public void validate() {
    }

    @Override
    public void processData() {
        ServerLogic.removePlayer(getClientId());
    }
}
