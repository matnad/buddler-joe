package net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerLogic {
        private int portValue;
        static ServerSocket serverSocket;


        ServerLogic(int portValue) throws IOException {
            this.portValue = portValue;
            serverSocket = new ServerSocket(portValue);
            System.out.println("Started Server");
        }

        void waitForPlayers() throws IOException {
            int ClientNo = 1;

            while (true) {
                Socket Client = serverSocket.accept();
                System.out.println("Client Arrived");
                System.out.println("Start Thread for "+ClientNo);
                ClientThread task = new ClientThread(Client, ClientNo);
                ClientNo++;
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

    }
