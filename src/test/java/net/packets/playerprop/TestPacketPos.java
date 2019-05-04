package net.packets.playerprop;

import game.NetPlayerMaster;
import net.ServerLogic;
import net.playerhandling.ServerPlayer;
import org.joml.Vector2f;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class TestPacketPos {
  @Test
  public void checkDataNull() {
    PacketPos p = new PacketPos(null);
    Assert.assertEquals("ERRORS: No position data found.", p.createErrorMessage());
  }

  @Test
  public void checkDataIncorrectPosData() {
    PacketPos p = new PacketPos(1, "1.0║1.0║Error");
    Assert.assertEquals("ERRORS: Invalid position data.", p.createErrorMessage());
  }

  @Test
  public void checkDataIncorrectPosDataTooShort() {
    PacketPos p = new PacketPos(1, "1.0║1.0");
    p.processData();
    Assert.assertEquals("ERRORS: Invalid position data.", p.createErrorMessage());
  }

  @Test
  public void checkDataCorrect() {
    PacketPos p = new PacketPos(1f, 1f, 1f);
    Assert.assertEquals("1.0║1.0║1.0", p.getData());
  }

  @Test
  public void checkCorrectDataServer() {
    ServerLogic serverLogic = Mockito.spy(ServerLogic.class);
    ServerPlayer player = new ServerPlayer("TestPlayer", 1);
    serverLogic.getPlayerList().addPlayer(player);
    NetPlayerMaster netPlayerMaster = Mockito.spy(NetPlayerMaster.class);
    netPlayerMaster.addPlayer(1, "TestPlayer");
    PacketPos p = new PacketPos(1, "1.0║1.0║1.0");
    p.processData();
    Assert.assertEquals(new Vector2f(1, 1), serverLogic.getPlayerList().getPlayer(1).getPos2d());
  }
}
