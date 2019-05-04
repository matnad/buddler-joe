package net.packets.name;

import game.Game;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class TestPacketSetNameStatus {
    @Test
    public void checkUsernameNull() {
        PacketSetNameStatus p = new PacketSetNameStatus(null);
        p.processData();
        Assert.assertEquals("ERRORS: No Status found.", p.createErrorMessage());
    }

    @Test
    public void checkUsernameNotAscii() {
        PacketSetNameStatus p = new PacketSetNameStatus(1, "੧੧੧੧");
        Assert.assertEquals("ERRORS: Invalid characters, only extended ASCII.", p.createErrorMessage());
    }

    @Test
    public void checkSuccessfullyChangedUsername() {
        Game game = Mockito.spy(Game.class);
        PacketSetNameStatus p = new PacketSetNameStatus("OK║Peter");
        p.processData();
        Assert.assertEquals("ERRORS: No game started.", p.createErrorMessage());
    }

    @Test
    public void checkChangedChangedUsername() {
        Game game = Mockito.spy(Game.class);
        game.loadSettings();
        Game.getSettings().setUsername("Test");
        PacketSetNameStatus p = new PacketSetNameStatus("CHANGED║Peter");
        p.processData();
        Assert.assertEquals("ERRORS: Not a real game!", p.createErrorMessage());
    }

    @Test
    public void checkErrorsReceived() {
        PacketSetNameStatus p = new PacketSetNameStatus("ERROR║Test Error");
        p.processData();
        Assert.assertEquals("ERRORS: Test Error", p.createErrorMessage());
    }
}
