package net.packets.lists;

import net.ServerLogic;
import net.packets.Packet;

/**
 * A packet that gets sent from the client to the server to receive a list of all current games
 * running. Packet code: GTGML
 *
 * @author Joe's Buddler Corp.
 */
public class PacketGetGameList extends Packet {

  /**
   * Constructor that is used by the server upon receiving a GTGML command.
   *
   * @param clientId The clientId of the requesting client.
   */
  public PacketGetGameList(int clientId) {
    super(PacketTypes.GET_GAME_LIST);
  }

  /** Constructor used by the client to create a getGameList packet to be sent to the server. */
  public PacketGetGameList() {
    super(PacketTypes.GET_GAME_LIST);
  }

  /** Validation method is unused in this packet due to it being an empty request packet. */
  @Override
  public void validate() {
    // No validation necessary since it being an empty packet
  }

  /** Creates a String of all lobbies currently in a game. */
  @Override
  public void processData() {
    String info;
    if (!isLoggedIn()) {
      addError("Not loggedin yet");
    }
    if (hasErrors()) {
      info = createErrorMessage();
    } else {
      info = "OK║" + ServerLogic.getLobbyList().getLobbiesInGame();
    }
    PacketGamesOverview p = new PacketGamesOverview(getClientId(), info);
    p.sendToClient(getClientId());
  }
}
