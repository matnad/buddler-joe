package net.packets.gamestatus;

import static game.Game.Stage.INLOBBBY;
import static game.Game.Stage.PLAYING;

import entities.Player;
import game.Game;
import game.map.ClientMap;
import net.packets.Packet;
import net.packets.playerprop.PacketPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A packed that is send from the server to the client, to inform the client that the GameRound of
 * his lobby has started. Packet-Code: LOBOV
 *
 * @author Sebastian Schlachter
 */
public class PacketStartRound extends Packet {

  private static final Logger logger = LoggerFactory.getLogger(PacketStartRound.class);


  /**
   * Constructor that is used by the Server to build the Packet.
   *
   * @param clientId a ClientId (to allow second constructor) Not sure if needed.
   */
  public PacketStartRound(int clientId) {
    super(PacketTypes.START);
    setClientId(clientId);
    validate();
  }

  /**
   * Constructor that is used by the Client to build the Packet, after receiving the Command STOPG.
   */
  public PacketStartRound() {
    super(PacketTypes.START);
    validate();
  }

  /** Dummy method since there is no data to validate. */
  @Override
  public void validate() {
    // No data to validate since it is a Empty Packet
  }

  /**
   * Method that lets the client react to the receiving of this packet. Changes the display from the
   * InLobby-Menu to the actual Game. The game begins.
   */
  @Override
  public void processData() {
    Game.setStartedAt(System.currentTimeMillis());
    Game.removeActiveStage(INLOBBBY);
    Game.clearAllTextAtEndOfCurrentFrame();
    ClientMap map = Game.getMap();
    Game.setAfterMatchLobbyReady(false);
    try {
      map.reloadMap();
      Player player = Game.getActivePlayer();
      player.setPosition(map.getSpawnPositionForPlayer(player));
      new PacketPos(player.getPositionXy().x, player.getPositionXy().y, player.getRotY())
          .sendToServer();
      Game.addActiveStage(PLAYING);
      // InLobby.done();
    } catch (NullPointerException e) {
      e.printStackTrace();
      addError("No map available.");
    }
  }
}
