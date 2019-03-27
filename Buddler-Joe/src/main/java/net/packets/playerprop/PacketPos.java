package net.packets.playerprop;

import game.NetPlayerMaster;
import net.ServerLogic;
import net.packets.Packet;

public class PacketPos extends Packet {

  private int playerId;
  private float posX;
  private float posY;
  private float rotY;

  // Client sends
  public PacketPos(float posX, float posY, float rotY) {
    super(PacketTypes.POSITION_UPDATE);
    //this.posX = posX;
    //this.posY = posY;
    //this.rotY = rotY;
    setData(posX + "║" + posY + "║" + rotY);
    // No validation here to save performance
  }

  // Server recieves
  public PacketPos(int clientId, String data) {
    super(PacketTypes.POSITION_UPDATE);
    setClientId(clientId);
    setData(clientId + "║" + data);
    validate(); // And construct variables in one step to save performance.
  }

  // Client recieves
  public PacketPos(String data) {
    super(PacketTypes.POSITION_UPDATE);
    setData(data);
    validate(); // And construct variables in one step to save performance.
  }

  @Override
  public void validate() {
    if (getData() == null) {
      addError("No position data found.");
      return;
    }
    String[] posArray = getData().split("║");
    if (posArray.length != 4) {
      addError("Invalid position data.");
      return;
    }
    try {
      this.playerId = Integer.parseInt(posArray[0]);
      this.posX = Float.parseFloat(posArray[1]);
      this.posY = Float.parseFloat(posArray[2]);
      this.rotY = Float.parseFloat(posArray[3]);
    } catch (NumberFormatException e) {
      addError("Invalid position data.");
    }
  }

  @Override
  public void processData() {
    if (!hasErrors()) {
      if (getClientId() > 0) {
        // Server
        sendToLobby(ServerLogic.getPlayerList().getPlayer(getClientId()).getCurLobbyId());
      } else {
        // Client
        NetPlayerMaster.updatePosition(playerId, posX, posY, rotY);
      }
    }
  }
}
