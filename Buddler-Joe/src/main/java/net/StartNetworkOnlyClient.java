package net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class StartNetworkOnlyClient {
    private final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    private String ServerIP;
    private int PortValue;
    private ClientLogic clientLogic;

    private StartNetworkOnlyClient(){
        try {
//            System.out.println("Enter Server Name or IP: ");
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

    private void TakeInputAndAct() throws IOException{
        while (true){
            System.out.println("Command: ");
            String inputMessage = br.readLine();
            clientLogic.sendToServer(inputMessage);


        }
    }

    public static void main(String[] args) {
        StartNetworkOnlyClient client = new StartNetworkOnlyClient();
        try {
            client.TakeInputAndAct();
        } catch (IOException e ) {
            System.out.println("Buffer Reader does not exist");
        }

    }

    void kill() {
        System.exit(0);
    }
}