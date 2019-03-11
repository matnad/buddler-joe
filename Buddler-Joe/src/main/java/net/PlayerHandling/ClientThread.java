package net.PlayerHandling;

import net.ServerLogic;
import net.packets.Name.PacketGetName;
import net.packets.LoginLogout.PacketLogin;
import net.packets.Name.PacketSetName;

import java.io.*;
import java.net.Socket;

public class ClientThread implements Runnable {

    private BufferedReader input;
    private PrintWriter output;
    private final int clientId;
    private final Socket socket;
    private boolean connected;

    public ClientThread(Socket Client, int clientId) {
        this.clientId = clientId;
        this.socket = Client;

        // So we can see what unique clients have joined
        System.out.println("Client details: "+Client.toString());

        // create streams
        try {
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

        } catch (IOException e) {
            System.err.println("Streams not set up for Client.");
        }
    }

    @Override
    public void run() {
        String command = "";
        try{
            while (command != null){
                String[] in = input.readLine().split(" ");
                command = in[0];
                if (command == null) {
                    System.out.println("Client " + clientId + " left");
                } else {
                    System.out.println("command sent was '" + command + "' by client No " + clientId);
                    switch(command){
                        case "PLOGI":
                            if(!connected){
                                PacketLogin login = new PacketLogin(clientId, in[1].trim());
                                System.out.println("Player " + ServerLogic.getPlayerList().searchName(clientId) + " has connected.");
                                connected = true;
                            } else {
                                continue;
                            }
                        case "GETNM":
                            PacketGetName getName = new PacketGetName(clientId, in[1].trim());
                        case "SETNM":
                            PacketSetName setName = new PacketSetName(clientId, in[1].trim());

                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Client " + clientId + " left");
            try{
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    public void sendToClient(String message) {
        output.println(message);
        output.flush();
    }

    public int getClientId() {
        return clientId;
    }



}