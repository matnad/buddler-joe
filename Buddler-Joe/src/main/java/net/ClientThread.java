package net;

import java.io.*;
import java.net.Socket;

public class ClientThread implements Runnable {

    private BufferedReader input;
    private PrintWriter output;
    private final int clientNo;
    private final Socket socket;

    ClientThread(Socket Client, int clientNo) {
        this.clientNo = clientNo;
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
                command = input.readLine();
                if (command == null) {
                    System.out.println("Client " + clientNo + " left");
                } else {
                    System.out.println("command sent was '" + command + "' by client No " + clientNo);
                    if (command.equals("ping")) {
                        sendToClient("pong");
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Client " + clientNo + " left");
            try{
                socket.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void sendToClient(String message) {
        output.println(message);
        output.flush();
    }
}