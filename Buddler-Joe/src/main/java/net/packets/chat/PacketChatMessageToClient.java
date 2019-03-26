package net.packets.chat;

import net.packets.Packet;

/**
 * * Packet that gets send a chat message from the Server to the Client. * Packet-Code: CHATC
 * * @author Moritz Würth
 */
public class PacketChatMessageToClient extends Packet {

  private String chatmsg;

  /**
   * Constructor that is used by the Client to build the Packet.
   *
   * @param chatmsg the final message which get printed with the username from the sender, the time
   *     and the message. {@link PacketChatMessageToServer} gets set here, to equal data.
   */
  public PacketChatMessageToClient(String chatmsg) {
    super(PacketTypes.CHAT_MESSAGE_TO_CLIENT);
    this.chatmsg = chatmsg.trim();
    validate();
  }

  /**
   * Constructor that is used by the Server to build the Packet.
   *
   * @param clientID ClientId of the client that has sent this packet.
   * @param data a String with the chat message, timestamp and receiver. * (names are separated by
   *     "║") {@link PacketChatMessageToServer} gets set here, to equal data.
   */
  public PacketChatMessageToClient(int clientID, String data) {
    super(PacketTypes.CHAT_MESSAGE_TO_CLIENT);
    setClientId(clientID);
    setData(data);
    chatmsg = getData().trim();
    validate();
  }

  /**
   * Check if {@link PacketChatMessageToServer} has characters. Check if {@link
   * PacketChatMessageToServer} is shorter then 130 characters. In the case of an error it gets
   * added with {@link Packet#addError(String)}.
   */
  public void validate() {
    if (chatmsg == null) {
      addError("No Message found");
      return;
    }
    if (chatmsg.length() > 130) {
      addError("Message to long. Maximum is 100 Characters.");
    }
    isExtendedAscii(chatmsg);
  }

  /**
   * Method that lets the Client react to the receiving of this packet. Check for errors in
   * validate.(prints errormessages if there are some) If there are no errors the chat message get
   * printed.
   */
  public void processData() {
    String status;
    if (hasErrors()) {
      status = createErrorMessage();
      System.out.println(status);
    } else {
      System.out.println(chatmsg);
    }
  }
}
