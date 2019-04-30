package net.packets.chat;

import java.text.SimpleDateFormat;
import java.util.Date;
import net.ServerLogic;
import net.packets.Packet;
import net.playerhandling.ServerPlayer;

/**
 * Packet that gets send from the Client to the Server, to send chat message from Client to a other
 * Client via Server. Packet-Code: CHATS
 *
 * @author Moritz Würth
 */
public class PacketChatMessageToServer extends Packet {

  private String chatmsg;
  private String timestamp;

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
    try {
    this.chatmsg = chatmsg.trim();
    } catch (NullPointerException e) {
      addError("There is no message.");
    }
    SimpleDateFormat simpleFormat = new SimpleDateFormat("HH:mm");
    Date date = new Date();
    timestamp = simpleFormat.format(date);
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
    if (input.length != 2) {
      addError("Invalid Input.");
      return;
    }
    chatmsg = input[0].trim();
    timestamp = input[1];
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
    if(hasErrors()) {
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
    String starter =
        "["
            + ServerLogic.getPlayerList().getPlayer(getClientId()).getUsername()
            + "-"
            + timestamp
            + "]";
    if (!hasErrors()) {

      ServerPlayer client = ServerLogic.getPlayerList().getPlayer(getClientId());
      if (client == null) {
        addError("Not logged in");
      } else {
        int lobbyId = client.getCurLobbyId();
        if (lobbyId == 0) {
          addError("Must been in a Lobby to use the chat.");
        } else {

          //  check if the player wants to whisper
          if (chatmsg.startsWith("@")) {
            int wisperId = ServerLogic.getPlayerList().getClientIdForWhisper(chatmsg);
            int usernameLength =
                ServerLogic.getPlayerList().getPlayer(getClientId()).getUsername().length();

            //  temporary solution
            chatmsg = chatmsg + "   ";
            //  check if the player wants whisper to himself
            if (chatmsg.length() > usernameLength + 1) {
              if (chatmsg
                      .substring(1, usernameLength + 1)
                      .equals(ServerLogic.getPlayerList().getPlayer(getClientId()).getUsername())
                  && !chatmsg.substring(usernameLength + 1, usernameLength + 2).equals("_")
                  && !Character.isDigit(chatmsg.charAt(usernameLength + 1))) {
                wisperId = -1;
              }
            }

            //  wisperId = -1 player don't exist in the lobby
            //  wisperId = -2 player sends message to all players
            if (-1 == wisperId) {
              PacketChatMessageToClient sendMessage =
                  new PacketChatMessageToClient(starter + "Username ist ungültig");
              sendMessage.sendToClient(getClientId());

            } else if (-2 == wisperId) {
              chatmsg = chatmsg.substring(4).trim();
              //            System.out.println(chatText);
              PacketChatMessageToClient broadcostmessage =
                  new PacketChatMessageToClient(
                      getClientId(),
                      starter
                          + "(to all from "
                          + ServerLogic.getPlayerList().getPlayer(getClientId()).getUsername()
                          + ") "
                          + chatmsg);
              broadcostmessage.sendToAllClients();
            } else {
              String userName = ServerLogic.getPlayerList().getPlayer(wisperId).getUsername();
              chatmsg = chatmsg.substring(userName.length() + 1);
              PacketChatMessageToClient sendMessage =
                  new PacketChatMessageToClient(getClientId(), starter + "(whispered)" + chatmsg);
              sendMessage.sendToClient(wisperId);

              PacketChatMessageToClient sendMessage2 =
                  new PacketChatMessageToClient(
                      getClientId(), starter + "(whispered to " + userName + ")" + chatmsg.trim());

              sendMessage2.sendToClient(getClientId());
            }

          } else {

            PacketChatMessageToClient sendMessage =
                new PacketChatMessageToClient(getClientId(), starter + chatmsg);
            sendMessage.sendToLobby(
                ServerLogic.getPlayerList().getPlayer(getClientId()).getCurLobbyId());
          }
        }
      }
    }

    if (hasErrors()) {
      status = createErrorMessage();
    } else {
      status = "OK";
    }
  }
}
