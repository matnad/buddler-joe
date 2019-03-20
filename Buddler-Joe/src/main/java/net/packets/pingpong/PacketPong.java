package net.packets.pingpong;

import net.packets.Packet;

import java.util.HashMap;

public class PacketPong extends Packet {

    //String is the reference number, Integer should be the clientid
    private static HashMap<String, Integer> ping_reference = new HashMap<>();
    //Arraylist mit time
    private static int ping = 0;
    /**
     * Constructor client gets pong(answer) from server
     * @param data defines what ping this pong refers to
     */
    public PacketPong(String data) {
        super(Packet.PacketTypes.PONG);
        setData(data);
        validate();
    }

    /**
     * Constructor server gets pong(answer) from client
     * @param data defines what ping this pong belongs to
     */
    public PacketPong(int clientId, String data) {
        super(Packet.PacketTypes.PONG);
        setClientId(clientId);
        setData(data);
        validate();
    }
    public static HashMap getListOfPingReference() { return ping_reference; }

    //data already checked when receiving the ping packet.
    @Override
    public void validate() {
       if(getData() == null) {
           addError("Empty packet");
       }
    }

    /**
     * We check if the pong number(String) is
     * equal to one of those numbers(Strings) we
     * saved in the HashMap after receiving the ping packet.
     * We check if the pong belongs to a ping
     */
    @Override
    public void processData() {
        //if(hasErrors() || !getListOfPingReference().containsKey(getData())) {
            //Invalid
        //}else{
            //Here would be the time calculation
            System.out.println("PONG " + getData());
            getListOfPingReference().remove(getData());
        //}
    }
}
