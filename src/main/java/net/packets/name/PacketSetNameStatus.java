package net.packets.name;

import game.Game;
import net.packets.Packet;

public class PacketSetNameStatus extends Packet {

  private String status;

  /**
   * Constructor to be called by the server in the PacketSetName class to be sent to the client.
   *
   * @param clientId The clientId of the client who should receive the package
   * @param status The status message created by the server concerning the status of the name
   *     setting
   */
  public PacketSetNameStatus(int clientId, String status) {
    super(PacketTypes.SET_NAME_STATUS);
    setData(status);
    setClientId(clientId);
    this.status = status;
    validate();
  }

  /**
   * Constructor to be called by the client upon receiving a setNameStatus package from the server.
   *
   * @param data Contains the status by the server to be displayed to the client
   */
  public PacketSetNameStatus(String data) {
    super(PacketTypes.SET_NAME_STATUS);
    setData(data);
    this.status = data;
    validate();
  }

  /**
   * Implementation of the abstract validate method to validate the received data Validate method
   * calls the isExtendedAscii method which checks whether a String is extended Ascii or not. If the
   * status is null or not extended Ascii, an error message gets added to the error message List.
   */
  @Override
  public void validate() {
    if (status != null) {
      isExtendedAscii(status);
    } else {
      addError("No Status found.");
    }
  }

  /**
   * Implementation of the abstract processData method to process the data on the client side
   * received from the server Checks whether the Status is either Successfully, meaning that the
   * name setting was successful with the name chosen, Changed, which means that the name setting
   * was successful but the name had to be changed to another version of it or that the Login was
   * not successful due to errors in the status or errors detected due to a faulty package.
   */
  @Override
  public void processData() {
    if (status.startsWith("Successfully")) {
      // TODO: Include username as separate variable
      String[] username = getData().split("Successfully changed the name to: ");
      if (username.length == 2) {
        Game.getActivePlayer().setUsername(username[1]);
      }
      System.out.println(status);
    } else if (status.startsWith("Changed")) {
      // TODO: Include username as separate variable
      String[] usernameA = getData().split(". Because your chosen name is already in use.");
      if (usernameA.length >= 1) {
        Game.getActivePlayer().setUsername(usernameA[0].substring(12));
      }
      System.out.println(status);
    } else {
      if (hasErrors()) {
        System.out.println(createErrorMessage());
      } else {
        System.out.println(status);
      }
    }
  }
}
