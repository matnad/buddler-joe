package net.packets.life;

import org.junit.Assert;
import org.junit.Test;

public class TestPacketLifeStatus {
    @Test
    public void checkEmptyLifeStatus() {
        PacketLifeStatus p = new PacketLifeStatus(null);
        Assert.assertEquals("ERRORS: Empty message.", p.createErrorMessage());
    }
    @Test
    public void checkIncorrectNumber() {
        PacketLifeStatus p = new PacketLifeStatus(1, "A");
        Assert.assertEquals("ERRORS: Invalid number.", p.createErrorMessage());
    }

    @Test
    public void checkCorrectPacket() {
        PacketLifeStatus p = new PacketLifeStatus(1, "1");
        Assert.assertEquals("ERRORS: ", p.createErrorMessage());
    }

    @Test
    public void checkNotConnectedToTheServer() {
        PacketLifeStatus p = new PacketLifeStatus(1, "1");
        p.processData();
        Assert.assertEquals("ERRORS: Failed to assign the data.", p.createErrorMessage());
    }
}
