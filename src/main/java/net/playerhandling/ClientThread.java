package net.playerhandling;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import net.ServerLogic;
import net.packets.Packet;
import net.packets.block.PacketBlockDamage;
import net.packets.chat.PacketChatMessageToServer;
import net.packets.gamestatus.PacketGetHistory;
import net.packets.gamestatus.PacketReady;
import net.packets.items.PacketItemUsed;
import net.packets.items.PacketSpawnItem;
import net.packets.life.PacketLifeStatus;
import net.packets.lists.PacketHighscore;
import net.packets.lists.PacketPlayerList;
import net.packets.lobby.PacketCreateLobby;
import net.packets.lobby.PacketGetLobbies;
import net.packets.lobby.PacketGetLobbyInfo;
import net.packets.lobby.PacketJoinLobby;
import net.packets.lobby.PacketJoinLobbyStatus;
import net.packets.lobby.PacketLeaveLobby;
import net.packets.loginlogout.PacketDisconnect;
import net.packets.loginlogout.PacketLogin;
import net.packets.name.PacketGetName;
import net.packets.name.PacketSetName;
import net.packets.pingpong.PacketPing;
import net.packets.pingpong.PacketPong;
import net.packets.playerprop.PacketPos;
import net.packets.playerprop.PacketVelocity;

/**
 * One thread for each client. This thread contains and manages the input and output streams to
 * communicate with the client. Will receive messages from their client and process them. Can send
 * messages to their client. It also activates the <code>PingManager</code> to send pings to the
 * client in a certain frequency.
 */
// Client and Server code can be similar, but we don't want shared classes
@SuppressWarnings("Duplicates")
public class ClientThread implements Runnable {

  private final int clientId;
  private final Socket socket;
  private final PingManager pingManager;
  private BufferedReader input;
  private PrintWriter output;

  /**
   * Create input and output streams to communicate with the client over the specified socket. Also
   * start the ping manager to survey the connection.
   *
   * @param clientSocket TCP connection socket to the server
   * @param clientId unique identifier of the client
   */
  public ClientThread(Socket clientSocket, int clientId) {
    this.clientId = clientId;
    this.socket = clientSocket;
    System.out.println("Client details: " + clientSocket.toString());
    try {
      input =
          new BufferedReader(
              new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
      output =
          new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));

    } catch (IOException e) {
      System.err.println("Streams not set up for Client.");
    }
    pingManager = new PingManager(clientId);
    new Thread(pingManager).start();
  }

  /**
   * Called when the client thread is started.
   *
   * <p>Contains the logic of the client to server communication. Receive and process messages.
   */
  @Override
  public void run() {
    while (true) {
      try {

        String in = input.readLine();

        // Message too short
        if (in.length() < 5) {
          System.out.println(in + " is not a valid message from the client.");
          continue;
        }

        String code = in.substring(0, 5);
        // There is a whitespace between code and data which we deliberately ignore here
        String data;
        // Check if the message has a data component
        if (in.length() < 7) {
          data = "";
        } else {
          data = in.substring(6);
        }
        Packet p = null;
        switch (Packet.lookupPacket(code)) {
          case LOGIN:
            PacketLogin login = new PacketLogin(clientId, data);
            login.processData();
            if (!login.hasErrors()) {
              System.out.println(
                  "ServerPlayer "
                      + ServerLogic.getPlayerList().getUsername(clientId)
                      + " has connected.");
            }
            break;
          case GET_NAME:
            p = new PacketGetName(clientId, data);
            break;
          case SET_NAME:
            p = new PacketSetName(clientId, data);
            break;
          case DISCONNECT:
            p = new PacketDisconnect(clientId);
            break;
          case GET_LOBBIES:
            p = new PacketGetLobbies(clientId);
            break;
          case CREATE_LOBBY:
            p = new PacketCreateLobby(clientId, data);
            break;
          case CREATE_LOBBY_STATUS:
            p = new PacketJoinLobby(clientId, data);
            break;
          case JOIN_LOBBY:
            p = new PacketJoinLobby(clientId, data);
            break;
          case JOIN_LOBBY_STATUS:
            p = new PacketJoinLobbyStatus(clientId, data);
            break;
          case GET_LOBBY_INFO:
            p = new PacketGetLobbyInfo(clientId);
            break;
          case LEAVE_LOBBY:
            p = new PacketLeaveLobby(clientId);
            break;
          case CHAT_MESSAGE_TO_SERVER:
            p = new PacketChatMessageToServer(clientId, data);
            break;
          case PING:
            p = new PacketPing(clientId, data);
            break;
          case PONG:
            p = new PacketPong(clientId, data);
            break;
          case POSITION_UPDATE:
            p = new PacketPos(clientId, data);
            break;
          case PLAYER_VELOCITY:
            p = new PacketVelocity(clientId, data);
            break;
          case BLOCK_DAMAGE:
            p = new PacketBlockDamage(clientId, data);
            break;
          case SPAWN_ITEM:
            p = new PacketSpawnItem(clientId, data);
            break;
          case PLAYERLIST:
            p = new PacketPlayerList(clientId);
            break;
          case HIGHSCORE:
            p = new PacketHighscore(clientId);
            break;
          case ITEM_USED:
            p = new PacketItemUsed(clientId, data);
            break;
          case READY:
            p = new PacketReady(clientId);
            break;
          case GET_HISTORY:
            p = new PacketGetHistory(clientId);
            break;
          case LIFE_STATUS:
            p = new PacketLifeStatus(clientId, data);
            break;
          default:
        }
        if (p != null) {
          p.processData();
        }

      } catch (IOException e) {
        System.out.println("Client " + clientId + " left");
        // Properly disconnect the user
        break;
      } catch (NullPointerException e) {
        // They should not happen, but if they do, we don't care
      } catch (Exception e) {
        // We assume any other exception is fatal and properly disconnect the user
        break;
      }
    }
    // If the thread dies or a fatal exception occurs, disconnect the player and close the socket
    ServerLogic.removePlayer(clientId);
    try {
      socket.close();
    } catch (IOException e1) {
      e1.printStackTrace();
    }
  }

  /**
   * The packet generates the final message string and sends it to the stream.
   *
   * @param packet packet to send to the client
   */
  public void sendToClient(Packet packet) {
    output.println(packet.toString());
    output.flush();
  }

  public int getClientId() {
    return clientId;
  }

  public PingManager getPingManager() {
    return pingManager;
  }

  /** Close the connection to the client. */
  public void closeSocket() {
    try {
      socket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
