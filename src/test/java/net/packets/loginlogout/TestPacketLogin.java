package net.packets.loginlogout;

import net.ServerLogic;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class TestPacketLogin {
  @Test
  public void checkUsernameIsNull() {
    ServerLogic serverLogic = Mockito.spy(ServerLogic.class);
    PacketLogin p = new PacketLogin(null);
    p.processData();
    Assert.assertEquals("ERRORS: There is no username. No username found.", p.createErrorMessage());
  }

  @Test
  public void checkUsernameIsNotAscii() {
    ServerLogic serverLogic = Mockito.spy(ServerLogic.class);
    PacketLogin p = new PacketLogin(1, "ඥ");
    p.processData();
    Assert.assertEquals(
        "ERRORS: Username too short. Minimum is 4 Characters.", p.createErrorMessage());
  }

  @Test
  public void checkEveryThingWorksWithLogin() {
    ServerLogic serverLogic = Mockito.spy(ServerLogic.class);
    PacketLogin p = new PacketLogin("Peter Gryffin");
    p.sendToServer();
    Assert.assertEquals("Peter Gryffin", p.getData());
  }
}
