package net.packets.lobby;

import net.packets.Packet;

public class PacketJoinLobby extends Packet {


    /**
     * A packed which is send from the client to the Server once
     * he has chosen a lobby to join. Server should then move the client in
     * the chosen lobby
     * @param clientId of the player to be added to specified lobby
     * @param data lobbyId of the chosen lobby
     */
    public PacketJoinLobby(int clientId, String data) {
        super(PacketTypes.JOIN_LOBBY);
        setData(data);
        setClientId(clientId);
        validate();
    }


    @Override
    public void validate() {

    }

    @Override
    public void processData() {

    }
}
