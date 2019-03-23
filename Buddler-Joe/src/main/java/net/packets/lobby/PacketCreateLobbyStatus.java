package net.packets.lobby;

import net.playerhandling.ServerPlayerList;
import net.ServerLogic;
import net.packets.Packet;

/**
 * Packet that gets send from the Server to the Client, to inform the him over the result of the lobby-creation attempt.
 * Packet-Code: LOBCS
 */
public class PacketCreateLobbyStatus extends Packet{
    private String status;


    /**
     * Constructor that is used by the Server to build the Packet.
     * @param clientId ClientId of the receiver.
     * @param data A String that contains Information about the lobby-creation attempt.
     *             ("OK" or in the case of an error, a suitable errormessage)
     * {@link PacketCreateLobbyStatus#status} gets set to equal data.
     */
    public PacketCreateLobbyStatus(int clientId, String data) {
        //Server builds
        super(Packet.PacketTypes.CREATE_LOBBY_STATUS);
        setData(data);
        setClientId(clientId);
        status = getData();
        validate();
    }

    /**
     * Constructor that is used by the Client to build this packet, if he receives "LOBCS".
     * @param data A String that contains Information about the lobby-creation attempt.
     *             ("OK" or in the case of an error, a suitable errormessage)
     * {@link PacketCreateLobbyStatus#status} gets set to equal data.
     */
    public PacketCreateLobbyStatus(String data) {
        //client builds
        super(Packet.PacketTypes.CREATE_LOBBY_STATUS);
        setData(data);
        status = getData();
        validate();
    }

    /**
     * Validation method to check the data that has, or will be send in this packet.
     * Checks if data is not null.
     * Checks that {@link PacketCreateLobbyStatus#status} consists of extendet ASCII Characters.
     * In the case of an error it gets added with {@link Packet#addError(String)}.
     */
    @Override
    public void validate() {
        if(status != null) {
            isExtendedAscii(status);
        }else{
            addError("No Status found.");
        }
    }

    /**
     * Method that lets the Client react to the receiving of this packet.
     * Check for errors in validate.(prints errormessages if there are some)
     * If status starts eith "OK", the message "Lobby-Creation Successful" gets printed.
     * Else in the case of an error on the serverside the error message gets printed.
     */
    @Override
    public void processData() {
        if(hasErrors()){
            System.out.println(createErrorMessage());
        }else if(status.startsWith("OK")){
            System.out.println("Lobby-Creation Successful");
        }else{
            System.out.println(status);
        }
    }
}
