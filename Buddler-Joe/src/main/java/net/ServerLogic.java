package net;

import net.packets.Packet;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class ServerLogic {

        private static ServerPlayerList playerList;
        private static HashMap<Integer, ClientThread> clientThreadMap;
        private int portValue;
        static ServerSocket serverSocket;

    /**
     * Basic server logic to create a new thread for every player which connects to the game
     * @param portValue the port on which the server runs on
     * @throws IOException
     */


    ServerLogic(int portValue) throws IOException {
            this.playerList = new ServerPlayerList();
            this.clientThreadMap = new HashMap<Integer, ClientThread>();
            this.portValue = portValue;
            serverSocket = new ServerSocket(portValue);
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
                ClientId++;
                clientThreadMap.put(ClientId,task);
                new Thread(task).start();
            }
        }

        public void kill() {
            try {
                serverSocket.close();
            } catch (IOException e) {
                System.out.println("Could not close ServerSocket");
            }
        }

        public static ServerPlayerList getPlayerList(){
            return playerList;
        }

        public static void sendPacket(int receiver, Packet packet){
            playerList.searchThread(receiver).sendToClient(packet.getData());
        }

        public static void sendPacketToLobby(int receiverLobby, Packet packet){

        }

    public ClientThread getThreadByClientId(int clientId){
        return clientThreadMap.get(clientId);
    }

    }
