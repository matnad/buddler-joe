package net.packets.life;

import game.NetPlayerMaster;
import net.ServerLogic;
import net.lobbyhandling.Lobby;
import net.packets.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PacketLifeStatus extends Packet {
  private int currentLives;
  private String sender;
  private int playerId;
  public static final Logger logger = LoggerFactory.getLogger(PacketLifeStatus.class);

  /**
   * Client creates packet (and sends to server if sender equals client), if he saw a crush. Data
   * contains the life status from the sender's perspective, sender specification(server/client) and
   * the playerId of the crushed player.
   *
   * <p>Server creates this packet to send the final decision to all players in the lobby.
   *
   * <p>if sender equals server, the packet will be sent from server to clients. currentLives will
   * be the final decision of server. This packet will be sent to lobby, so that every client can
   * update the life status of the respective player.
   *
   * @param data is currentLives+sender+playerId; example: 2server4
   */
  public PacketLifeStatus(String data) {
    super(PacketTypes.LIFE_STATUS);
    setData(data);
    validate();
  }

  /**
   * Server creates packet after receiving and processes. (Decides if there will be a change of life
   * status of playerId). data contains the life status from the sender's perspective, sender
   * specification(client) and the playerId of the crushed player.
   *
   * @param data is currentLives+sender+playerId; example: 2server4
   */
  public PacketLifeStatus(int clientId, String data) {
    super(PacketTypes.LIFE_STATUS);
    setData(data);
    setClientId(clientId);
    validate();
  }

  /**
   * Checks whether the data is in the correct format and everything is in order with the validation
   * requirements.
   */
  @Override
  public void validate() {
    String currentLives = getData().substring(0, 1);
    String sender = getData().substring(1, 7);
    String playerId = getData().substring(7);
    try {
      this.playerId = Integer.parseInt(playerId);
      int test = Integer.parseInt(currentLives);
      if (test < -1 || test > 3) {
        throw new IllegalArgumentException();
      }
      if (test == 3) {
        this.currentLives = 2;
      } else if (test == -1) {
        this.currentLives = 0;
      } else {
        this.currentLives = test;
      }
      if (!(sender.equals("server") || sender.equals("client"))) {
        throw new IllegalArgumentException();
      } else {
        this.sender = sender;
      }
      if (getClientId() != 0) {
        Lobby lobby = ServerLogic.getLobbyForClient(getClientId());
        if (lobby != null) {
          if (!lobby
              .getLobbyPlayers()
              .contains(ServerLogic.getPlayerList().getPlayer(this.playerId))) {
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
          "Invalid life status or invalid sender or invalid playerId or player is not in a lobby");
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
        Lobby lobby = ServerLogic.getLobbyForClient(playerId);
        lobby.addPerspective(getClientId(), playerId, currentLives);
      }
      if (getClientId() == 0 && sender.equals("server")) {
        NetPlayerMaster.getNetPlayerById(playerId).updateLives(currentLives);
      }
    } else {
      logger.error(
          "Invalid playerId or invalid life status or invalid sender or the player is not in a lobby");
    }
  }
}
