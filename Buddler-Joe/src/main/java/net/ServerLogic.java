package net;

import bin.Game;
import entities.NetPlayer;
import net.packets.Packet;
import net.packets.Packet00Login;
import net.packets.Packet01Move;
import net.packets.Packet99Disconnect;
import org.joml.Vector3f;
import org.lwjgl.system.CallbackI;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class ServerLogic extends Thread {

    private DatagramSocket socket;
//    private bin.Game game;
    private List<NetPlayer> connectedPlayers;

    public ServerLogic() {
//        this.game = game;
        this.connectedPlayers = new ArrayList<>();

        try {
            this.socket = new DatagramSocket(11337);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (true) {
            byte[] data = new byte[1024];
            DatagramPacket packet = new DatagramPacket(data, data.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.parsePacket(packet.getData(), packet.getAddress(), packet.getPort());
//            String message = new String(packet.getData());
//            System.out.println("CLIENT ["+packet.getAddress().getHostAddress()+":"+packet.getPort()+"] > " + message);
//            if (message.trim().equalsIgnoreCase("ping")){
//                sendData("pong".getBytes(), packet.getAddress(), packet.getPort());
//            }

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
                System.out.println("["+address.getHostAddress()+":"+port+"] "+ ((Packet00Login) packet).getUsername()+" has connected...");
                NetPlayer player = new NetPlayer(null, new Vector3f(50,0,0),
                        0, 0, 0, 2.5f, address, port, ((Packet00Login) packet).getUsername(),
                        ((Packet00Login) packet).getModel(), ((Packet00Login) packet).getTexture(),
                        ((Packet00Login) packet).getModelSize());
                addConnection(player, (Packet00Login) packet);
                break;
            case DISCONNECT:
                packet = new Packet99Disconnect(data);
                System.out.println("["+address.getHostAddress()+":"+port+"] "+ ((Packet99Disconnect) packet).getUsername()+" has left...");
                removeConnection((Packet99Disconnect) packet, address, port);
                break;
            case MOVE:
                packet = new Packet01Move(data);
//                System.out.println(""+((Packet01Move)packet).getUsername()+" has moved to "+((Packet01Move)packet).getMoveCoords());
                handleMove((Packet01Move)packet, address, port);
            //case PING:
              //  packet = new Packet02Ping(data);
                //System.out.println("["+address.getHostAddress()+":"+port+"] "+ ((Packet02Ping) packet).getUsername()+" Ping.")
        }
    }

    private void handleMove(Packet01Move packet, InetAddress address, int port) {
        NetPlayer player = getNetPlayer(address, port);
        if (player != null) {
            player.setPosition(packet.getMoveCoords());
            player.setRotX(packet.getRotX());
            player.setRotY(packet.getRotY());
            player.setRotZ(packet.getRotZ());
            packet.writeData(this);
        }
    }


    private void addConnection(NetPlayer player, Packet00Login packet) {
        boolean alreadyConnected = false;
        for (NetPlayer connectedPlayer : connectedPlayers) {
            if (player.equals(connectedPlayer)) { //Checks IP and Port
                alreadyConnected = true;
            } else {
                //sendData(packet.getData(), connectedPlayer.getIpAddress(), connectedPlayer.getPort());
                packet = new Packet00Login(packet.getUsername(), packet.getModel(), packet.getTexture(), packet.getModelSize());
//                System.out.println("Trying to send package. ip: "+connectedPlayer.getIpAddress().getHostAddress()+" port: "+connectedPlayer.getPort());
                sendData(packet.getData(), connectedPlayer.getIpAddress(), connectedPlayer.getPort());
            }
        }
        if (!alreadyConnected) {
            for (NetPlayer connectedPlayer : connectedPlayers) {
                packet = new Packet00Login(connectedPlayer.getUsername(), connectedPlayer.getModelStr(),
                        connectedPlayer.getTextureStr(), connectedPlayer.getModelSize());
                sendData(packet.getData(), player.getIpAddress(), player.getPort());
            }
            this.connectedPlayers.add(player);
        }
    }

    private void removeConnection(Packet99Disconnect packet, InetAddress address, int port) {
        NetPlayer player = getNetPlayer(address, port);
        connectedPlayers.remove(player);
        packet.writeData(this); //sends packet to all remaining connected players

    }

    //Some player getters with various input. DirectionalUsername may not be reliable!
    public NetPlayer getNetPlayer(DatagramPacket datagramPacket) {
        for (NetPlayer connectedPlayer : connectedPlayers) {
            if (connectedPlayer.ownsDatagram(datagramPacket))
                return connectedPlayer;
        }
        return null;
    }

    public NetPlayer getNetPlayer(InetAddress ipAddress, int port) {
        for (NetPlayer connectedPlayer : connectedPlayers) {
            if (connectedPlayer.getIpAddress().equals(ipAddress) && connectedPlayer.getPort() == port) {
                return connectedPlayer;
            }
        }
        return null;
    }

//    public NetPlayer getNetPlayer(String username) {
//        for (NetPlayer connectedPlayer : connectedPlayers) {
//            if (connectedPlayer.getUsername().equals(username))
//                return connectedPlayer;
//        }
//        return null;
//    }

    public int getNetPlayerIndex(NetPlayer player) {
        int index = 0;
        for (NetPlayer connectedPlayer : connectedPlayers) {
            if (connectedPlayer.getUsername().equals(player)) //checks ip and port
                break;
            index++;
        }
        return index;
    }

    public void sendData(byte[] data, InetAddress ipAddress, int port) {
        DatagramPacket packet = new DatagramPacket(data, data.length, ipAddress, port);
        try {
            this.socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendDataToAllClients(byte[] data) {
        for (NetPlayer connectedPlayer : connectedPlayers) {
            sendData(data, connectedPlayer.getIpAddress(), connectedPlayer.getPort());
        }
    }
}
