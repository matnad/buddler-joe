package net.packets.gamestatus;

import game.History;
import net.packets.Packet;

/**
 * A Packet that gets sent from the Client to the Server, to get the History(a conclusion of current
 * and past lobbies and rounds). Packet-Code: HISGE
 *
 * @author Sebastian Schlachter
 */
public class PacketGetHistory extends Packet {

  /**
   * Constructor that is used by the Server to build the Packet, after receiving the Command
   * "HISGE".
   *
   * @param clientId ClientId of the Client that has sent the command.
   */
  public PacketGetHistory(int clientId) {
    // server builds
    super(PacketTypes.GET_HISTORY);
    setClientId(clientId);
  }

  /**
   * Constructor that will be used by the Client to build the Packet. Which can then be send to the
   * Server. There are no parameters necessary here since the Packet has no real content(only a
   * Type, HISGE).
   */
  public PacketGetHistory() {
    // client builds
    super(PacketTypes.GET_HISTORY);
  }

  @Override
  public void validate() {
    // Nothing to validate.
  }

  @Override
  public void processData() {
    String info;
    if (!isLoggedIn()) {
      addError("Not loggedin yet");
    }
    if (hasErrors()) {
      info = createErrorMessage();
    } else {
      info = "OK║" + History.getStory();
    }
    PacketHistory p = new PacketHistory(getClientId(), info);
    p.sendToClient(getClientId());
  }
}
