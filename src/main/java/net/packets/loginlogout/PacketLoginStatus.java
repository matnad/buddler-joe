package net.packets.loginlogout;

import game.Game;
import net.packets.Packet;
import net.packets.lobby.PacketGetLobbies;

public class PacketLoginStatus extends Packet {

  private String status;
  private String username;

  /**
   * Constructor when the client receives a PacketLoginStatus packet from the server.
   *
   * @param data Should contain the status message from the server concerning the Login status.
   */
  public PacketLoginStatus(String data) {
    super(PacketTypes.LOGIN_STATUS);
    setData(data);
    this.status = data;
    validate();
  }

  /**
   * Constructor when the Server creates a PacketLoginStatus packet to be sent to the client.
   *
   * @param clientId The client to which this particular Login Status belongs to
   * @param status The status from the server which gets created in the PacketLogin
   */
  public PacketLoginStatus(int clientId, String status) {
    super(PacketTypes.LOGIN_STATUS);
    setData(status);
    setClientId(clientId);
    this.status = status;
    validate();
  }

  /**
   * Implementation of the abstract validate method to validate the input data/status Validate
   * method calls the isExtendedAscii method which checks whether a String is extended Ascii or not.
   * If the status is null or not extended Ascii, an error message gets added to the error message
   * List.
   */
  @Override
  public void validate() {
    if (status != null) {
      isExtendedAscii(status);
      String[] temp = status.split("║");
      try {
        username = temp[1];
      } catch (ArrayIndexOutOfBoundsException e) {
        addError("There is no username attached.");
      }
    } else {
      addError("No Status found.");
    }
  }

  /**
   * Implementation of the abstract processData method to process the data on the client side
   * received from the server Checks whether the Status is either OK, meaning that the login was
   * successful with the name chosen, CHANGE, which means that the login was successful but the name
   * had to be changed to another version of it or That the Login was not successful due to errors
   * in the status or errors detected due to a faulty package.
   */
  @Override
  public void processData() {
    if (hasErrors()) {
      logger.info(createErrorMessage());
      return;
    }
    if (status.startsWith("OK") && !hasErrors() && status.length() > 2) {
      PacketGetLobbies p = new PacketGetLobbies();
      p.sendToServer();
      Game.setLoggedIn(true);
    } else if (status.startsWith("CHANGE") && !hasErrors() && status.length() > 6) {
      Game.getActivePlayer().setUsername(username);
      Game.getSettings().setUsername(username);
      PacketGetLobbies p = new PacketGetLobbies();
      p.sendToServer();
      Game.setLoggedIn(true);
    }
  }
}
