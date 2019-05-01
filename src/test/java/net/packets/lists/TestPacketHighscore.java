package net.packets.lists;

import org.junit.Assert;
import org.junit.Test;

public class TestPacketHighscore {

  @Test
  public void checkHighscoreNull() {
    PacketHighscore packetHighscore = new PacketHighscore(null);
    Assert.assertEquals("ERRORS: No Highscore found.", packetHighscore.createErrorMessage());
  }

  @Test
  public void checkShortHighscoreNotAscii() {
    PacketHighscore packetHighscore = new PacketHighscore("test║ॠ║test");
    Assert.assertEquals(
        "ERRORS: Invalid characters, only extended ASCII.", packetHighscore.createErrorMessage());
  }

  @Test
  public void checkLongHighscoreNotAscii() {
    PacketHighscore packetHighscore = new PacketHighscore("test║test║test║test║test║ॠ");
    Assert.assertEquals(
        "ERRORS: Invalid characters, only extended ASCII.", packetHighscore.createErrorMessage());
  }

  @Test
  public void checkSetClientIdCorrect() {
    PacketHighscore packetHighscore = new PacketHighscore(1);
    Assert.assertEquals(1, packetHighscore.getClientId());
  }

  @Test
  public void checkPacketCreationCorrectClient() {
    PacketHighscore packetHighscore = new PacketHighscore();
    Assert.assertEquals("ERRORS: ", packetHighscore.createErrorMessage());
  }

  @Test
  public void checkPacketProcessDataCorrectly() {
    PacketHighscore packetHighscore = new PacketHighscore("OK║test1║1║test2║2");
    packetHighscore.processData();
    Assert.assertEquals("test1", packetHighscore.getCatalog().get(0).getUsername());
  }
}
