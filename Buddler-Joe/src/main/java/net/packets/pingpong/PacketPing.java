package net.packets.pingpong;

import net.packets.Packet;

public class PacketPing extends Packet {

    /**
     * Constructor when server gets ping from client
     * @param clientId identity of client who sent ping
     * @param data specifies the ping to create a matching pong and to
     * let the client know that the pong he receives refers to the ping he sent.
     */
    public PacketPing(int clientId, String data) {
        super(Packet.PacketTypes.PING);
        setClientId(clientId);
        setData(data);
        System.out.println("PING " + getData());
        validate();
    }

    /**
     * Constructor when client gets ping from server
     */
    public PacketPing(String data) {
        super(Packet.PacketTypes.PING);
        setData(data);
        System.out.println("PING " + getData());
        validate();
    }

    /**
     * validate() checks if the String contains the reference number.
     * We check if all chars of data are digits.
     */
    @Override
    public void validate() {
        if(getData() == null) {
            addError("Empty message");
        }else{
            for(int i = 0; i < getData().length(); i++) {
                if(!Character.isDigit(getData().charAt(i))) {
                    //Invalid
                    addError("Invalid ping number");
                }
            }
        }
    }

    /**
     * This method creates a response packet "pong" to give
     * the client/server an answer.
     * Here, we add the identifying ping number from the
     * validated packet to the Hashmap.
     * If the numeric value of data is over 10'000,
     * the ping was sent by the client.
     * If the numeric value of data is under 10'000,
     * the ping was sent by the server.
     */
    @Override
    public void processData() {
        if(hasErrors()) {
            //discard packet and no need to response
        }else{
            if(getListOfPingReference().containsKey(getData())) {
                //Invalid
                //HashMap doesnt allow duplicate Keys
            }
            getListOfPingReference().put(getData(), getClientId());
            PacketPong pong = new PacketPong(getData());
            if(Integer.parseInt(getData()) >= 10000) {
                pong.sendToClient(getClientId());
            } else if(Integer.parseInt(getData()) < 10000) {
                pong.sendToServer();
            }
        }
    }
}
