package net;

import net.lobbyhandling.Lobby;
import net.lobbyhandling.ServerLobbyList;
import net.packets.chat.PacketChatMessageToClient;
import net.packets.lobby.PacketCurLobbyInfo;
import net.playerhandling.ClientThread;
import net.playerhandling.Player;
import net.playerhandling.ServerPlayerList;
import net.packets.Packet;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class ServerLogic {

        private static ServerPlayerList playerList;
        private static ServerLobbyList lobbyList;
        private static HashMap<Integer, ClientThread> clientThreadMap;
        private int portValue;
        static ServerSocket serverSocket;

    /**
     * Basic server logic to create a new thread for every player which connects to the game
     * @param portValue the port on which the server runs on
     * @throws IOException
     */


    ServerLogic(int portValue) throws IOException {
            playerList = new ServerPlayerList();
            clientThreadMap = new HashMap<Integer, ClientThread>();
            this.portValue = portValue;
            serverSocket = new ServerSocket(portValue);
            lobbyList = new ServerLobbyList();
            System.out.println("Started Server");
        }

    /**
     * Method to wait for incoming players and then create a new thread for them.
     * @throws IOException
     */
    void waitForPlayers() throws IOException {
            int ClientId = 1;

            while (true) {
                Socket Client = serverSocket.accept();
                System.out.println("Client Arrived");
                System.out.println("Start Thread for "+ClientId);
                ClientThread task = new ClientThread(Client, ClientId);
                clientThreadMap.put(ClientId++,task);
                new Thread(task).start();
            }
        }

        private void kill() {
            try {
                serverSocket.close();
            } catch (IOException e) {
                System.out.println("Could not close ServerSocket");
            }
        }

        public static ServerPlayerList getPlayerList(){
            return playerList;
        }

        public static ServerLobbyList getLobbyList(){
        return lobbyList;
    }

        public static void sendPacket(int receiver, Packet packet){  //TODO: Check receiver and if he exists
            //System.out.println("reciver: " + receiver + " Packet: " + packet.getPacketType());
            //System.out.println("Data: "+ packet.getData());
            ClientThread ct = getThreadByClientId(receiver);
            if(ct != null){
                ct.sendToClient(packet);
            }
        }

        public static void sendPacketToLobby(int receiverLobby, Packet packet){

        }

        public static ClientThread getThreadByClientId(int clientId){
             return clientThreadMap.get(clientId);
        }


    /** Remove the player from the server and informed the the other players in the lobby.
     * @param clientId from the player who remove
     */
        public static void removePlayer(int clientId){

            Player player = ServerLogic.getPlayerList().getPlayer(clientId);
            int lobbyId = player.getCurLobbyId();
            int playerId = player.getClientId();

            //check if the client is in a lobby and remove it from the lobby
            Lobby lobby = ServerLogic.getLobbyList().getLobby(lobbyId);
        if(ServerLogic.getPlayerList().getPlayer(clientId).getCurLobbyId() > 0) {
            lobby.removePlayer(clientId);
        }
            player.setCurLobbyId(0);
            //close the thread which manage the client and delete the player from the playerlist
            clientThreadMap.remove(clientId);
            getPlayerList().removePlayer(clientId);
            //set the time when the player left the lobby
            String timestamp;
            SimpleDateFormat simpleFormat = new SimpleDateFormat("HH:mm");
            Date date = new Date();
            timestamp = simpleFormat.format(date);
            //send the message "[SERVER 'TIME']'username' left lobby" to the lobby
            PacketChatMessageToClient sendMessage = new PacketChatMessageToClient(playerId,"[SERVER-" + timestamp + "] " + player.getUsername()+" left lobby");
            sendMessage.sendToLobby(lobbyId);
            //send lobbyinfo to the other player in the lobby
            String info;
            info = "OK║" + ServerLogic.getLobbyList().getLobby(lobbyId).getPlayerNames();
            PacketCurLobbyInfo packetCurLobbyInfo = new PacketCurLobbyInfo(playerId,info);
            packetCurLobbyInfo.sendToLobby(lobbyId);
            //close the client's thread
            ServerLogic.getThreadByClientId(playerId).closeSocket();
    }
}
