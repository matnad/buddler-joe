package net.packets.playerprop;

import static net.lobbyhandling.TestServerLobbyList.logger;

import game.NetPlayerMaster;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class TestPacketDefeated {

    @Test
    public void checkNotAnInteger() {
        PacketDefeated p = new PacketDefeated("ABC");
        Assert.assertEquals("ERRORS: Client ID is not an integer.", p.createErrorMessage());
    }

    @Test
    public void checkClientIdNull() {
        PacketDefeated p = new PacketDefeated(null);
        Assert.assertEquals("ERRORS: Client ID is not an integer.", p.createErrorMessage());
    }

    @Test
    public void checkServerPlayerNotExisting() {
        NetPlayerMaster netPlayerMaster = Mockito.spy(NetPlayerMaster.class);
        PacketDefeated p = new PacketDefeated(1);
        p.processData();
        Assert.assertEquals("ERRORS: ServerPlayer not found.", p.createErrorMessage());
    }
}
