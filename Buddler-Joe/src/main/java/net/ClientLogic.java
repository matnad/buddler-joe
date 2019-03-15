package net;

import net.packets.Packet;
import net.packets.lobby.PacketCreateLobbyStatus;
import net.packets.lobby.PacketCurLobbyInfo;
import net.packets.lobby.PacketJoinLobbyStatus;
import net.packets.lobby.PacketLobbyOverview;
import net.packets.login_logout.PacketLoginStatus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import static net.packets.Packet.PacketTypes.LOGIN;


public class ClientLogic implements Runnable {

    private static PrintWriter output;
    private static BufferedReader input;
    private static StartNetworkOnlyClient clientGUI;
    private static Thread thread;
    private static Socket server;
    private int clientId;

    /**
     * ClientLogic to communicate with the server. Controls the input/output from/to the player
     * @param IP of the server which is to be communicated with
     * @param Port of the server
     * @param clientGUI ?
     * @throws IOException
     */

    public ClientLogic(String IP, int Port, StartNetworkOnlyClient clientGUI) throws IOException {
        server = new Socket(IP, Port);
        output = new PrintWriter(server.getOutputStream(), false);
        input = new BufferedReader(new InputStreamReader(server.getInputStream()));

        // start thread
        thread = new Thread(this);
        thread.start();
    }

    /**
     * Thread to run the ClientLogic on, waits until a message is incoming
     */

    @Override
    public void run() {
        try {
            waitforserver();
        } catch (IOException | RuntimeException e) {
            e.printStackTrace();
            System.out.println("Connection lost to server");
        }
    }

    /**
     * Method to wait for the server connection until one is established
     * @throws IOException
     * @throws RuntimeException
     */

    private void waitforserver() throws IOException, RuntimeException {
        while (true) {
            String in = input.readLine();
            if(in.equals("")){
                continue;
            }
            //System.out.println("command:" + in);
            String command = in.substring(0,5);
            if(command == null) {
                System.out.println("Shutting down.");
                clientGUI.kill();
            }
            String data = in.substring(6);
            //System.out.println("data:"+ data + "\n------------------------------------------------");
            switch (Packet.lookupPacket(command)){
                case LOGIN_STATUS:
                    PacketLoginStatus p = new PacketLoginStatus(data);
                    p.processData();
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
            }
        }
    }

    public static void sendToServer(String message) {
        output.println(message);
        output.flush();
    }


}