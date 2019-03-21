package net;

import net.packets.Packet;
import net.packets.lobby.PacketCreateLobbyStatus;
import net.packets.lobby.PacketCurLobbyInfo;
import net.packets.lobby.PacketJoinLobbyStatus;
import net.packets.lobby.PacketLobbyOverview;
import net.packets.login_logout.PacketLoginStatus;
import net.packets.name.PacketSendName;
import net.packets.name.PacketSetNameStatus;
import net.packets.pingpong.PacketPing;
import net.packets.pingpong.PacketPong;
import net.playerhandling.PingManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ClientLogic implements Runnable {

    private static PrintWriter output;
    private static BufferedReader input;
    private static StartNetworkOnlyClient clientGUI;
    private static Thread thread;
    private static Socket server;
    private int clientId;
    private static int counter = 1;
    private String username;
    PingManager pingManager;

    /**
     * ClientLogic to communicate with the server. Controls the input/output from/to the player. The constructor sets
     * the IP, port and clientGUI. It then starts a thread on this class.
     * @param IP of the server which is to be communicated with
     * @param Port of the server to which the client will be connected
     * @param clientGUI ?
     * @throws IOException
     */

    public ClientLogic(String IP, int Port, StartNetworkOnlyClient clientGUI) throws IOException {
        server = new Socket(IP, Port);
        output = new PrintWriter(server.getOutputStream(), false);
        input = new BufferedReader(new InputStreamReader(server.getInputStream()));
        thread = new Thread(this);
        thread.start();
        pingManager = new PingManager();
        new Thread(pingManager).start();
    }

    /**
     * Thread to run the ClientLogic on, calls the method waitforserver to start up.
     */

    @Override
    public void run() {
        try {
            waitForServer();
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
            System.out.println("Connection lost to server");
            try {
                server.close();
            } catch (IOException e1){
                e1.printStackTrace();
            }
        }
    }

    /**
     * Method to wait for incoming server messages. They then get parted up in a command and data part.
     * The command String determines the actions taken by the ClientLogic. The data will be passed on to the methods
     * if needed. Consequently the command is passed into the switch which then processes the data.
     * @throws IOException
     * @throws RuntimeException
     */

    private void waitForServer() throws IOException, RuntimeException {
        //firstLogin();
        while (true) {
            String in = input.readLine();
            if(in.length() < 6){
                System.out.println("No valid command has been sent by server");
                continue;
            }
            String command = in.substring(0,5);
            if(command == null) {
                System.out.println("Shutting down.");
                clientGUI.kill();
            }
            String data = in.substring(6);
            switch (Packet.lookupPacket(command)){
                case LOGIN_STATUS:
                    PacketLoginStatus p = new PacketLoginStatus(data);
                    p.processData();
                    break;
                case SEND_NAME:
                    PacketSendName sendName = new PacketSendName(data);
                    sendName.processData();
                    break;
                case SET_NAME_STATUS:
                    PacketSetNameStatus setName = new PacketSetNameStatus(data);
                    setName.processData();
                    break;
                case LOBBY_OVERVIEW:
                    PacketLobbyOverview pLO = new PacketLobbyOverview(data);
                    pLO.processData();
                    break;
                case CREATE_LOBBY_STATUS:
                    PacketCreateLobbyStatus  pcls = new PacketCreateLobbyStatus(data);
                    pcls.processData();
                    break;
                case JOIN_LOBBY_STATUS:
                    PacketJoinLobbyStatus pjls = new PacketJoinLobbyStatus(data);
                    pjls.processData();
                    break;
                case CUR_LOBBY_INFO:
                    PacketCurLobbyInfo pcli = new PacketCurLobbyInfo(data);
                    pcli.processData();
                    break;
                case PONG:
                    PacketPong packetPong = new PacketPong(data);
                    packetPong.processData();
                    break;
                case PING:
                    PacketPing packetPing = new PacketPing(data);
                    packetPing.processData();
                    //andere methode z.b packetPing.calculate diese ruft pingupdate() auf
                    break;
            }
        }
    }

    /**
     * Method to send a package to the server. Will transform the packet to a String here.
     * @param input The packet to be sent to the Server.
     */

    public static void sendToServer(String input) {
        output.println(input);
        output.flush();
    }

    public static void recommendName(String username){
        System.out.println("The username is already taken, we would recommend: " + username + "_" + counter++);
    }

    public PingManager getPingManager() {
        return pingManager;
    }
}