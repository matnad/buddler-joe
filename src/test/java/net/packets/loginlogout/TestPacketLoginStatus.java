package net.packets.loginlogout;

import org.junit.Assert;
import org.junit.Test;

public class TestPacketLoginStatus {
  @Test
  public void checkNoUsernameAttached() {
    PacketLoginStatus p = new PacketLoginStatus("test");
    Assert.assertEquals("ERRORS: There is no username attached.", p.createErrorMessage());
  }

  @Test
  public void checkStatusNull() {
    PacketLoginStatus p = new PacketLoginStatus(1, null);
    p.processData();
    Assert.assertEquals("ERRORS: No Status found.", p.createErrorMessage());
  }
}
