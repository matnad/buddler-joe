package net.packets.chat;

import net.packets.Packet;

/**
 * Packet that gets send from the Server to the Client, to inform the him over the result of the
 * send message attempt. Packet-Code: CHATN
 *
 * @author Moritz Würth
 */
public class PacketChatMessageStatus extends Packet {

  private String status;

  /**
   * Constructor that is used by the Client to build the packet.
   *
   * @param data A String that contains Information about the leave attempt. ("OK" or in the case of
   *     an error, a suitable errormessage) {@link PacketChatMessageStatus#status} gets set to equal
   *     {@param data}.
   */
  public PacketChatMessageStatus(String data) {
    super(PacketTypes.CHAT_MESSAGE_STATUS);
    setData(data);
    status = getData();
    validate();
  }

  /**
   * Constructor that is used by the Server to build the Packet.
   *
   * @param clientID ClientId of the receiver.
   * @param data A String that contains Information about the leave attempt. ("OK" or in the case of
   *     an error, a suitable errormessage) {@link PacketChatMessageStatus#status} gets set to equal
   *     {@param data}.
   */
  public PacketChatMessageStatus(int clientID, String data) {
    super(PacketTypes.CHAT_MESSAGE_STATUS);
    setClientId(clientID);
    setData(data);
    validate();
  }
  /**
   * Validation method to check the data that has, or will be send in this packet. Checks if {@link
   * PacketChatMessageStatus#status} is not null. Checks that {@link PacketChatMessageStatus#status}
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
   * validate.(prints errormessages if there are some) The error message gets print, if an error on
   * the serverside exist
   */

  @Override
  public void processData() {
    if (status.startsWith("OK")) {
      //            System.out.println("Successfully sent a Message");
    } else {
      System.out.println(status);
    }
  }
}
