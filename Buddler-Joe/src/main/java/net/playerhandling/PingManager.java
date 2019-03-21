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
    private static ArrayList<String> listOfPingTS;
    //Arraylist mit time
    private static int ping;
    Thread thread;
    private static int clientId;

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
            //hier anweisung
            long currTime;
            if(clientId > 0) {//from server to client
                currTime = System.currentTimeMillis();
                String data = String.valueOf(currTime);
                append(data);
                PacketPing packetPing = new PacketPing(clientId, data);
                packetPing.processData();
            }else{
                currTime = System.currentTimeMillis();
                String data = String.valueOf(currTime);
                append(data);
                PacketPing packetPing = new PacketPing(data);
                packetPing.processData();
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

    }

    public int getPing() {
        return ping;
    }

}
