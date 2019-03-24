package net.packets.lobby;

import net.ServerLogic;
import net.packets.Packet;

/**
 * A packed that is send from the server to the client, which contains a List of at max 10 Lobbies
 * that are currently available on the server and not full.
 * Packet-Code: LOBOV
 * @author Sebastian Schlachter
 */
public class PacketLobbyOverview extends Packet {

    String[] in;


    /**
     * Constructor that is used by the Client to build the Packet, after receiving the Command LOBOV.
     * @param data a single String that begins with "OK║" and contains a List of max 10 Lobbies
     *             (and information to them). Each list entry is separated by "║". In the case that
     *             an error occurred before, the String is an errormessage and does not begin with "OK║".
     * The variable {@param data} gets split at the positions of "║". Every substring gets then saved
     *             in to the Array called {@code in}.
     */
    public PacketLobbyOverview(String data) {
        //Client receives
        super(PacketTypes.LOBBY_OVERVIEW);
        //System.out.print(data);
        setData(data);
        in = getData().split("║");
        validate();
    }

    /**
     * Constructor that is used by the Server to build the Packet.
     * @param clientId ClientId of the the receiver.
     * @param data A single String that begins with "OK║" and contains a List of max 10 Lobbies
     *             (and information to them). Each list entry is separated by "║". In the case that an
     *             error occurred before the String is an errormessage and does not begin with "OK║".
     * The variable {@param data} gets split at the positions of "║". Every substring gets then saved
     *             in to the Array called {@code in}.
     */
    public PacketLobbyOverview(int clientId, String data) {
        //server builds
        super(PacketTypes.LOBBY_OVERVIEW);
        setClientId(clientId);
        setData(data);
        in = getData().split("║");
        validate();
    }


    /**
     * Validation method to check the data that has, or will be send in this packet.
     * Checks if {@code data} is not null.
     * Checks for every element of the Array {@code in}, that it consists of extendet ASCII Characters.
     * In the case of an error it gets added with {@link Packet#addError(String)}.
     */
    @Override
    public void validate() {
        if(getData() != null){
            for (String s : in) {
                isExtendedAscii(s);
            }
        }else{
            addError("No data has been found");
        }
    }

    /**
     * Method that lets the Client react to the receiving of this packet.
     * Check for errors in validate.
     * If {@code in[0]} equals "OK" the list of lobbies gets printed.
     * Else in the case of an error only the error message gets printed.
     */
    @Override
    public void processData() {
        if(hasErrors()){
            System.out.println(createErrorMessage());
        }else if(in[0].equals("OK")) { //the "OK" gets added in PacketCreatLobby.processData and PacketGetLobbies.processData
            System.out.println("-------------------------------------");
            System.out.println("Available Lobbies:");
            for (int i = 1; i < in.length; i++) {
                System.out.println(in[i]);
            }
            System.out.println("-------------------------------------");
        }else{
            System.out.println(in[0]);
        }
    }
}
