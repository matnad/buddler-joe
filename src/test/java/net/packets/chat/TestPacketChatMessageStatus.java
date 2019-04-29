package net.packets.chat;

import org.junit.Assert;
import org.junit.Test;

public class TestPacketChatMessageStatus {

    @Test
    public void checkStatusIsNotAsciiClient() {
        PacketChatMessageStatus packet = new PacketChatMessageStatus("║");
        Assert.assertEquals("ERRORS: Invalid characters, only extended ASCII.", packet.createErrorMessage());
    }

    @Test
    public void checkStatusIsAsciiClient() {
        PacketChatMessageStatus packet = new PacketChatMessageStatus("ABC");
        Assert.assertEquals("ERRORS: ", packet.createErrorMessage());
    }

    @Test
    public void checkStatusIsNotAsciiServer() {
        PacketChatMessageStatus packet = new PacketChatMessageStatus(1,"║");
        Assert.assertEquals("ERRORS: Invalid characters, only extended ASCII.", packet.createErrorMessage());
    }

    @Test
    public void checkStatusIsAsciiServer() {
        PacketChatMessageStatus packet = new PacketChatMessageStatus(1,"ABCD");
        Assert.assertEquals("ERRORS: ", packet.createErrorMessage());
    }

    @Test
    public void checkStatusIsNullClient() {
        PacketChatMessageStatus packet = new PacketChatMessageStatus(null);
        Assert.assertEquals("ERRORS: No Status found.", packet.createErrorMessage());
    }
}
