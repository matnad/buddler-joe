package net;

public class TestServer {
    public StartServer server;

    public TestServer() {
        this.server = new StartServer(11337);
        server.startServer();
    }

    public StartServer getServer() {
        return server;
    }
}
