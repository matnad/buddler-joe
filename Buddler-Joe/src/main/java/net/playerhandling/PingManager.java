package net.playerhandling;

import net.packets.pingpong.PacketPing;

import java.util.ArrayList;

import static java.lang.Thread.sleep;

public class PingManager implements Runnable{

    /**
     * every client and server create an instance of this class for themselves
     * listOfPingTS saves all timestamps(sendingtime)
     * in the variable ping is the average ping time
     * data is timestamp in String
     */
    //TS=timestamps number in string
    private ArrayList<String> listOfPingTS;
    //Arraylist mit time
    private long ping;
    Thread thread;
    private int clientId;

    /**
     * sending ping from server to client
     */
    public PingManager(int clientId) {
        listOfPingTS = new ArrayList<>();
        ping = 0;
        this.clientId = clientId;
    }

    /**
     * sending ping from client to server
     */
    public PingManager() {
        listOfPingTS = new ArrayList<>();
        ping = 0;
    }

    public void run() {
        while(true) {
            try {
                sleep(3000);
            }
            catch(InterruptedException e) {
            }
            long currTime;
            if(clientId > 0) {//from server to client
                currTime = System.currentTimeMillis();
                String data = String.valueOf(currTime);
                append(data);
                PacketPing packetPing = new PacketPing(clientId, data);
                packetPing.sendToClient(clientId);
            }else{ //from client to server
                currTime = System.currentTimeMillis();
                String data = String.valueOf(currTime);
                append(data);
                PacketPing packetPing = new PacketPing(data);
                packetPing.sendToServer();
            }
        }
    }

    private void append(String timestamp) {
        listOfPingTS.add(timestamp);
    }

    public void delete(String timestamp) {
        listOfPingTS.remove(timestamp);
    }

    public void updatePing(String diffTime) {
        ping = (ping*9 + Long.parseLong(diffTime))/10;
    }

    public ArrayList getListOfPingTS() {
        return listOfPingTS;
    }

    public long getPing() {
        return ping;
    }

}
