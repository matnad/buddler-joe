package net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import net.packets.chat.PacketChatMessageToServer;
import net.packets.lobby.*;
import net.packets.login_logout.PacketDisconnect;
import net.packets.login_logout.PacketLogin;
import net.packets.name.PacketSetName;
import net.playerhandling.PingManager;

public class StartNetworkOnlyClient {
    private static final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    private static String ServerIP;
    private static int PortValue;
    private static ClientLogic clientLogic;

    private StartNetworkOnlyClient(){
        try {
            ServerIP = "127.0.0.1";
            PortValue = 11337;
            clientLogic = new ClientLogic(ServerIP, PortValue);
        } catch (IOException e){
            System.out.println("Buffer Reader does not exist");
        } catch (NumberFormatException e1) {
            System.out.println("Port can only be a number");
        }

    }

    private static void TakeInputAndAct() throws IOException{
        while (true){
//            System.out.println("Command: ");
            String inputMessage = br.readLine();
            if(inputMessage.equals("help")) {
                System.out.println("You can use these commands:\n" +
                        "ping - Display your average ping to the server over the last 10 seconds\n"+
                        "login <username> - Login attempt with the server\n" +
                        "name <username> - Change your username\n" +
                        "C <message> - Chat with users in your lobby\n" +
                        "lobbies - Get a list of all lobbies on the server\n"+
                        "info - Get info about the lobby you are currently in\n"+
                        "create <lobby name> - Create lobby with specified name\n"+
                        "join <lobby name> - Join lobby with specified name\n"+
                        "leave - Leave your current lobby\n"+
                        "leave - Leave your current lobby\n"+
                        "connect - reconnect if the socket has been closed, display connection info otherwise\n" +
                        "disconnect - Disconnect from the server\n" +
                        "help - Display this message");
            } else if (inputMessage.equals("ping")) {
                System.out.println("Ping to the server over the last 10 packets: "
                        + ClientLogic.getPingManager().getPing() + " ms");
            } else if (inputMessage.startsWith("name ") && inputMessage.length() > 5) {
                PacketSetName p = new PacketSetName(inputMessage.substring(5));
                p.sendToServer();
            } else if (inputMessage.equals("lobbies")) {
                PacketGetLobbies p = new PacketGetLobbies();
                p.sendToServer();
            } else if (inputMessage.startsWith("join ") && inputMessage.length() > 5) {
                PacketJoinLobby p = new PacketJoinLobby(inputMessage.substring(5));
                p.sendToServer();
            } else if(inputMessage.equals("leave")) {
                PacketLeaveLobby p = new PacketLeaveLobby();
                p.sendToServer();
            } else if (inputMessage.startsWith("create ") && inputMessage.length() > 7) {
                PacketCreateLobby p = new PacketCreateLobby(inputMessage.substring(7));
                p.sendToServer();
            } else if(inputMessage.startsWith("login ") && inputMessage.length() > 6) {
                PacketLogin p = new PacketLogin(inputMessage.substring(6));
                p.sendToServer();
            } else if(inputMessage.equals("info")) {
                PacketGetLobbyInfo p = new PacketGetLobbyInfo();
                p.sendToServer();
            } else if(inputMessage.startsWith("C ") && inputMessage.length() > 2) {
                PacketChatMessageToServer p = new PacketChatMessageToServer(inputMessage.substring(2));
                p.sendToServer();
            } else if (inputMessage.equals("disconnect")) {
                PacketDisconnect p = new PacketDisconnect();
                p.sendToServer();
                ClientLogic.getServer().close();
            } else if (inputMessage.equals("connect")) {
                if(ClientLogic.getServer().isClosed()) {
                    //reconnect
                    clientLogic = new ClientLogic(ServerIP, PortValue);
                    if(ClientLogic.getServer().isClosed()) {
                        System.out.println("Connection could not be established. Exiting program.");
                        System.exit(-1);
                    } else {
                        System.out.println("Connection to the server re-established. Socket status: "+ClientLogic.getServer());
                        try {
                            firstLogin();
                        } catch (IOException | StringIndexOutOfBoundsException e){
                            System.out.println("Server disconnected.");
                        }
                    }
                } else {
                    System.out.println("You are still connected to the server. Socket status: "+ClientLogic.getServer());
                }
            } else {
                ClientLogic.sendToServer(inputMessage);
            }
        }
    }

    public static void main(String[] args) {
        StartNetworkOnlyClient client = new StartNetworkOnlyClient();
        try {
            firstLogin();
        } catch (IOException | StringIndexOutOfBoundsException e){
            System.out.println("Server disconnected.");
        }
        try {
            TakeInputAndAct();
        } catch (IOException e ) {
            System.out.println("Buffer Reader does not exist");
        }

    }

    private static void firstLogin() throws IOException, StringIndexOutOfBoundsException {
        System.out.println("Welcome player! What name would you like to give yourself? " + "\n" +
                "Your System says, that you are " + System.getProperty("user.name") +
                "." + "\n" + "Would you like to choose that name? Type Yes or " +
                "the username you would like to choose.\n");
        String answer = br.readLine();
        if(answer.trim().toLowerCase().equals("yes")){
            PacketLogin p = new PacketLogin(System.getProperty("user.name"));
            p.sendToServer();
        } else {
            PacketLogin p = new PacketLogin(answer);
            p.sendToServer();
        }
    }

    void kill() {
        System.exit(0);
    }
}