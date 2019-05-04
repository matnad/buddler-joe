package net.packets.name;

import net.ServerLogic;
import net.playerhandling.ServerPlayer;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class TestPacketSetName {
  @Test
  public void checkNoUsername() {
    PacketSetName p = new PacketSetName(1, null);
    Assert.assertEquals("ERRORS: No username found.", p.createErrorMessage());
  }

  @Test
  public void checkUsernameTooShort() {
    PacketSetName p = new PacketSetName(1, "ABC");
    Assert.assertEquals("ERRORS: Username too short. Minimum is 4 Characters.", p.createErrorMessage());
  }

  @Test
  public void checkUsernameTooLong() {
    PacketSetName p = new PacketSetName("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
    p.processData();
    Assert.assertEquals("ERRORS: Username too long. Maximum is 30 Characters.", p.createErrorMessage());
  }

    @Test
    public void checkUsernameIsInList() {
        ServerLogic serverLogic = Mockito.spy(ServerLogic.class);
        ServerPlayer player = new ServerPlayer("Peter", 1);
        ServerLogic.getPlayerList().addPlayer(player);
        ServerPlayer player2 = new ServerPlayer("Peter2", 2);
        ServerLogic.getPlayerList().addPlayer(player2);
        PacketSetName p = new PacketSetName(2, "Peter");
        p.processData();
        Assert.assertEquals("Peter_1", ServerLogic.getPlayerList().getUsername(2));
    }

    @Test
    public void checkUsernameIsNotInList() {
        ServerLogic serverLogic = Mockito.spy(ServerLogic.class);
        ServerPlayer player = new ServerPlayer("Hans", 2);
        ServerLogic.getPlayerList().addPlayer(player);
        PacketSetName p = new PacketSetName(2, "Peter");
        p.processData();
        Assert.assertEquals("Peter", ServerLogic.getPlayerList().getUsername(2));
    }
}
