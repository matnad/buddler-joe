package net.packets.gamestatus;

import org.junit.Assert;
import org.junit.Test;

public class TestPacketHistory {
    @Test
    public void correctPacketSever() {
        PacketHistory p = new PacketHistory(1, "TestHistory");
        Assert.assertFalse(p.hasErrors());
    }

    @Test
    public void correctPacketClient() {
        PacketHistory p = new PacketHistory("TestHistory");
        Assert.assertFalse(p.hasErrors());
    }

}
