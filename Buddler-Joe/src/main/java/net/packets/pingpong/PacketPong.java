package net.packets.pingpong;

import net.packets.Packet;

public class PacketPong extends Packet {

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
     * We check if the pong number(String) is
     * equal to one of those numbers(Strings) we
     * saved in the HashMap after receiving the ping packet.
     * We check if the pong belongs to a ping
     */
    @Override
    public void processData() {
        if(!hasErrors()) {
            if(getClientId() != 0) {
                this.sendToClient(getClientId());
            }else{
                //Here would be the time calculation
                long timeAtSending = Long.parseLong(getData());
                long currTime = System.currentTimeMillis();
                long diffTime = currTime - timeAtSending;
            }

        }
        //AUS DER ARRAYLIST LÖSCHEN NICHT VERGESSEN

    }
}
