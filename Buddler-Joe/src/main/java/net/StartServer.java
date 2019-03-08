package net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class StartServer {

    private BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    private static int serverPort;
    private static boolean created;
    private static ServerLogic serverLogic;

    public StartServer() throws NumberFormatException {
//        System.out.println("Enter Server Port: ");
//        try {
//            serverPort = Integer.parseInt(br.readLine());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        serverPort = 11337;
    }

    public static void main(String[] args) {

        while (!created) {
            try {
                StartServer server = new StartServer();
                serverLogic = new ServerLogic(serverPort);
                serverLogic.waitForPlayers();
                created = true;
            } catch (IOException e) {
                System.out.println("Could not create Server - Use different Port - IO");
            } catch (NumberFormatException NFE) {
                System.out.println("Entered Server Port Incorrectly");
            } finally {
                if (serverLogic != null)
                    serverLogic.kill();
            }

        }


    }
}
