package net.packets.life;

import game.NetPlayerMaster;
import net.ServerLogic;
import net.lobbyhandling.Lobby;
import net.packets.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PacketLifeStatus extends Packet {
  private int newLives;
  private int effectedPlayerId;
  public static final Logger logger = LoggerFactory.getLogger(PacketLifeStatus.class);

  /**
   * Client creates packet, if he saw a crush. Data contains the new life status from the sender's
   * perspective and the playerId of the crushed player.
   *
   * @param newLives life status of effected player
   * @param effectedPlayerId the effected player
   */
  public PacketLifeStatus(int newLives, int effectedPlayerId) {
    super(PacketTypes.LIFE_STATUS);
    this.newLives = newLives;
    this.effectedPlayerId = effectedPlayerId;
    setData(this.newLives + "║" + this.effectedPlayerId);
    // no validation like PacketVelocity
  }

  /**
   * Server creates packet after receiving and processes. (Decides if there will be a change of life
   * status of effected player).
   *
   * @param clientId clientId of the sender
   * @param data contains the new life status from the sender's perspective and the effected player.
   */
  public PacketLifeStatus(int clientId, String data) {
    super(PacketTypes.LIFE_STATUS);
    setData(data);
    setClientId(clientId);
    validate();
  }

  /**
   * Server creates this packet to inform all players about the decision. It will be sent to the
   * lobby.
   *
   * <p>The client also creates this packet after receiving it from server. All clients will update
   * the life status of their NetPlayer (effected player).
   *
   * @param data contains the final life status and the effected player
   */
  public PacketLifeStatus(String data) {
    super(PacketTypes.LIFE_STATUS);
    setData(data);
    validate();
  }

  /**
   * Checks whether the data is in the correct format and everything is in order with the validation
   * requirements.
   */
  @Override
  public void validate() {
    if (getData() == null) {
      addError("Data is null.");
      return;
    }
    String[] informations = getData().split("║");
    if (informations.length != 2) {
      addError("Invalid number of arguments");
      return;
    }
    try {
      this.newLives = Integer.parseInt(informations[0]);
      this.effectedPlayerId = Integer.parseInt(informations[1]);
      if (newLives < -1 || newLives > 3) {
        logger.error("Can't set lives to " + newLives);
        throw new IllegalArgumentException();
      }
      if (newLives == 3) {
        this.newLives = 2;
      } else if (newLives == -1) {
        this.newLives = 0;
      }

      if (getClientId() != 0) {
        Lobby lobby = ServerLogic.getLobbyForClient(getClientId());
        if (lobby != null) {
          if (!lobby
              .getLobbyPlayers()
              .contains(ServerLogic.getPlayerList().getPlayer(this.effectedPlayerId))) {
            throw new IllegalArgumentException();
          }
        } else {
          throw new IllegalArgumentException();
        }
      }
    } catch (NumberFormatException e) {
      addError("Invalid playerId or life status");
    } catch (IllegalArgumentException e) {
      addError("Invalid life status or invalid playerId or player is not in a lobby");
    }
  }

  /**
   * If the clientId is 0, then the packet will be sent to server, else the server updates the life
   * status of the respective client.
   */
  @Override
  public void processData() {
    if (!hasErrors()) {
      if (getClientId() != 0) {
        Lobby lobby = ServerLogic.getLobbyForClient(effectedPlayerId);
        lobby.addPerspective(getClientId(), effectedPlayerId, newLives);
      } else {
        NetPlayerMaster.getNetPlayerById(effectedPlayerId).updateLives(newLives);
      }
    } else {
      logger.error("Errors processing life status packet: " + createErrorMessage());
    }
  }
}
