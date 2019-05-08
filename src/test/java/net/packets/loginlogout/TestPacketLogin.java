package net.packets.loginlogout;

import game.Game;
import net.ServerLogic;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestPacketLogin {

  public static final Logger logger = LoggerFactory.getLogger(TestPacketLogin.class);

  @Test
  public void checkUsernameIsNull() {
    ServerLogic serverLogic = Mockito.spy(ServerLogic.class);
    PacketLogin p = new PacketLogin(null);
    p.processData();
    Assert.assertEquals("ERRORS: No username found.", p.createErrorMessage());
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
    String currUsername = "Joe Buddler";
    try {
      currUsername = Game.getSettings().getUsername();
    } catch (NullPointerException e) {
      logger.info("No Settings File existing.");
    }
    ServerLogic serverLogic = Mockito.spy(ServerLogic.class);
    PacketLogin p = new PacketLogin("Peter Gryffin");
    p.sendToServer();
    Assert.assertEquals("Peter Gryffin", p.getData());
    try {
      Game.getSettings().setUsername(currUsername);
    } catch (NullPointerException e) {
      logger.info("No Settings File existing.");
    }
  }
}
