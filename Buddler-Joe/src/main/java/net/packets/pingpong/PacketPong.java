package net.packets.pingpong;

import net.ClientLogic;
import net.ServerLogic;
import net.packets.Packet;
import net.playerhandling.PingManager;

/**
 * A pong packet can be created by instantiating this PacketPong class.
 * Pong packets are created by the <code>PingManager</code> class.
 * This class inherits from the superclass <code>Packet</code>.
 * A <code>PacketPong</code> object contains the creation time of its respective <code>PacketPing</code> object.
 *
 * @see net.playerhandling.PingManager
 * @see Packet
 */

public class PacketPong extends Packet {

    /**
     * Creates a <code>PacketPong</code> object by client side and validates its <code>data</code>.
     *
     * @param data is the creation time of the respective ping packet.
     */

    public PacketPong(String data) {
        super(Packet.PacketTypes.PONG);
        setData(data);
        validate();
    }

    /**
     * Creates a <code>PacketPong</code> object by server side and validates its <code>data</code>.
     *
     * @param clientId identity of the client.
     * @param data is the creation time of the respective ping packet.
     */

    public PacketPong(int clientId, String data) {
        super(Packet.PacketTypes.PONG);
        setClientId(clientId);
        setData(data);
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
     * Does the time calculation. It calculates the difference between the timestamp when the ping was sent and the timestamp when the pong arrived to the respective sender.
     *
     * If the clientId was not passed, the average ping of the respective client will be udated. Otherwise, the average ping of the respective clientthread will be updated.
     * If there is an error in <code>data</code>, nothing will happen with the packet.
     *
     * @throws NumberFormatException if <code>data</code> contains non digit characters.
     */

    @Override
    public void processData() {
        if(!hasErrors()) {
            long timeAtSending;
            try {
                timeAtSending = Long.parseLong(getData());
            }catch(NumberFormatException e){
                return; //stops the method
            }
            long currTime = System.currentTimeMillis();
            long diffTime = currTime - timeAtSending;
            if (getClientId() == 0) {
                PingManager pingManager = ClientLogic.getPingManager();
                pingManager.delete(getData());
                pingManager.updatePing(diffTime);
                //System.out.println("PING " + diffTime);
            } else { //when server gets answer/pong
                PingManager pingManager = ServerLogic.getThreadByClientId(getClientId()).getPingManager();
                pingManager.delete(getData());
                pingManager.updatePing(diffTime);
                //System.out.println("PING " + diffTime);
            }

        }

    }
}
