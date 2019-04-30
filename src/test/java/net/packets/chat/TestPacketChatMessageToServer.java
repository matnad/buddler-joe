package net.packets.chat;

import org.junit.Assert;
import org.junit.Test;

public class TestPacketChatMessageToServer {
  @Test
  public void checkNullMessage() {
    PacketChatMessageToServer p = new PacketChatMessageToServer(null);
    Assert.assertEquals("ERRORS: There is no message.", p.createErrorMessage());
  }

  @Test
  public void checkStatusIsTooLong() {
    PacketChatMessageToServer packet =
        new PacketChatMessageToServer(
            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                + "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
                + "aaaaaaaaaaaaaaaaaa");
    Assert.assertEquals(
        "ERRORS: Message to long. Maximum is 100 Characters.", packet.createErrorMessage());
  }

    @Test
    public void checkStatusIsNotAsciiClient() {
        PacketChatMessageToServer packet = new PacketChatMessageToServer("ඥ");
        Assert.assertEquals(
                "ERRORS: Invalid characters, only extended ASCII.", packet.createErrorMessage());
    }
    @Test
    public void checkStatusIsNotAsciiServer() {
        PacketChatMessageToServer packet = new PacketChatMessageToServer(1,"ඥ║1");
        Assert.assertEquals(
                "ERRORS: Invalid characters, only extended ASCII.", packet.createErrorMessage());
    }

    @Test
    public void checkStatusIsTooShortServer() {
        PacketChatMessageToServer packet = new PacketChatMessageToServer(1,"ඥ");
        Assert.assertEquals(
                "ERRORS: Invalid Input.", packet.createErrorMessage());
    }
}
