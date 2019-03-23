package net.playerhandling;
import net.packets.pingpong.PacketPing;
import java.util.ArrayList;
import static java.lang.Thread.sleep;

/**
 * The function of the PingManager is to send automatically pings to client and server and to calculate the respective average ping. This class is activated by the <code>ClientThread</code> and the <code>ClientLogic</code> class.
 *
 * @see ClientThread
 * @see net.ClientLogic
 */

public class PingManager implements Runnable{

    /**
     * @param listOfPingTS this list contains the creation times of all sent pings.
     * @param ping the average ping
     * @param clientId the identity of the client
     */
    private ArrayList<String> listOfPingTS;
    private long ping;
    private int clientId;

    /**
     * Creates a <code>PingManager</code> object when sending ping from server to client. The client is determined by the <code>clientId</code>.
     */
    public PingManager(int clientId) {
        listOfPingTS = new ArrayList<>();
        ping = 0;
        this.clientId = clientId;
    }

    /**
     *Creates a <code>PingManager</code> object when sending ping from client to server.
     */
    public PingManager() {
        listOfPingTS = new ArrayList<>();
        ping = 0;
    }

    /**
     * Executes every five seconds the automized sending of the pings and saves the creation times in <code>listOfPingTS</code>.
     * The destination is determined by the <code>clientId</code>.
     * If the clientId was passed, the ping would be sent to the client. Otherwise, the clientId will have the default value 0 and the ping will be sent to the server.
     * An ping object will be created by instantiating the <code>PacketPing</code> class.
     *
     * @throws InterruptedException when thread is interrupted.
     * @see PacketPing
     */
    public void run() {
        while(true) {
            try {
                sleep(5000);
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

    /**
     * Appends the creation time of the <code>PacketPing</code> object to <code>listOfPingTS</code>.
     *
     * @param timestamp creation time of the ping
     */
    private void append(String timestamp) {
        listOfPingTS.add(timestamp);
    }

    /**
     * Deletes the creation time of the <code>PacketPing</code> object in <code>listOfPingTS</code> if the respective <code>PacketPong</code> object returned successfully.
     *
     * @param timestamp creation time of the ping
     */
    public void delete(String timestamp) {
        listOfPingTS.remove(timestamp);
    }

    /**
     * Updates the average <code>ping</code>.
     *
     * @param diffTime the difference between the arrival time of the <code>PacketPong</code> object and the creation time of the <code>PacketPing</code> object
     */
    public void updatePing(long diffTime) {
        ping = (ping*9 + diffTime)/10;
    }

    /*
    public ArrayList getListOfPingTS() {
        return listOfPingTS;
    }
    */

    /**
     * Informs the every player about their ping.
     *
     * @return average ping time.
     */
    public long getPing() {
        return ping;
    }

}
