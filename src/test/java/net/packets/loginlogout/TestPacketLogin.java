package net.packets.loginlogout;

import org.junit.Assert;
import org.junit.Test;

public class TestPacketLogin {
    @Test
    public void checkUsernameIsNull() {
        PacketLogin p = new PacketLogin(null);
        p.processData();
        Assert.assertEquals("ERRORS: There is no username. No username found. No server.", p.createErrorMessage());
    }

    @Test
    public void checkUsernameIsNotAscii() {
        PacketLogin p = new PacketLogin(1, "ඥ");
        p.processData();
        Assert.assertEquals("ERRORS: Username too short. Minimum is 4 Characters. No server.", p.createErrorMessage());
    }
}
