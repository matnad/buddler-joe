package net.packets.lobby;

import net.packets.Packet;

/**
 * A packed that is send from the server to the client, which contains the names of all clients that
 * are in the lobby. Packet-Code: LOBCI
 *
 * @author Sebastian Schlachter
 */
public class PacketCurLobbyInfo extends Packet {

  private String info;
  private String[] infoArray;

  /**
   * Constructor that is used by the Server to build the Packet.
   *
   * @param clientId clientId of the the receiver.
   * @param data A single String that begins with "OK║" and contains the names of all clients that
   *     are in the lobby of the receiver. (names are separated by "║") In the case that an error
   *     occurred before, the String is an errormessage and does not begin with "OK║". {@link
   *     PacketCurLobbyInfo#info} gets set to equal data.
   */
  public PacketCurLobbyInfo(int clientId, String data) {
    // server builds
    super(PacketTypes.CUR_LOBBY_INFO);
    setClientId(clientId);
    setData(data);
    info = getData();
    infoArray = new String[0]; // necessary since infoArray is not really used on the Server side,
    // but needed in validate
    validate();
  }

  /**
   * Constructor that is used by the Client to build the Packet, after receiving the Command LOBCI.
   *
   * @param data A single String that begins with "OK║" and contains the names of all clients that
   *     are in the lobby of the receiver. (names are separated by "║") In the case that an error
   *     occurred before, the String is an errormessage and does not begin with "OK║". {@link
   *     PacketCurLobbyInfo#info} gets set to equal data. The variable data gets split at the
   *     positions of "║". Every substring gets then saved in to the Array {@code infoArray}.
   */
  public PacketCurLobbyInfo(String data) {
    // client builds
    super(PacketTypes.CUR_LOBBY_INFO);
    setData(data);
    info = getData();
    infoArray = data.split("║");
    validate();
  }

  /**
   * Validation method to check the data that has, or will be send in this packet. Checks if {@code
   * data} is not null. Checks for every element of the Array {@code infoArray}, that it consists of
   * extendet ASCII Characters. In the case of an error it gets added with {@link
   * Packet#addError(String)}.
   */
  @Override
  public void validate() {
    if (info != null) {
      for (String s : infoArray) {
        isExtendedAscii(s);
      }
    } else {
      addError("No Status found.");
    }
  }

  /**
   * Method that lets the Client react to the receiving of this packet. Check for errors in
   * validate. If {@code in[0]} equals "OK" the names of the clients get printed. Else in the case
   * of an error only the error message gets printed.
   */
  @Override
  public void processData() {
    if (hasErrors()) { // Errors ClientSide
      String s = createErrorMessage();
      System.out.println(s);
    } else if (infoArray[0].equals("OK")) { // No Errors ServerSide
      System.out.println("-------------------------------------");
      System.out.println("Players in Lobby:");
      for (int i = 1; i < infoArray.length; i++) {
        System.out.println(infoArray[i]);
      }
      System.out.println("-------------------------------------");
      System.out.println("To chat with players in this lobby, type: C <message>");
      System.out.println("To leave this lobby, type: leave");
    } else { // Errors ServerSide
      System.out.println(infoArray[0]);
    }
  }
}
