package net.packets.lobby;

import game.Game;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class TestPacketJoinLobbyStatus {
  @Test
  public void checkDataNull() {
    PacketJoinLobbyStatus p = new PacketJoinLobbyStatus(null);
    Assert.assertEquals("ERRORS: No Status found.", p.createErrorMessage());
  }

  @Test
  public void checkDataNotAscii() {
    PacketJoinLobbyStatus p = new PacketJoinLobbyStatus("ਠ");
    p.processData();
    Assert.assertEquals("ERRORS: Invalid characters, only extended ASCII.", p.createErrorMessage());
  }

  @Test
  public void checkGameNotRunning() {
    Game game = Mockito.spy(Game.class);
    List<Game.Stage> activeStages = new CopyOnWriteArrayList<>();
    activeStages.add(Game.Stage.CHOOSELOBBY);
    game.setActiveStages(activeStages);
    PacketJoinLobbyStatus p = new PacketJoinLobbyStatus("OK");
    p.processData();
    Assert.assertEquals("ERRORS: Game is not running.", p.createErrorMessage());
  }
}
