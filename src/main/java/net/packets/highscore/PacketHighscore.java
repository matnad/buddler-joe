package net.packets.chat;

import net.packets.Packet;

/**
 * Packet that gets send from the Server to the Client, to inform the him over the result of the
 * send message attempt. Packet-Code: CHATN
 *
 * @author Moritz Würth
 */
public class PacketHighscore extends Packet {

    private String highscore;

    /**
     * Constructor that is used by the Server to build the Packet.
     */

    public PacketHighscore(int clientId, String data) {
        super(PacketTypes.HIGHSCORE);
        setClientId(clientId);
        setData(data);
        //this.data = highscore;
        validate();
    }

    /**
     *
     */
    @Override
    public void validate() {
        if (highscore != null) {
            isExtendedAscii(highscore);
        } else {
            addError("No Highscore found.");
        }
    }

    /**
     *
     */
    @Override
    public void processData() {
        //TODO: Display Highscore
    }
}
