package net.playerhandling;

import net.ServerLogic;
import net.packets.Packet;
import net.packets.lobby.*;
import net.packets.login_logout.PacketDisconnect;
import net.packets.name.PacketGetName;
import net.packets.login_logout.PacketLogin;
import net.packets.name.PacketSetName;
import net.packets.pingpong.PacketPing;
import net.packets.pingpong.PacketPong;

import java.io.*;
import java.net.Socket;

public class ClientThread implements Runnable {

    private BufferedReader input;
    private PrintWriter output;
    private final int clientId;
    private final Socket socket;

    public ClientThread(Socket Client, int clientId) {
        this.clientId = clientId;
        this.socket = Client;
        System.out.println("Client details: "+Client.toString());
        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

        } catch (IOException e) {
            System.err.println("Streams not set up for Client.");
        }
        //hier PingManager aufrufen
        //thread starten PingManager pingManager = new PingManager()
        //pingManager.start()
    }

    @Override
    public void run() {
            while (true) {
                try {
                    String in = input.readLine();
                if (in.length() < 5) {
                    System.out.println("No valid command has been sent by player No " + clientId);
                    continue;
                }
                String command = in.substring(0, 5);
                String data = in.substring(5).trim();
                System.out.println("command sent was '" + command + "' by client No " + clientId);
                switch (Packet.lookupPacket(command)) {
                    case LOGIN:
                        PacketLogin login = new PacketLogin(clientId, data);
                        login.processData();
                        if (!login.hasErrors()) {
                            System.out.println("Player " + ServerLogic.getPlayerList().getUsername(clientId) + " has connected.");
                        }
                        break;
                    case GET_NAME:
                        PacketGetName getName = new PacketGetName(clientId, data);
                        getName.processData();
                        break;
                    case SET_NAME:
                        PacketSetName setName = new PacketSetName(clientId, data);
                        setName.processData();
                        break;
                    case DISCONNECT:
                        PacketDisconnect disconnect = new PacketDisconnect(clientId, data);
                        disconnect.processData();
                        break;
                    case GET_LOBBIES:
                        PacketGetLobbies getLobbies = new PacketGetLobbies(clientId);
                        getLobbies.processData();
                        break;
                    case CREATE_LOBBY:
                        PacketCreateLobby createLobby = new PacketCreateLobby(clientId, data);
                        createLobby.processData();
                        break;
                    case CREATE_LOBBY_STATUS:
                        PacketJoinLobby joinLobby = new PacketJoinLobby(clientId, data);
                        joinLobby.processData();
                        break;
                    case JOIN_LOBBY:
                        PacketJoinLobby packetJoinLobby = new PacketJoinLobby(clientId, data);
                        packetJoinLobby.processData();
                        break;
                    case JOIN_LOBBY_STATUS:
                        PacketJoinLobbyStatus packetJoinLobbyStatus = new PacketJoinLobbyStatus(clientId, data);
                        packetJoinLobbyStatus.processData();
                        break;
                    case CUR_LOBBY_INFO:
                        PacketCurLobbyInfo packetCurLobbyInfo = new PacketCurLobbyInfo(clientId, data);
                        packetCurLobbyInfo.processData();
                        break;
                    case PING:
                        PacketPing packetPing = new PacketPing(clientId, data);
                        packetPing.processData();
                        break;
                    case PONG:
                        PacketPong packetPong = new PacketPong(clientId, data);
                        packetPong.processData();
                    default:
                        break;
                }
            } catch(IOException | NullPointerException e){
                System.out.println("Client " + clientId + " left");
                try {
                    socket.close();
                    break;
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public void sendToClient(Packet packet) {
        output.println(packet.toString());
        output.flush();
    }

    public int getClientId() {
        return clientId;
    }



}