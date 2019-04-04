package net.packets.highscore;

import net.highscore.ServerHighscore;
import net.packets.Packet;

/**
 * Packet that gets send from the Server to the Client, to inform the him over the result of the
 * send message attempt. Packet-Code: CHATN
 *
 * @author Moritz Würth
 */
public class PacketHighscore extends Packet {

  private String[] highscore;

  /** Constructor that is used by the Client to verify the data and process the data. */
  public PacketHighscore(String data) {
    super(PacketTypes.HIGHSCORE);
    setData(data);
    highscore = getData().split("║");
    validate();
  }

  /**
   * Constructor to be used by the client to create a package and request the highscore from the
   * server.
   */
  public PacketHighscore() {
    super(PacketTypes.HIGHSCORE);
  }

  /**
   * Constructor to create the package on the server side and then process and send it to the
   * client.
   *
   * @param clientId The clientId of the client where the information should be returned to.
   */
  public PacketHighscore(int clientId) {
    super(PacketTypes.HIGHSCORE);
    setClientId(clientId);
  }

  /** Check whether the Highscore only contains extended ascii and exists at all. */
  @Override
  public void validate() {
    if (highscore != null) {
      for (int i = 1; i < highscore.length; i++) {
        if (!isExtendedAscii(highscore[i])) {
          break;
        }
      }
    } else {
      addError("No Highscore found.");
    }
  }

  /**
   * Method to process the data either from the server or client side.
   *
   * <p>If on server side then either an error message or the current highscore. Then sends the
   * packet to the client.
   *
   * <p>If on the client side the Highscore gets printed out on the terminal.
   */
  @Override
  public void processData() {
    if (getClientId() > 0) {
      // Server side:
      if (ServerHighscore.getHighscoreAsString().startsWith("There")) {
        setData("ERROR║" + ServerHighscore.getHighscoreAsString());
      } else {
        setData("OK║" + ServerHighscore.getHighscoreAsString());
      }
      this.sendToClient(getClientId());
    } else {
      if (hasErrors()) {
        System.out.println(createErrorMessage());
      } else if (highscore[0].equals(
          "OK")) { // the "OK" gets added in PacketCreatLobby.processData and
        System.out.println("-------------------------------------");
        System.out.println("Current Highscore:");
        for (int i = 1; i < highscore.length; i++) {
          System.out.println(highscore[i]);
        }
        System.out.println("-------------------------------------");
      } else {
        System.out.println(highscore[1]);
      }
    }
  }
}
