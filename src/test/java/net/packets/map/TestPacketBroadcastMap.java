package net.packets.map;

import game.Game;
import game.map.ClientMap;
import game.map.ServerMap;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class TestPacketBroadcastMap {

    @Test
    public void checkIncompleteMapData() {
        PacketBroadcastMap p = new PacketBroadcastMap("test");
        Assert.assertEquals("ERRORS: Map data is incomplete.", p.createErrorMessage());
    }

    @Test
    public void checkSeedNotInt() {
        PacketBroadcastMap p = new PacketBroadcastMap("testInt║test");
        Assert.assertEquals("ERRORS: Invalid map seed. Wrong map format: 29 test", p.createErrorMessage());
    }

    @Test
    public void checkCorrectMapNotExisting() {
        Game game = Mockito.spy(Game.class);
        ServerMap serverMap = new ServerMap("l", 1);
        PacketBroadcastMap p = new PacketBroadcastMap("1║" + serverMap.toPacketString());
        p.processData();
        Assert.assertEquals(new ClientMap("l",1).toString(), Game.getMap().toString());
    }

}
