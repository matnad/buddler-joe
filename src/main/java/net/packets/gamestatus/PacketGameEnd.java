package net.packets.gamestatus;

import net.packets.Packet;

/**
 * A Packet that gets send from the Client to the Server, to inform him about the end of a Round.
 * Packet-Code: STOPG
 *
 * @author Sebastian Schlachter
 */
public class PacketGameEnd extends Packet {

  /**
   * Constructor that is used by the Server to build the Packet.
   *
   * @param clientId a clientId.
   */
  public PacketGameEnd(int clientId) {
    // server builds
    super(PacketTypes.GAME_OVER);
    setClientId(clientId);
    validate();
  }

  /**
   * Constructor that is used by the Client to build the Packet, after receiving the Command STOPG.
   */
  public PacketGameEnd() {
    // client builds
    super(PacketTypes.GAME_OVER);
    validate();
  }

  @Override
  public void validate() {
    // No data to validate since it is a Empty Packet
  }

  @Override
  public void processData() {}
}
