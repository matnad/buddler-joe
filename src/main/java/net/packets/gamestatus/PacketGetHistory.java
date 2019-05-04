package net.packets.gamestatus;

import game.History;
import net.packets.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A Packet that gets sent from the Client to the Server, to get the History(a conclusion of current
 * and past lobbies and rounds). Packet-Code: HISGE
 *
 * @author Sebastian Schlachter
 */
public class PacketGetHistory extends Packet {

  private static final Logger logger = LoggerFactory.getLogger(PacketGetHistory.class);


  /**
   * Constructor that is used by the Server to build the Packet, after receiving the Command
   * "HISGE".
   *
   * @param clientId ClientId of the Client that has sent the command.
   */
  public PacketGetHistory(int clientId) {
    super(PacketTypes.GET_HISTORY);
    setClientId(clientId);
    validate();
  }

  /**
   * Constructor that will be used by the Client to build the Packet. Which can then be send to the
   * Server. There are no parameters necessary here since the Packet has no real content(only a
   * Type, HISGE).
   */
  public PacketGetHistory() {
    super(PacketTypes.GET_HISTORY);
  }

  /** Dummy method since there is no data to validate. */
  @Override
  public void validate() {}

  /**
   * Method that lets the Server react to the receiving of this packet. Check for errors in validate
   * and if the sender is a logged in Client. If there are no Errors a History-Packet will be
   * created and send back to the sender of this packet.
   */
  @Override
  public void processData() {
    String info;
    if (!isLoggedIn()) {
      return;
    }
    if (hasErrors()) {
      info = createErrorMessage();
    } else {
      info = "OK║" + History.getStory();
    }
    try {
      PacketHistory p = new PacketHistory(getClientId(), info);
      p.sendToClient(getClientId());
    } catch (NullPointerException e) {
      logger.info("Not connected to a Server.");
      return;
    }
  }
}
