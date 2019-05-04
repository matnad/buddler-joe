package net.packets.lobby;

import game.Game;
import game.stages.ChooseLobby;
import game.stages.InLobby;
import net.packets.Packet;

/**
 * Packet that gets send from the Server to the Client, to inform the him over the result of the
 * lobby-join attempt. Packet-Code: LOBJS
 *
 * @author Sebastian Schlachter
 */
public class PacketJoinLobbyStatus extends Packet {

  private String status;

  /**
   * Constructor that is used by the Server to build the Packet.
   *
   * @param clientId ClientId of the receiver.
   * @param data A String that contains Information about the join attempt. ("OK" or in the case of
   *     an error, a suitable errormessage)
   */
  public PacketJoinLobbyStatus(int clientId, String data) {
    // server builds
    super(Packet.PacketTypes.JOIN_LOBBY_STATUS);
    setData(data);
    setClientId(clientId);
    validate();
  }

  /**
   * Constructor that is used by the Client to build this packet, if he receives "LOBJS".
   *
   * @param data A String that contains Information about the lobby-join attempt. ("OK" or in the
   *     case of an error, a suitable errormessage) {@link PacketJoinLobbyStatus#status} gets set to
   *     equal data.
   */
  public PacketJoinLobbyStatus(String data) {
    // client builds
    super(Packet.PacketTypes.JOIN_LOBBY_STATUS);
    setData(data);
    status = getData();
    validate();
  }

  /**
   * Validation method to check the data that has, or will be send in this packet. Checks if {@code
   * data} is not null. Checks that {@link PacketJoinLobbyStatus#status} consists of extendet ASCII
   * Characters. In the case of an error it gets added with {@link Packet#addError(String)}.
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
   * Method that lets the Client react to the receiving of this packet. Check for errors in
   * validate.(prints errormessages if there are some) If {@link PacketJoinLobbyStatus#status}
   * starts with "OK", the message "Successfully joined lobby" gets printed and the Menu changes to
   * InLobby. Else in the case of an error on the serverside the error message gets printed.
   */
  @Override
  public synchronized void processData() {
    if (hasErrors()) { // Errors on Client
      // System.out.println(createErrorMessage());
    } else if (status.startsWith("OK")) {
      // System.out.println("Successfully joined lobby");
      Game.getChat().setLobbyChatSettings();
      ChooseLobby.setRemoveAtEndOfFrame(true);
      InLobby.setRemoveAtEndOfFrame(true);
      Game.addActiveStage(Game.Stage.INLOBBBY);
      Game.removeActiveStage(Game.Stage.CHOOSELOBBY);
    } else { // Errors on Server
      // System.out.println(status);
    }
  }
}
