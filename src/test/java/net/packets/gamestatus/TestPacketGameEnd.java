package net.packets.gamestatus;

import org.junit.Assert;
import org.junit.Test;

public class TestPacketGameEnd {

  @Test
  public void dataTooShort() {
    PacketGameEnd p = new PacketGameEnd("Test");
    Assert.assertTrue(p.hasErrors());
  }

  @Test
  public void dataTooLong() {
    PacketGameEnd p = new PacketGameEnd("Test║test║test");
    Assert.assertTrue(p.hasErrors());
  }

  @Test
  public void dataInvalidTimeFormatTrue() {
    PacketGameEnd p = new PacketGameEnd("Joe Buddler║WrongNr");
    Assert.assertTrue(p.hasErrors());
  }

  @Test
  public void correctPacket() {
    PacketGameEnd p = new PacketGameEnd("Joe Buddler║5L");
    Assert.assertTrue(p.hasErrors());
  }
}
