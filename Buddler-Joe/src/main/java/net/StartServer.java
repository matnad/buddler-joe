package net;

public class StartServer {

    private static ServerLogic socketServer;

    public static void main(String[] args) {
        socketServer = new ServerLogic();
        socketServer.start();

    }
}
