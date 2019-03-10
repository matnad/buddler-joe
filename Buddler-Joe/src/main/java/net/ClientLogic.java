package net;

import net.packets.PacketLoginStatus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


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
        this.clientGUI = clientGUI;
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
            String command = "";
            String[] in = input.readLine().split(" ");
            if(in == null) {
                System.out.println("Shutting down.");
                clientGUI.kill();
            }
//            command = in[0];
//            switch (command){
//                case "PLOGS":
//                    PacketLoginStatus status = new PacketLoginStatus(clientId,in[1].trim());
//            }
            System.out.println("Server Reply: "+command);
        }
    }

    public static void sendToServer(String message) {
        output.println(message);
        output.flush();
    }


}