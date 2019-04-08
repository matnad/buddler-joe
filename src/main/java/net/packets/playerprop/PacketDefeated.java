package net.packets.playerprop;

import entities.NetPlayer;
import game.NetPlayerMaster;
import net.packets.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PacketDefeated extends Packet {

  private static final Logger logger = LoggerFactory.getLogger(PacketDefeated.class);

  private int defeatedClientId;

  /**
   * Created by the server to broadcast the defeat of a player.
   *
   * @param clientId clientId of the defeated player
   */
  public PacketDefeated(int clientId) {
    super(PacketTypes.PLAYER_DEFEATED);
    setClientId(clientId);
    setData("" + clientId);
    validate();
  }

  /**
   * Client reads data and gets ID of the defeated player.
   *
   * @param data contains an integer which is the player ID
   */
  public PacketDefeated(String data) {
    super(PacketTypes.PLAYER_DEFEATED);
    setData(data);
    validate(); // And set variable
  }

  @Override
  public void validate() {
    try {
      defeatedClientId = Integer.parseInt(getData());
    } catch (NumberFormatException e) {
      addError("Client ID is not an integer.");
    }
  }

  @Override
  public void processData() {
    NetPlayer defeatedNetPlayer = NetPlayerMaster.getNetPlayerById(defeatedClientId);
    if (defeatedNetPlayer == null) {
      addError("Player not found.");
    }
    if (!hasErrors()) {
      defeatedNetPlayer.setDefeated(true); // If null, then packet has errors.
    } else {
      logger.error("Errors while transmitting defeated player ID. " + createErrorMessage());
    }
  }
}
