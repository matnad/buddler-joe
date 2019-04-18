package net.packets.lobby;

import entities.blocks.Block;
import entities.blocks.BlockMaster;
import game.Game;
import game.stages.InLobby;
import net.packets.Packet;

/**
 * Packet that gets send from the Server to the Client, to inform the him over the result of the
 * lobby-leave attempt. Packet-Code: LOBLS
 *
 * @author Sebastian Schlachter
 */
public class PacketLeaveLobbyStatus extends Packet {

  private String status;

  /**
   * Constructor that is used by the Server to build the Packet.
   *
   * @param clientId ClientId of the receiver.
   * @param data A String that contains Information about the leave attempt. ("OK" or in the case of
   *     an error, a suitable errormessage) {@link PacketLeaveLobbyStatus#status} gets set to equal
   *     data.
   */
  public PacketLeaveLobbyStatus(int clientId, String data) {
    // Server builds
    super(PacketTypes.LEAVE_LOBBY_STATUS);
    setClientId(clientId);
    setData(data);
    status = getData();
    validate();
  }

  /**
   * Constructor that is used by the Client to build this packet, if he receives "LOBLS".
   *
   * @param data A String that contains Information about the lobby-leave attempt. ("OK" or in the
   *     case of an error, a suitable errormessage) {@link PacketLeaveLobbyStatus#status} gets set
   *     to equal data.
   */
  public PacketLeaveLobbyStatus(String data) {
    // Client builds
    super(PacketTypes.LEAVE_LOBBY_STATUS);
    setData(data);
    status = getData();
    validate();
  }

  /**
   * Validation method to check the data that has, or will be send in this packet. Checks if {@link
   * PacketLeaveLobbyStatus#status} is not null. Checks that {@link PacketLeaveLobbyStatus#status}
   * consists of extendet ASCII Characters. In the case of an error it gets added with {@link
   * Packet#addError(String)}.
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
   * validate.(prints errormessages if there are some) If {@link PacketLeaveLobbyStatus#status}
   * starts with "OK", the message "Successfully left lobby" gets printed and the Menu switches to
   * ChooseLobby. Else in the case of an error on the serverside the error message gets printed.
   */
  @Override
  public void processData() {
    if (hasErrors()) { // Errors on Client
      System.out.println(createErrorMessage());
    } else if (status.startsWith("OK")) {
      System.out.println("Successfully left lobby");
      InLobby.done();
      Game.addActiveStage(Game.Stage.CHOOSELOBBY);
      Game.removeActiveStage(Game.Stage.INLOBBBY);
      // InLobby.done();
    } else { // Errors on Server
      System.out.println(status);
    }
  }
}
