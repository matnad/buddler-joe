package net.packets.name;

import net.packets.Packet;

public class PacketSendName extends Packet {
  private String username;

  /**
   * Constructor to be called by the client upon receiving a sendName packet.
   *
   * @param data The status of the requested player search from the Server. Either an username or an
   *     error message
   */
  public PacketSendName(String data) {
    super(PacketTypes.SEND_NAME);
    setData(data);
    this.username = data;
    validate();
  }

  /**
   * Constructor to be called by the server to create a PacketSendName with the necessary
   * information to pass it to the player.
   *
   * @param clientId The clientId of the client who requested a name
   * @param name The username of the requested player or an error message
   */
  public PacketSendName(int clientId, String name) {
    super(PacketTypes.SEND_NAME);
    setData(name);
    setClientId(clientId);
    this.username = name;
    validate();
  }

  /**
   * Implementation of the abstract validate method to validate the data received Checks whether the
   * username or error message is existing, whether it is extended Ascii and whether there is a name
   * attached if the response from the server is OK.
   */
  @Override
  public void validate() {
    if (username != null) {
      isExtendedAscii(username);
      if (username.startsWith("OK") && username.length() <= 5) {
        addError("No name attached to the message.");
      }
    } else {
      addError("No Name found.");
    }
  }

  /**
   * Implementation of the abstract processData method to be called by the client Checks whether the
   * response is OK or not and then returns either the searched for name or the error message.
   */
  @Override
  public void processData() {
    try {
      if (username.substring(0, 2).startsWith("OK") && !hasErrors()) {
        System.out.println("The player searched for is: " + username.substring(3));
      } else {
        if (hasErrors()) {
          System.out.println(createErrorMessage());
        } else {
          System.out.println(username);
        }
      }
    } catch (StringIndexOutOfBoundsException e) {
      System.out.println(username);
    }
  }
}
