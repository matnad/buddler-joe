package net.packets;



public class PacketGameEnd extends Packet{


    public PacketGameEnd(int clientId) {
        // server builds
        super(PacketTypes.GAME_OVER);
        setClientId(clientId);
        validate();
    }

    public PacketGameEnd() {
        // client builds
        super(PacketTypes.GAME_OVER);
        validate();
    }

    @Override
    public void validate() {
        // No data to validate since it is a Empty Packet
    }

    @Override
    public void processData() {

    }
}
