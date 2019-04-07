package net.packets.life;

import net.ServerLogic;
import net.packets.Packet;

public class PacketLifeStatus extends Packet {

  /**
   * After receiving, creates a packet by server side and extracts data.
   *
   * @param clientId who sent the packet
   * @param data amount of lives of the specific player
   */
  public PacketLifeStatus(int clientId, String data) {
    super(PacketTypes.LIFE_STATUS);
    setClientId(clientId);
    setData(data);
    validate();
  }

  /**
   * Creates a <code>LifeStatus</code> object by client side and validates its <code>data</code> and
   * sends to server.
   *
   * @param data is the actual life status.
   */
  public PacketLifeStatus(String data) {
    super(PacketTypes.LIFE_STATUS);
    setData(data);
    validate();
  }

  // hier muss man dann checken ob die zahl im bereich [0,2] liegt, sonst invalid.
  @Override
  public void validate() {
    if (getData() == null) {
      addError("Empty message");
    } else {
      for (int i = 0; i < getData().length(); i++) {
        if (!Character.isDigit(getData().charAt(i))) {
          addError("Invalid number");
        }
      }
    }
  }

  /**
   * If the clientId is 0, then the packet will be sent to server, else the server updates the life
   * status of the respective client.
   */
  @Override
  public void processData() {
    if (!hasErrors()) {
      if (getClientId() == 0) { // when packet gets created by client
        sendToServer();
      } else { // when server receives packet
        try {
          ServerLogic.getPlayerList()
              .getPlayer(getClientId())
              .setCurrentLives(Integer.parseInt(getData()));
        } catch (NumberFormatException e) {
          System.out.println("Failed to assign the data.");
        }
      }
    }
  }
}
