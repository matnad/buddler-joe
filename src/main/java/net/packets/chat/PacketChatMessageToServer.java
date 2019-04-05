package net.packets.chat;

import game.NetPlayerMaster;
import java.text.SimpleDateFormat;
import java.util.Date;
import net.ServerLogic;
import net.packets.Packet;
import net.playerhandling.Player;

/**
 * Packet that gets send from the Client to the Server, to send chat message from Client to a other
 * Client via Server. Packet-Code: CHATS
 *
 * @author Moritz Würth
 */
public class PacketChatMessageToServer extends Packet {

  private String chatmsg;
  private String timestamp;
  private String receiver;
  private int wisperId;

  /**
   * Constructor that will be used by the Client to build the Packet. Which can then be send to the
   * Server. The constructor takes the current time and set a new String with the message, timestamp
   * und receiver with "║" as delimiter.
   *
   * @param chatmsg the message from the client {@link PacketChatMessageToServer#chatmsg} gets set
   *     here, to equal data.
   */
  // client
  public PacketChatMessageToServer(String chatmsg) {
    super(PacketTypes.CHAT_MESSAGE_TO_SERVER);
    this.chatmsg = chatmsg.trim();
    SimpleDateFormat simpleFormat = new SimpleDateFormat("HH:mm");
    Date date = new Date();
    timestamp = simpleFormat.format(date);
    receiver = "0";
    setData(chatmsg + "║" + timestamp);
    validate();
  }

  /**
   * Constructor that is used by the Server to build the Packet.
   *
   * @param clientId ClientId of the client that has sent this packet.
   * @param data a String with the chat message, timestamp and receiver. (names are separated by
   *     "║") {@link PacketChatMessageToServer#chatmsg} gets set here, to equal data.
   */
  // server
  public PacketChatMessageToServer(int clientId, String data) {
    super(PacketTypes.CHAT_MESSAGE_TO_SERVER);
    setClientId(clientId);
    if (data == null) {
      data = ""; // To prevent nullpointer when splitting
    }
    String[] input = data.split("║");
    if (input.length != 3) {
      addError("Invalid Input");
      return;
    }
    chatmsg = input[0].trim();
    receiver = input[1];
    timestamp = input[2];
    wisperId = Integer.parseInt(receiver);
    setData(data);
    validate();
  }

  /**
   * Check if {@link PacketChatMessageToServer} has characters. Check if {@link
   * PacketChatMessageToServer} is shorter then 100 characters. In the case of an error it gets
   * added with {@link Packet#addError(String)}.
   */
  @Override
  public void validate() {
    if (chatmsg == null) {
      addError("No Message found");
      return;
    }
    if (chatmsg.length() > 100) {
      addError("Message to long. Maximum is 100 Characters.");
    }
    isExtendedAscii(chatmsg);
  }

  /**
   * Method that lets the Server react to the receiving of this packet. Check for errors in
   * validate. Check that the Client that has sent the packet is in a lobby. In the case of an error
   * it gets added with {@link Packet#addError(String)}. If there are no errors constructs a {@link
   * PacketChatMessageToClient}-Packet and send it to all player in the same lobby and constructs a
   * {link PacketChatMessageStatus}-Packet with "OK", when there are none errors. With errors the
   * packet has a list of the errors.
   */
  @Override
  public void processData() {
    String status;
    if (!hasErrors()) {
      Player client = ServerLogic.getPlayerList().getPlayer(getClientId());
      if (client == null) {
        addError("Not logged in");
      } else {
        int lobbyId = client.getCurLobbyId();
        if (lobbyId == 0) {
          addError("Must been in a Lobby to use the chat.");
        } else {
          String fullmessage = "[" + client.getUsername() + "-" + timestamp + "]  " + chatmsg;

          if(wisperId > 0){
            PacketChatMessageToClient sendMessage =
                    new PacketChatMessageToClient(getClientId(), fullmessage);
            sendMessage.sendToClient(wisperId);
          }
          PacketChatMessageToClient sendMessage =
              new PacketChatMessageToClient(getClientId(), fullmessage);
          sendMessage.sendToLobby(client.getCurLobbyId());
        }
      }
    }
    if (hasErrors()) {
      status = createErrorMessage();
    } else {
      status = "OK";
    }
    PacketChatMessageStatus sendMessage = new PacketChatMessageStatus(getClientId(), status);
    sendMessage.sendToClient(getClientId());
  }
}
