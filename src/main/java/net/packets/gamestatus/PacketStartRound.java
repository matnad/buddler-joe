package net.packets.gamestatus;

import static game.Game.Stage.INLOBBBY;
import static game.Game.Stage.PLAYING;

import game.Game;
import game.stages.InLobby;
import net.packets.Packet;

/**
 * A packed that is send from the server to the client, to inform the client that the GameRound of
 * his lobby has started. Packet-Code: LOBOV
 *
 * @author Sebastian Schlachter
 */
public class PacketStartRound extends Packet {

  /**
   * Constructor that is used by the Server to build the Packet.
   *
   * @param clientId a ClientId (to allow second constructor) Not sure if needed.
   */
  public PacketStartRound(int clientId) {
    // server builds
    super(PacketTypes.START);
    setClientId(clientId);
    validate();
  }

  /**
   * Constructor that is used by the Client to build the Packet, after receiving the Command STOPG.
   */
  public PacketStartRound() {
    // client builds
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
    InLobby.done();
    Game.removeActiveStage(INLOBBBY);
    Game.getMap().reloadMap();
    Game.addActiveStage(PLAYING);
    //InLobby.done();
  }
}
