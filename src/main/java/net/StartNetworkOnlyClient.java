package net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import net.packets.Packet;
import net.packets.chat.PacketChatMessageToServer;
import net.packets.gamestatus.PacketGetHistory;
import net.packets.lists.PacketPlayerList;
import net.packets.lobby.PacketCreateLobby;
import net.packets.lobby.PacketLeaveLobby;
import net.packets.loginlogout.PacketDisconnect;
import net.packets.loginlogout.PacketLogin;
import net.packets.name.PacketSetName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The client-side interface to communicate with the server.
 *
 * <p>Provides a simple console-based Interface to communicate.
 *
 * <p>Will start and manage the client network logic.
 *
 * @see ClientLogic
 */
public class StartNetworkOnlyClient implements Runnable {
  public static final Logger logger = LoggerFactory.getLogger(StartNetworkOnlyClient.class);
  private static final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
  private static String serverIp;
  private static int serverPort;

  private static Thread networkThread;

  /**
   * Start the client logic and pass ip + port.
   *
   * @see ClientLogic
   */
  public StartNetworkOnlyClient() {
    try {
      new ClientLogic(serverIp, serverPort);
    } catch (IOException e) {
      System.out.println(
          "Buffer Reader does not exist. Can't find a server at the specified location.");
    } catch (NumberFormatException e1) {
      System.out.println("Port can only be a number");
    }
  }

  /**
   * Start the User Interface. You can pass an IP and Port to try and connect to. Otherwise defaults
   * will be used.
   *
   * @param args "server ip:server port" will be validated and set to default values in case of
   *     errors
   */
  public static void main(String[] args) {
    networkThread = new Thread(() -> StartNetworkOnlyClient.startWith("127.0.0.1", 11337));
    networkThread.setName("Network Client");
    networkThread.start();
  }

  /**
   * Start the Network only client with a certain IP and serverPort.
   *
   * @param serverPort port to connect on
   * @param serverIp ip or URL to connect to
   */
  public static void startWith(String serverIp, int serverPort) {
    StartNetworkOnlyClient.serverIp = serverIp;
    StartNetworkOnlyClient.serverPort = serverPort;

    // Start Interface
    new StartNetworkOnlyClient();
  }
  
  @Override
  public void run() {}
}
