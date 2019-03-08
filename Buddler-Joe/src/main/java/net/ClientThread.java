package net;

import net.packets.PacketGetName;
import net.packets.PacketLogin;
import net.packets.PacketSetName;

import java.io.*;
import java.net.Socket;

public class ClientThread implements Runnable {

    private BufferedReader input;
    private PrintWriter output;
    private final int clientId;
    private final Socket socket;
    private ServerPlayerList playerList;

    ClientThread(Socket Client, int clientId, ServerPlayerList playerList) {
        this.playerList = playerList;
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
                        case LOGIN:
                            PacketLogin login = new PacketLogin(playerList, clientId, in[1].trim(), this);
                        case GETNM:
                            PacketGetName getName = new PacketGetName(playerList, in[1].trim(), clientId);
                        case GETNM:
                            PacketSetName setName = new PacketSetName(playerList, in[1].trim());

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
}