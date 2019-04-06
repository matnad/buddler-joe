package net;

import game.Game;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import net.packets.Packet;
import net.packets.PacketGameEnd;
import net.packets.PacketHistory;
import net.packets.PacketStartRound;
import net.packets.block.PacketBlockDamage;
import net.packets.chat.PacketChatMessageStatus;
import net.packets.chat.PacketChatMessageToClient;
import net.packets.items.PacketSpawnItem;
import net.packets.lists.PacketGamesOverview;
import net.packets.lobby.PacketCreateLobbyStatus;
import net.packets.lobby.PacketCurLobbyInfo;
import net.packets.lobby.PacketJoinLobbyStatus;
import net.packets.lobby.PacketLeaveLobbyStatus;
import net.packets.lobby.PacketLobbyOverview;
import net.packets.loginlogout.PacketLoginStatus;
import net.packets.loginlogout.PacketUpdateClientId;
import net.packets.map.PacketBroadcastMap;
import net.packets.name.PacketSendName;
import net.packets.name.PacketSetNameStatus;
import net.packets.pingpong.PacketPing;
import net.packets.pingpong.PacketPong;
import net.packets.playerprop.PacketPos;
import net.playerhandling.PingManager;

/**
 * Client side network logic
 *
 * <p>Communicates with the server via TCP socket. Sends, receives and processes incoming and
 * outgoing messages. It also activates the <code>PingManager</code> to send pings to the server in
 * a certain frequency.
 */
public class ClientLogic implements Runnable {

  private static volatile boolean disconnectFromServer;
  private static PrintWriter output;
  private static BufferedReader input;
  private static Socket server;
  private static PingManager pingManager;

  private static boolean connected;

  /**
   * ClientLogic to communicate with the server. Controls the input/output from/to the player. The
   * constructor sets the IP and port. It then starts a thread on this class.
   *
   * @param ip of the server which is to be communicated with
   * @param port of the server to which the client will be connected
   * @throws IOException when socket fails
   */
  ClientLogic(String ip, int port) throws IOException {
    // Open socket and create buffers
    server = new Socket(ip, port);
    output = new PrintWriter(server.getOutputStream(), false);
    input = new BufferedReader(new InputStreamReader(server.getInputStream()));
    disconnectFromServer = false;

    // Run thread
    Thread thread = new Thread(this);
    thread.start();

    // Start ping manager to survey the connection responsiveness
    pingManager = new PingManager();
    new Thread(pingManager).start();

    // Connected
    if (input != null && output != null) {
      connected = true;
      Game.setConnectedToServer(true);
    }
  }

  /**
   * Method to send a package to the server. Will transform the packet to a String here.
   *
   * @param packet The packet to be sent to the Server.
   */
  public static void sendToServer(Packet packet) {
    if (Game.isConnectedToServer()) {
      output.println(packet.toString());
      output.flush();
    }
  }

  public static PingManager getPingManager() {
    return pingManager;
  }

  /**
   * Returns the Socket.
   *
   * @return The server as a Socket object
   */
  public static Socket getServer() {
    return server;
  }

  /** Thread to run the ClientLogic on, calls the method waitforserver to start up. */
  @Override
  public void run() {
    try {
      waitForServer();
    } catch (IOException | RuntimeException e) {
      e.printStackTrace();
      System.out.println("Connection lost to server");
      try {
        server.close();
      } catch (IOException e1) {
        e1.printStackTrace();
      }
    }
    System.out.println(
        "Connection to the server timed out or was interrupted. Socket has been closed.");
  }

  /**
   * Method to wait for incoming server messages. They then get parted up in a code and data part.
   * The code String determines the actions taken by the ClientLogic. The data will be passed on to
   * the methods if needed. Consequently the code is passed into the switch which then processes the
   * data.
   *
   * @throws IOException when the socket fails
   * @throws RuntimeException when something unexpected happens
   */
  private void waitForServer() throws IOException, RuntimeException {
    while (!disconnectFromServer) {
      String in;
      try {
        in = input.readLine();
      } catch (SocketException e) {
        System.out.println("\nThe connection to the server has been closed!");
        server.close();
        break;
      }

      // Something went wrong on the server side
      if (in == null) {
        continue;
      }

      // Message too short
      if (in.length() < 5) {
        System.out.println(in + " is not a valid message from the server.");
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

      // Create the correct packet depending on message code
      Packet p = null;
      switch (Packet.lookupPacket(code)) {
        case LOGIN_STATUS:
          p = new PacketLoginStatus(data);
          break;
        case UPDATE_CLIENT_ID:
          p = new PacketUpdateClientId(data);
          break;
        case SEND_NAME:
          p = new PacketSendName(data);
          break;
        case SET_NAME_STATUS:
          p = new PacketSetNameStatus(data);
          break;
        case LOBBY_OVERVIEW:
          p = new PacketLobbyOverview(data);
          break;
        case CREATE_LOBBY_STATUS:
          p = new PacketCreateLobbyStatus(data);
          break;
        case JOIN_LOBBY_STATUS:
          p = new PacketJoinLobbyStatus(data);
          break;
        case CUR_LOBBY_INFO:
          p = new PacketCurLobbyInfo(data);
          break;
        case LEAVE_LOBBY_STATUS:
          p = new PacketLeaveLobbyStatus(data);
          break;
        case CHAT_MESSAGE_TO_CLIENT:
          p = new PacketChatMessageToClient(data);
          break;
        case CHAT_MESSAGE_STATUS:
          p = new PacketChatMessageStatus(data);
          break;
        case PING:
          p = new PacketPing(data);
          break;
        case PONG:
          p = new PacketPong(data);
          break;
        case POSITION_UPDATE:
          p = new PacketPos(data);
          break;
        case BLOCK_DAMAGE:
          p = new PacketBlockDamage(data);
          break;
        case FULL_MAP_BROADCAST:
          p = new PacketBroadcastMap(data);
          break;
        case SPAWN_ITEM:
          p = new PacketSpawnItem(data);
          break;
        case GAMES_OVERVIEW:
          p = new PacketGamesOverview(data);
          break;
        case START:
          p = new PacketStartRound();
          break;
        case GAME_OVER:
          p = new PacketGameEnd();
          break;
        case HISTORY:
          p = new PacketHistory(data);
          break;
        default:
      }
      if (p != null) {
        p.processData();
      }
    }
  }

  public static boolean isConnected() {
    return connected;
  }

  public static boolean isDisconnectFromServer() {
    return disconnectFromServer;
  }

  /**
   * A method to disconnect from the server.
   * @param disconnectFromServer The boolean if to be disconnected
   */

  public static void setDisconnectFromServer(boolean disconnectFromServer) {
    ClientLogic.disconnectFromServer = disconnectFromServer;
    try {
      server.close();
    } catch (IOException e) {
      System.out.println("Problem closing connection to server.");
    }
  }
}
