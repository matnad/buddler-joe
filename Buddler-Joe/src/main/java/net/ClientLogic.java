package net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


public class ClientLogic implements Runnable {

    private PrintWriter output;
    private BufferedReader input;
    private StartNetworkOnlyClient clientGUI;
    private Thread thread;
    private Socket server;

    public ClientLogic(String IP, int Port, StartNetworkOnlyClient clientGUI) throws IOException {
        server = new Socket(IP, Port);
        output = new PrintWriter(server.getOutputStream(), false);
        input = new BufferedReader(new InputStreamReader(server.getInputStream()));
        this.clientGUI = clientGUI;
        // start thread
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        try {
            waitforserver();
        } catch (IOException | RuntimeException e) {
            System.out.println("Connection lost to server");
        }
    }

    private void waitforserver() throws IOException, RuntimeException {
        while (true) {
            String serverReply = input.readLine();
            if(serverReply == null) {
                System.out.println("Shutting down.");
                clientGUI.kill();
            }
            System.out.println("Server Reply: "+serverReply);
        }
    }

    void sendToServer(String message) {
        output.println(message);
        output.flush();
    }


}