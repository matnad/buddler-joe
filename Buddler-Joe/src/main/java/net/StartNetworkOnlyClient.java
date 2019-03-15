package net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class StartNetworkOnlyClient {
    private static final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    private String ServerIP;
    private int PortValue;
    private static ClientLogic clientLogic;

    private StartNetworkOnlyClient(){
        try {
//            System.out.println("Enter Server name or IP: ");
//            ServerIP = br.readLine();
            ServerIP = "127.0.0.1";
//            System.out.println("Enter Port number for the Server: ");
//            PortValue = Integer.parseInt(br.readLine());
            PortValue = 11337;
            clientLogic = new ClientLogic(ServerIP, PortValue, this);
        } catch (IOException e){
            System.out.println("Buffer Reader does not exist");
        } catch (NumberFormatException e1) {
            System.out.println("Port can only be a number");
        }

    }

    private static void TakeInputAndAct() throws IOException{
        while (true){
            System.out.println("Command: ");
            String inputMessage = br.readLine();
            clientLogic.sendToServer(inputMessage);
        }
    }

    public static void main(String[] args) {
        StartNetworkOnlyClient client = new StartNetworkOnlyClient();
        try {
            firstLogin();
        } catch (IOException e){
            System.out.println("Server disconnected.");
        }
        try {
            client.TakeInputAndAct();
        } catch (IOException e ) {
            System.out.println("Buffer Reader does not exist");
        }

    }

    private static void firstLogin() throws IOException {
        System.out.println("Welcome player! What name would you like to give yourself? " + "\n" +
                "Your System says, that you are " + System.getProperty("user.name") +
                "." + "\n" + "Would you like to choose that name? Type Yes or " +
                "the username you would like to choose.");
        String answer = br.readLine();
        if(answer.substring(0,3).startsWith("Yes")){
            clientLogic.sendToServer("PLOGI " + System.getProperty("user.name"));
        } else {
            clientLogic.sendToServer("PLOGI " + answer);
        }
    }

    void kill() {
        System.exit(0);
    }
}