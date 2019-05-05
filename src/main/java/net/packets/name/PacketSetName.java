package net.packets.name;

import game.Game;
import net.ServerLogic;
import net.packets.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PacketSetName extends Packet {

  private static final Logger logger = LoggerFactory.getLogger(PacketSetName.class);
  private String username;

  /**
   * Constructor to be called by the server to set a username for a corresponding player.
   *
   * @param clientId The clientId of the player that wants to set their username
   * @param data The username the player would like to set
   */
  public PacketSetName(int clientId, String data) {
    super(PacketTypes.SET_NAME);
    setData(data);
    setClientId(clientId);
    validate();
    if (hasErrors()) {
      return;
    }
    username = getData().trim();
  }

  /**
   * Constructor to be called by the client to create a setName packet and then pass it to the
   * server.
   *
   * @param usernameIn The username the player would like to set for himself
   */
  public PacketSetName(String usernameIn) {
    super(PacketTypes.SET_NAME);
    setData(usernameIn);
    validate();
    if (hasErrors()) {
      return;
    } else {
      this.username = getData().trim();
      Game.getSettings().setUsername(username);
    }
  }

  /**
   * Implementation of the abstract validate method to check whether the data is in fact a username
   * or not Calls the checkUsername method to validate the username. Adds errors to the errorList if
   * there are any
   */
  @Override
  public void validate() {
    checkUsername(getData());
  }

  /**
   * Implementation of the abstract processData method to be called by the server which sets the new
   * username Created a String status that returns that status of the name change. If there have
   * occurred any errors, these get turned into an error message, then the method checks whether the
   * username is already in the playerList. If yes then it starts a counter to try as long as it
   * takes to set the username in the list until it is unique. It then sets the changed username to
   * the List and created a status message to be sent to the player. If no then it sets the username
   * right away and returns a successful status message. It then creates a PacketSetName class
   * instance and returns it to the player.
   */
  @Override
  public void processData() {
    String status;
    if (hasErrors()) {
      status = "ERROR║" + createErrorMessage();
    } else {
      try {
        if (ServerLogic.getPlayerList().isUsernameInList(username)) {
          int counter = 1;
          String name = username;
          while (ServerLogic.getPlayerList().isUsernameInList(name)) {
            name = username + "_" + counter;
            counter++;
          }
          username = name;
          ServerLogic.getPlayerList().getPlayer(getClientId()).setUsername(username);
          status = "CHANGED║" + username;
        } else {
          ServerLogic.getPlayerList().getPlayer(getClientId()).setUsername(username);
          status = "OK║" + username;
        }
      } catch (NullPointerException e) {
        status = "ERROR║ServerPlayer not logged in";
      }
    }
    PacketSetNameStatus p = new PacketSetNameStatus(getClientId(), status);
    p.sendToClient(getClientId());
  }
}
