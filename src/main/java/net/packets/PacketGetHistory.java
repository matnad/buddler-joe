package net.packets;

import game.History;
import net.ServerLogic;
import net.packets.lobby.PacketLobbyOverview;

public class PacketGetHistory extends Packet {

    public PacketGetHistory(int clientId) {
        // server builds
        super(PacketTypes.GET_HISTORY);
        setClientId(clientId);
    }

    /**
     * Constructor that will be used by the Client to build the Packet. Which can then be send to the
     * Server. There are no parameters necessary here since the Packet has no real content(only a
     * Type, LOBGE).
     */
    public PacketGetHistory() {
        // client builds
        super(PacketTypes.GET_HISTORY);
    }







    @Override
    public void validate() {
        //Nothing to validate.
    }

    @Override
    public void processData() {
        String info;
        if (!isLoggedIn()) {
            addError("Not loggedin yet");
        }
        if (hasErrors()) {
            info = createErrorMessage();
        } else {
            info = "OK║" + History.getStory();
        }
        PacketHistory p = new PacketHistory(getClientId(), info);
        p.sendToClient(getClientId());
    }
}
