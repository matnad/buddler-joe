package net.packets.pingpong;

import net.packets.Packet;

/**
 * A ping packet can be created by instantiating this PacketPing class.
 * Ping packets are created by the <code>PingManager</code> class.
 * This class inherits from the superclass <code>Packet</code>.
 *
 * @see net.playerhandling.PingManager
 * @see Packet
 */

public class PacketPing extends Packet {

    /**
     * Creates a <code>PacketPing</code> object by server side and validates its <code>data</code>.
     *
     * @param clientId identity of the client.
     * @param data is the creation time
     */

    public PacketPing(int clientId, String data) {
        super(Packet.PacketTypes.PING);
        setClientId(clientId);
        setData(data);
        //System.out.println("PING " + getData());
        validate();
    }

    /**
     * Creates a <code>PacketPing</code> object by client side and validates its <code>data</code>.
     *
     * @param data is the creation time
     */

    public PacketPing(String data) {
        super(Packet.PacketTypes.PING);
        setData(data);
        //System.out.println("PING " + getData());
        validate();
    }

    /**
     * Checks if the chars of <code>data</code> represent only digits.
     * If one of the chars is a non digit, the error will be saved.
     */

    @Override
    public void validate() {
        if(getData() == null) {
            addError("Empty message");
        }else{
            for(int i = 0; i < getData().length(); i++) {
                if(!Character.isDigit(getData().charAt(i))) {
                    addError("Invalid ping number");
                }
            }
        }
    }

    /**
     * Creates a response pong packet by creating an object of the class <code>PacketPong</code> to give the client/server an answer. <code>PacketPong</code> object contains the creation time.
     * <p>
     * If the clientId was passed, the pong would be sent to the client. Otherwise, the clientId would have the default value 0 and the pong would be sent to the server.
     * The <code>clientId</code> is declared in the super class <code>Packet</code>
     * If there is an error in <code>data</code>, nothing will happen to the packet.
     * @see PacketPong
     * @see Packet
     */

    @Override
    public void processData() {
        if(!hasErrors()) {
            PacketPong pong = new PacketPong(getClientId(), getData());
            if(getClientId() > 0) {
                pong.sendToClient(getClientId());
            }else{
                pong.sendToServer();
            }
        }
    }
}
