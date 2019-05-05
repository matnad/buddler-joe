package net.packets.block;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestBlockDamage {

  public static final Logger logger = LoggerFactory.getLogger(TestBlockDamage.class);

  @Test
  public void correctPacketSever() {
    PacketBlockDamage p = new PacketBlockDamage(1, "3║3║0.5f");
    Assert.assertFalse(p.hasErrors());
  }

  @Test
  public void incorrectBlockXServer() {
    PacketBlockDamage p = new PacketBlockDamage(1, "a║3║0.5f");
    Assert.assertTrue(p.hasErrors());
  }

  @Test
  public void incorrectBlockYServer() {
    PacketBlockDamage p = new PacketBlockDamage(1, "1║a║0.5f");
    Assert.assertTrue(p.hasErrors());
  }

  @Test
  public void incorrectDamageDataServer() {
    PacketBlockDamage p = new PacketBlockDamage(1, "12║12║test");
    Assert.assertTrue(p.hasErrors());
  }

  @Test
  public void correctPacketClient() {
    PacketBlockDamage p = new PacketBlockDamage("1║3║3║0.5f");
    Assert.assertFalse(p.hasErrors());
  }

  @Test
  public void incorrectBlockXClient() {
    PacketBlockDamage p = new PacketBlockDamage("1║test║3║0.5f");
    Assert.assertTrue(p.hasErrors());
  }

  @Test
  public void incorrectBlockYClient() {
    PacketBlockDamage p = new PacketBlockDamage("1║3║test║0.5f");
    Assert.assertTrue(p.hasErrors());
  }

  @Test
  public void incorrectClientIdClient() {
    PacketBlockDamage p = new PacketBlockDamage("test║12║12║0.5f");
    Assert.assertTrue(p.hasErrors());
  }

  @Test
  public void incorrectDamageDataClient() {
    PacketBlockDamage p = new PacketBlockDamage("1║12║12║test");
    Assert.assertTrue(p.hasErrors());
  }

  @Test
  public void checkCorrectDataClient() {
    PacketBlockDamage p = new PacketBlockDamage(2, 3, 1f);
    Assert.assertEquals("2║3║1.0", p.getData());
  }

  @Test
  public void checkCorrectDataServer() {
    PacketBlockDamage p = new PacketBlockDamage(1, 1, 2, 1f);
    p.processData();
    Assert.assertEquals("1║1║2║1.0", p.getData());
  }
}
