package net.packets.life;

import game.NetPlayerMaster;
import net.ServerLogic;
import net.lobbyhandling.Lobby;
import net.playerhandling.ServerPlayer;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class TestPacketLifeStatus {

  @Test
  public void checkDataNull() {
    PacketLifeStatus p = new PacketLifeStatus(null);
    Assert.assertEquals("ERRORS: Data is null.", p.createErrorMessage());
  }

  @Test
  public void checkCorrectData() {
    PacketLifeStatus p = new PacketLifeStatus(1, 1);
    Assert.assertEquals("1║1", p.getData());
  }

  @Test
  public void checkInvalidNumberOfArguments() {
    PacketLifeStatus p = new PacketLifeStatus(1, "TestData");
    p.processData();
    Assert.assertEquals("ERRORS: Invalid number of arguments", p.createErrorMessage());
  }

  @Test
  public void checkInvalidPlayerId() {
    PacketLifeStatus p = new PacketLifeStatus("ABS║Test");
    Assert.assertEquals("ERRORS: Invalid playerId or life status", p.createErrorMessage());
  }

  @Test
  public void checkCantSetLifeToNumber() {
    PacketLifeStatus p = new PacketLifeStatus("4║1");
    Assert.assertEquals(
        "ERRORS: Invalid life status or invalid playerId or player is not in a lobby",
        p.createErrorMessage());
  }

  //  @Test
  //  public void checkClientGetsPacket() {
  //    NetPlayerMaster netPlayerMaster = Mockito.spy(NetPlayerMaster.class);
  //    netPlayerMaster.addPlayer(1, "Testuser");
  //    PacketLifeStatus p = new PacketLifeStatus("1║1");
  //    p.processData();
  //    Assert.assertEquals(1, netPlayerMaster.getNetPlayerById(1).getCurrentLives());
  //  }
  //
  //  @Test
  //  public void checkServerGetsPacket() {
  //    ServerLogic serverLogic = Mockito.spy(ServerLogic.class);
  //    Lobby lobby = new Lobby("TestLobby", 1, "l");
  //    ServerPlayer player = new ServerPlayer("TestUser", 1);
  //    serverLogic.getLobbyList().addLobby(lobby);
  //    serverLogic.getLobbyList().getLobby(1).addPlayer(player);
  //    PacketLifeStatus p = new PacketLifeStatus(1, "1║1");
  //    p.processData();
  //    Assert.assertEquals(1, serverLogic.getLobbyForClient(1).getLobbyPlayers().get(1));
  //  }
}
