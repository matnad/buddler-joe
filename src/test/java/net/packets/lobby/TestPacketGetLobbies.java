package net.packets.lobby;

import net.ServerLogic;
import net.playerhandling.ServerPlayer;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class TestPacketGetLobbies {
  @Test
  public void checkNotLoggedIn() {
    PacketGetLobbies p = new PacketGetLobbies(2);
    p.processData();
    Assert.assertEquals("ERRORS: Not logged in yet.", p.createErrorMessage());
  }

  @Test
  public void checkEverythingWorksFine() {
    ServerLogic serverLogic = Mockito.spy(ServerLogic.class);
    ServerPlayer serverPlayer = new ServerPlayer("TestUser", 1);
    ServerLogic.getPlayerList().addPlayer(serverPlayer);
    PacketGetLobbies p = new PacketGetLobbies(1);
    p.processData();
    Assert.assertEquals("OK║No Lobbies online", p.getData());
  }

  @Test
  public void checkPacketBuildingClient() {
    PacketGetLobbies p = new PacketGetLobbies();
    Assert.assertEquals("ERRORS: ", p.createErrorMessage());
  }
}
