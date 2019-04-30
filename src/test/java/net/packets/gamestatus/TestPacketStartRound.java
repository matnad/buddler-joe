package net.packets.gamestatus;

import org.junit.Assert;
import org.junit.Test;

public class TestPacketStartRound {
    @Test
    public void checkPacketServer() {
        PacketStartRound p = new PacketStartRound(1);
        Assert.assertEquals(1, p.getClientId());
    }
}
