package net.packets.life;

import game.NetPlayerMaster;
import net.ServerLogic;
import net.lobbyhandling.Lobby;
import net.packets.Packet;
import net.playerhandling.ServerPlayer;

import java.util.concurrent.CopyOnWriteArrayList;

public class PacketLifeStatus extends Packet {
  private int currentLives;
  private String sender;
  private int playerId;
  // data = currentLives+sender+playerId
  // bsp: 2server4

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
    // System.out.println("here");
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
    // System.out.println("here");
    try {
      this.playerId = Integer.parseInt(playerId);
      this.currentLives = Integer.parseInt(currentLives); // throws NumberFormatException
      if (this.currentLives < 0 || this.currentLives > 2) {
        throw new RuntimeException();
      }
      // System.out.println("here");
      if (!(sender.equals("server") || sender.equals("client"))) {
        throw new RuntimeException();
      } else {
        this.sender = sender;
      }
      if (getClientId() != 0) {
        boolean isIn = false;
        for (ServerPlayer player : ServerLogic.getLobbyForClient(getClientId()).getLobbyPlayers()) {
          if (this.playerId == player.getClientId()) {
            isIn = true;
            break;
          }
        }
        if (isIn == false) {
          throw new RuntimeException();
        }
      }
    } catch (NumberFormatException e) {
      addError("Invalid playerId or life status");
    } catch (RuntimeException e) {
      addError("Invalid life status or invalid sender or invalid playerId");
    }
  }

  /**
   * If the clientId is 0, then the packet will be sent to server, else the server updates the life
   * status of the respective client.
   */
  @Override
  public void processData() {
    if (!hasErrors()) {
      // Packet erstellt bei client um server zu schicken
      if (getClientId() == 0 && sender.equals("client")) {
        sendToServer();
      }
      // packet erstellt bei server nachdem erhalten von client
      if (getClientId() != 0 && sender.equals("client")) {
        // ...entscheiden
        Lobby lobby = ServerLogic.getLobbyForClient(playerId);
        lobby.addPerspective(getClientId(), playerId, currentLives);
      }
      // packet erstellt bei allen clients nachdem server verschickt
      if (getClientId() == 0 && sender.equals("server")) {
        NetPlayerMaster.getNetPlayerById(playerId).updateLives(currentLives);
      }
    }
  }
}
