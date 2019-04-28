package net.packets.life;

import game.NetPlayerMaster;
import net.ServerLogic;
import net.lobbyhandling.Lobby;
import net.packets.Packet;

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
    setData(data);
    setClientId(clientId);
    validate();
  }

  /**
   * Checks whether the data is in the correct format and everything is in order with the validation
   * requirements.
   */
  // hier muss man dann checken ob die zahl im bereich [0,2] liegt, sonst invalid.
  @Override
  public void validate() {
    /*if (getData() == null) {
      addError("Empty message");
    } else {
      for (int i = 0; i < getData().length(); i++) {
        if (!Character.isDigit(getData().charAt(i))) {
          addError("Invalid number");
        }
      }
    }*/
    String currentLives = getData().substring(0, 1);
    String sender = getData().substring(1, 7);
    String playerId = getData().substring(7);

    try {
      this.playerId = Integer.parseInt(playerId);
      this.currentLives = Integer.parseInt(currentLives); // throws NumberFormatException
      if (this.currentLives < 0 || this.currentLives > 2) {
        throw new RuntimeException();
      }
      if (!(sender.equals("server") || sender.equals("client"))) {
        throw new RuntimeException();
      } else {
        this.sender = sender;
      }
    } catch (NumberFormatException e) {
      addError("Invalid playerId or life status");
    } catch (RuntimeException e) {
      addError("Invalid life status or invalid sender");
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
        // System.out.println("here");
        sendToServer();
      }
      // packet erstellt bei server nachdem erhalten von client
      if (getClientId() != 0 && sender.equals("client")) {
        // ...entscheiden
        // System.out.println("here");
        Lobby lobby = ServerLogic.getLobbyForClient(playerId);
        lobby.addPerspective(playerId, currentLives);
      }
      // packet erstellt bei allen clients nachdem server verschickt
      if (getClientId() == 0 && sender.equals("server")) {
        // updaten bei ihrem netmaster
        // System.out.println("I received packet");
        if (0 <= currentLives && currentLives <= 2) {
          //ServerLogic.getPlayerList().getPlayer(playerId).setCurrentLives(currentLives);
          //ServerLogic.getPlayerList().getPlayers().get(playerId).setCurrentLives(currentLives);
          
        }
        NetPlayerMaster.getNetPlayerById(playerId).updateLives(currentLives);
      }
    }
  }
}
