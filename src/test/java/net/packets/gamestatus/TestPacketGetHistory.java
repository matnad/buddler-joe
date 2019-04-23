package net.packets.gamestatus;

import net.TestClient;
import net.TestServer;
import org.junit.Test;

public class TestPacketGetHistory {
  private TestServer server = new TestServer();
  private TestClient client = new TestClient();

  @Test
  public void testCorrectHistory() {
    client.getClient().sendToServerTest(new PacketGetHistory());
  }
}
