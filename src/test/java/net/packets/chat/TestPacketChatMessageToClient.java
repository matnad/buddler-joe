package net.packets.chat;

import org.junit.Assert;
import org.junit.Test;

public class TestPacketChatMessageToClient {

  @Test
  public void checkStatusIsNotAsciiClient() {
    PacketChatMessageToClient packet = new PacketChatMessageToClient("║");
    Assert.assertEquals(
        "ERRORS: Invalid characters, only extended ASCII.", packet.createErrorMessage());
  }

  @Test
  public void checkStatusIsAsciiClient() {
    PacketChatMessageToClient packet = new PacketChatMessageToClient("ABC");
    Assert.assertEquals("ERRORS: ", packet.createErrorMessage());
  }

  @Test
  public void checkStatusIsNotAsciiServer() {
    PacketChatMessageToClient packet = new PacketChatMessageToClient(1, "║");
    Assert.assertEquals(
        "ERRORS: Invalid characters, only extended ASCII.", packet.createErrorMessage());
  }

  @Test
  public void checkStatusIsAsciiServer() {
    PacketChatMessageToClient packet = new PacketChatMessageToClient(1, "ABCD");
    Assert.assertEquals("ERRORS: ", packet.createErrorMessage());
  }

  @Test
  public void checkStatusIsNullClient() {
    PacketChatMessageToClient packet = new PacketChatMessageToClient(null);
    Assert.assertEquals("ERRORS: No Message found.", packet.createErrorMessage());
  }

    @Test
    public void checkStatusIsNullServer() {
        PacketChatMessageToClient packet = new PacketChatMessageToClient(1,null);
        Assert.assertEquals("ERRORS: No Message found.", packet.createErrorMessage());
    }

  @Test
  public void checkStatusIsTooLong() {
    PacketChatMessageToClient packet =
        new PacketChatMessageToClient(
            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
    Assert.assertEquals(
        "ERRORS: Message to long. Maximum is 100 Characters.", packet.createErrorMessage());
  }
}
