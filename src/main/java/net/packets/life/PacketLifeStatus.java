package net.packets.life;

import game.NetPlayerMaster;
import net.ServerLogic;
import net.lobbyhandling.Lobby;
import net.packets.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PacketLifeStatus extends Packet {
  private int currentLives;
  private int effectedPlayer;
  public static final Logger logger = LoggerFactory.getLogger(PacketLifeStatus.class);

  /**
   * Client creates packet, if he saw a crush. Data contains the life status from the sender's
   * perspective, sender specification(server/client) and the playerId of the crushed player.
   *
   * <p>Server creates this packet to send the final decision to all players in the lobby.
   *
   * <p>if sender equals server, the packet will be sent from server to clients. currentLives will
   * be the final decision of server. This packet will be sent to lobby, so that every client can
   * update the life status of the respective player.
   *
   * @param currentLives actual life status of effected player
   * @param effectedPlayer the effected player
   */
  public PacketLifeStatus(int currentLives, int effectedPlayer) {
    super(PacketTypes.LIFE_STATUS);
    setData(currentLives + "║" + effectedPlayer);
  }

  /**
   * Server creates packet after receiving and processes. (Decides if there will be a change of life
   * status of playerId). data contains the life status from the sender's perspective, sender
   * specification(client) and the playerId of the crushed player.
   *
   * @param clientId clientId of the sender
   * @param data is currentLives+sender+playerId; example: 2server4
   */
  public PacketLifeStatus(int clientId, String data) {
    super(PacketTypes.LIFE_STATUS);
    setData(data);
    setClientId(clientId);
    validate();
  }

  /**
   * Server creates this packet to inform all players about the decision.
   * It will be sent to the lobby.
   *
   * The client also creates this packet after receiving it from server.
   * All clients will update the life status of their NetPlayer (effected player).
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
      this.currentLives = Integer.parseInt(informations[0]);
      this.effectedPlayer = Integer.parseInt(informations[1]);
    } catch (NumberFormatException e) {
      addError("Invalid life status odr invalid playerId");
    }

    try {
      if (currentLives < -1 || currentLives > 3) {
        logger.error("Can't set lives to " + currentLives);
        throw new IllegalArgumentException();
      }
      if (currentLives == 3) {
        this.currentLives = 2;
      } else if (currentLives == -1) {
        this.currentLives = 0;
      }

      if (getClientId() != 0) {
        Lobby lobby = ServerLogic.getLobbyForClient(getClientId());
        if (lobby != null) {
          if (!lobby
              .getLobbyPlayers()
              .contains(ServerLogic.getPlayerList().getPlayer(this.effectedPlayer))) {
            throw new IllegalArgumentException();
          }
        } else {
          throw new IllegalArgumentException();
        }
      }
    } catch (NumberFormatException e) {
      addError("Invalid playerId or life status");
    } catch (IllegalArgumentException e) {
      addError(
          "Invalid life status or invalid playerId or player is not in a lobby");
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
        Lobby lobby = ServerLogic.getLobbyForClient(effectedPlayer);
        lobby.addPerspective(getClientId(), effectedPlayer, currentLives);
      }
      if (getClientId() == 0) {
        NetPlayerMaster.getNetPlayerById(effectedPlayer).updateLives(currentLives);
      }
    } else {
      logger.error("Errors processing life status packet: " + createErrorMessage());
    }
  }
}
