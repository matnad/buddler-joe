package net.packets;

import static org.mockito.Mockito.mock;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mock;

public class TestPacket {

  @Mock private Packet packet;

  @Test
  public void checkUsernameTooLong() {
    packet = mock(Packet.class);
    Assert.assertFalse(packet.checkUsername("My name is Buddle Joe and I am great!"));
  }

  @Test
  public void checkUsernameTooShort() {
    packet = mock(Packet.class);
    Assert.assertFalse(packet.checkUsername("tes"));
  }

  @Test
  public void checkUsernameNull() {
    packet = mock(Packet.class);
    Assert.assertFalse(packet.checkUsername(null));
  }

  @Test
  public void checkIsAsciiFalse() {
    packet = mock(Packet.class);
    Assert.assertFalse(packet.isExtendedAscii("║║║"));
  }

  @Test
  public void checkIsIntFalse() {
    packet = mock(Packet.class);
    Assert.assertFalse(packet.isInt("10B"));
  }
}
