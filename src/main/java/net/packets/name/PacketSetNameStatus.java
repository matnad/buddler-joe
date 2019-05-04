package net.packets.name;

import game.Game;
import game.stages.ChangeName;
import net.packets.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PacketSetNameStatus extends Packet {

  private String[] status;
  private static final Logger logger = LoggerFactory.getLogger(PacketSetNameStatus.class);

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
    validate();
  }

  /**
   * Implementation of the abstract validate method to validate the received data Validate method
   * calls the isExtendedAscii method which checks whether a String is extended Ascii or not. If the
   * status is null or not extended Ascii, an error message gets added to the error message List.
   */
  @Override
  public void validate() {
    if (getData() != null) {
      status = getData().split("║");
      for (int i = 0; i < status.length; i++) {
        if (!isExtendedAscii(status[i])) {
          return;
        }
      }
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
    if (hasErrors()) {
      logger.info(createErrorMessage());
      return;
    }
    if (status[0].equals("CHANGED")) {
      if (status.length == 2) {
        try {
          Game.getActivePlayer().setUsername(status[1]);
          Game.getSettings().setUsername(status[1]);
          ChangeName.setMsg("");
        } catch (NullPointerException e) {
          addError("Not a real game!");
        }
      }
    } else if (status[0].equals("OK")) {
      if (status.length == 2) {
        try {
          Game.getActivePlayer().setUsername(status[1]);
          Game.getSettings().setUsername(status[1]);
          ChangeName.setMsg(status[1]);
        } catch (NullPointerException e) {
          addError("No game started.");
        }
      }
    } else {
      addError(status[1]);
      logger.info(status[1]);
    }
    ChangeName.setMsg(status[1]);
  }
}
