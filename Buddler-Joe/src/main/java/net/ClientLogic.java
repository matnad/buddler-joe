package net;

import bin.Game;
import entities.NetPlayer;
import net.packets.Packet;
import net.packets.Packet00Login;
import net.packets.Packet01Move;
import net.packets.Packet99Disconnect;
import org.joml.Vector3f;

import java.io.IOException;
import java.net.*;


public class ClientLogic extends Thread {

    private InetAddress ipAddress;
    private DatagramSocket socket;
    private bin.Game game;

    private boolean running = false;

    public ClientLogic(Game game, String ipAddress) {
        this.game = game;

        try {
            this.socket = new DatagramSocket();
            this.ipAddress = InetAddress.getByName(ipAddress);
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void run() {
        System.out.println("client socket running");
        this.running = true;
        while (true) {
            byte[] data = new byte[1024];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }

            parsePacket(packet.getData(), packet.getAddress(), packet.getPort());
//            String message = new String(packet.getData());
//            System.out.println("SERVER ["+packet.getAddress().getHostAddress()+":"+packet.getPort()+"]> " + message);

        }
    }

    private void parsePacket(byte[] data, InetAddress address, int port) {
        String message = new String(data).trim();
        Packet.PacketTypes type = Packet.lookupPacket(message.substring(0,2));
        Packet packet = null;
        switch (type) {
            default:
            case INVALID:
                break;
            case LOGIN:
                packet = new Packet00Login(data);
                System.out.println("["+address.getHostAddress()+":"+port+"] "+ ((Packet00Login) packet).getUsername()+" has joined the world...");
                NetPlayer player = new NetPlayer(null, new Vector3f(50,0,0),
                        0, 0, 0, 2.5f, null, 0, ((Packet00Login) packet).getUsername(),
                        ((Packet00Login) packet).getModel(), ((Packet00Login) packet).getTexture(), ((Packet00Login) packet).getModelSize());
                game.addNetPlayer(player);
                break;
            case DISCONNECT:
                packet = new Packet99Disconnect(data);
                System.out.println("["+address.getHostAddress()+":"+port+"] "+ ((Packet99Disconnect) packet).getUsername()+" has left the world...");
                game.removeNetPlayer(((Packet99Disconnect) packet).getUsername());
                break;
            case MOVE:
                packet = new Packet01Move(data);
                handleMove((Packet01Move) packet);
        }
    }

    private void handleMove(Packet01Move packet) {
        boolean found = false;
        for (NetPlayer loadedNetPlayer : game.getLoadedNetPlayers()) {
            if (loadedNetPlayer.getUsername().equals(packet.getUsername())) {
                found = true;
                loadedNetPlayer.setPosition(packet.getMoveCoords());
                loadedNetPlayer.setRotX(packet.getRotX());
                loadedNetPlayer.setRotY(packet.getRotY());
                loadedNetPlayer.setRotZ(packet.getRotZ());
//                System.out.println(""+loadedNetPlayer.getPosition());
            }
        }
//        if(!found && !packet.getUsername().equals(game.getUsername())) {
//            NetPlayer player = new NetPlayer(null, new Vector3f(50,0,0),
//                    0, 0, 0, 2.5f, null, 0, packet.getUsername());
//            game.addNetPlayer(player);
//        }
    }



    public void sendData(byte[] data) {
        DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, 11337);
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public InetAddress getIpAddress() {
        return ipAddress;
    }

    public boolean isRunning() {
        return running;
    }
}
