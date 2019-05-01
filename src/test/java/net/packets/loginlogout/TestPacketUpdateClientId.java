package net.packets.loginlogout;

import org.junit.Assert;
import org.junit.Test;

public class TestPacketUpdateClientId {

    @Test
    public void checkUsernameIsNull() {
        PacketUpdateClientId p = new PacketUpdateClientId(null);
        Assert.assertEquals("ERRORS: No Status found.", p.createErrorMessage());
    }

    @Test
    public void checkInvalidClientId() {
        PacketUpdateClientId p = new PacketUpdateClientId("Test");
        Assert.assertEquals("ERRORS: Invalid client ID for current player received from server. ID: Test", p.createErrorMessage());
    }

    @Test
    public void checkValidClientIdNotConnectedToTheServer() {
        PacketUpdateClientId p = new PacketUpdateClientId("1");
        p.processData();
        Assert.assertEquals("ERRORS: Not connected to the server.", p.createErrorMessage());
    }
}
