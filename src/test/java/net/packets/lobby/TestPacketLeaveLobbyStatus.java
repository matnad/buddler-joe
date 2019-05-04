package net.packets.lobby;

import game.Game;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class TestPacketLeaveLobbyStatus {
    @Test
    public void checkStatusNull() {
        PacketLeaveLobbyStatus p = new PacketLeaveLobbyStatus(null);
        Assert.assertEquals("ERRORS: No Status found.", p.createErrorMessage());
    }

    @Test
    public void checkStatusNotAscii() {
        PacketLeaveLobbyStatus p = new PacketLeaveLobbyStatus(1,"ਠ");
        p.processData();
        Assert.assertEquals("ERRORS: Invalid characters, only extended ASCII.", p.createErrorMessage());
    }

    @Test
    public void checkStatusOkInLobby() {
        Game game = Mockito.spy(Game.class);
        Game.addActiveStage(Game.Stage.INLOBBBY);
        PacketLeaveLobbyStatus p = new PacketLeaveLobbyStatus("OK");
        p.processData();
        Assert.assertEquals("ERRORS: ", p.createErrorMessage());
    }

    @Test
    public void checkStatusOkInGame() {
        Game game = Mockito.spy(Game.class);
        Game.addActiveStage(Game.Stage.PLAYING);
        PacketLeaveLobbyStatus p = new PacketLeaveLobbyStatus("OK");
        p.processData();
        Assert.assertEquals("ERRORS: ", p.createErrorMessage());
    }
}
