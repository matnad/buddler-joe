package net.packets.items;

import net.ServerLogic;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

public class TestPacketItemUsed {

  @Mock ServerLogic mock;

  @Test
  public void checkItemIdIntClient() {
    PacketItemUsed packetItemUsed = new PacketItemUsed(12);
    Assert.assertEquals("12", packetItemUsed.getData());
  }

  @Test
  public void checkItemIdNotIntServer() {
    ServerLogic serverLogic = Mockito.spy(ServerLogic.class);
    PacketItemUsed packetItemUsed = new PacketItemUsed(1, "abc");
    Assert.assertEquals(
        "ERRORS: Invalid item id. Client is not in a lobby.", packetItemUsed.createErrorMessage());
  }

  @Test
  public void checkItemIdNotInALobby() {
    ServerLogic serverLogic = Mockito.spy(ServerLogic.class);
    PacketItemUsed packetItemUsed = new PacketItemUsed(1, "12");
    Assert.assertEquals("ERRORS: Client is not in a lobby.", packetItemUsed.createErrorMessage());
  }

  //  @Test
  //  public void checkItemIdEverythingOk() {
  //    ServerLogic serverLogic = Mockito.spy(ServerLogic.class);
  //    ServerPlayer testPlayer = new ServerPlayer("TestPlayer", 1);
  //    serverLogic.getPlayerList().addPlayer(testPlayer);
  //    Lobby testLobby = new Lobby("TestLobby", 1, "small");
  //    testLobby.addPlayer(testPlayer);
  //    testPlayer.setCurLobbyId(1);
  //    serverLogic.getLobbyList().addLobby(testLobby);
  //    PacketItemUsed packetItemUsed = new PacketItemUsed(1, "12");
  //    Assert.assertEquals("ERRORS: ", packetItemUsed.createErrorMessage());
  //  }
}
