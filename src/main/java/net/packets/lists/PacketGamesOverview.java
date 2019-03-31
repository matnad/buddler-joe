package net.packets.lists;

import net.packets.Packet;

/**
 * A packet to return an overview of all currently inGame lobbies. To be displayed to the client who
 * requested it. Packet code: GMLOV
 *
 * @author Joe's Buddler Corp.
 */
public class PacketGamesOverview extends Packet {

  private String[] in;

  /**
   * Constructor for the client to construct an overview packet and then print it out.
   *
   * @param data the data received by the server which should either contain errors, a message that
   *     there are no lobbies in game or all current lobbies in game.
   */
  public PacketGamesOverview(String data) {
    super(PacketTypes.GAMES_OVERVIEW);
    setData(data);
    in = getData().split("║");
    validate();
  }

  /**
   * Constructor to be used by the server to send the overview over the inGame lobbies to the
   * client.
   *
   * @param clientId The client which should receive the packet
   * @param data The data so be sent to the client which should either contain errors, a message
   *     that there are * no lobbies in game or all current lobbies in game.
   */
  public PacketGamesOverview(int clientId, String data) {
    super(PacketTypes.GAMES_OVERVIEW);
    setClientId(clientId);
    setData(data);
    in = getData().split("║");
    validate();
  }

  /** Validation whether the String is not null or extended Ascii. */
  @Override
  public void validate() {
    if (getData() != null) {
      for (String s : in) {
        isExtendedAscii(s);
      }
    } else {
      addError("No data has been found");
    }
  }

  /** Method to display the overview to the client. */
  @Override
  public void processData() {
    if (hasErrors()) {
      System.out.println(createErrorMessage());
    } else if (in[0].equals("OK")) { // the "OK" gets added in PacketCreatLobby.processData and
      System.out.println("-------------------------------------");
      System.out.println("Games online:");
      for (int i = 1; i < in.length; i++) {
        System.out.println(in[i]);
      }
      System.out.println("-------------------------------------");
    } else {
      System.out.println(in[0]);
    }
  }
}
