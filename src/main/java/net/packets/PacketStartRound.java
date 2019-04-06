package net.packets;

import game.Game;
import game.stages.InLobby;

import static game.Game.Stage.*;

public class PacketStartRound  extends Packet {

    public PacketStartRound(int clientId) {
        // server builds
        super(PacketTypes.START);
        setClientId(clientId);
        validate();
    }

    public PacketStartRound() {
        // client builds
        super(PacketTypes.START);
        validate();
    }

    @Override
    public void validate() {
        // No data to validate since it is a Empty Packet
    }

    @Override
    public void processData() {
        InLobby.done();
        Game.addActiveStage(PLAYING);
        Game.removeActiveStage(INLOBBBY);
        InLobby.done();
    }
}
