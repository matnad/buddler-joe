package net;

import entities.NetPlayer;
import net.packets.Packet;
import net.packets.Packet00Login;
import net.packets.Packet01Move;
import net.packets.Packet99Disconnect;
import org.joml.Vector3f;

import java.io.IOException;
import java.net.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class ServerLogic {
        private final int portValue;
        static ServerSocket serverSocket;


        ServerLogic(int portValue) throws IOException, ParseException {
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
