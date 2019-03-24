package net.packets.login_logout;

import net.packets.Packet;
import net.packets.lobby.PacketGetLobbies;

public class PacketLoginStatus extends Packet {

    private String status;

    /**
     * Constructor when the client receives a PacketLoginStatus packet from the server
     * @param data Should contain the status message from the server concerning the Login status.
     */

    public PacketLoginStatus(String data) {
        super(PacketTypes.LOGIN_STATUS);
        setData(data);
        this.status = data;
        validate();
    }

    /**
     * Constructor when the Server creates a PacketLoginStatus packet to be sent to the client.
     * @param clientId The client to which this particular Login Status belongs to
     * @param status The status from the server which gets created in the PacketLogin
     */

    public PacketLoginStatus(int clientId, String status){
        super(PacketTypes.LOGIN_STATUS);
        setData(status);
        setClientId(clientId);
        this.status = status;
        validate();
    }

    /**
     * Implementation of the abstract validate method to validate the input data/status
     * Validate method calls the isExtendedAscii method which checks whether a String is extended Ascii or not.
     * If the status is null or not extended Ascii, an error message gets added to the error message List.
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
     * Implementation of the abstract processData method to process the data on the client side received from the server
     * Checks whether the Status is either OK, meaning that the login was successful with the name chosen,
     * CHANGE, which means that the login was successful but the name had to be changed to another version of it or
     * That the Login was not successful due to errors in the status or errors detected due to a faulty package.
     */

    @Override
    public void processData() {
        if (status.startsWith("OK") && !hasErrors() && status.length()>2) {
            System.out.println("Login Successful, your username is: " + getData().substring(2));
            PacketGetLobbies p = new PacketGetLobbies();
            p.sendToServer();
        } else if(status.startsWith("CHANGE") && !hasErrors() && status.length()>6){
            System.out.println("Login Successful, however your username has already been taken. " +
                    "We assigned you this username: " + getData().substring(6));
            PacketGetLobbies p = new PacketGetLobbies();
            p.sendToServer();
        } else {
            if (hasErrors()) {
                System.out.println(status + "\n" + createErrorMessage());
            } else {
                System.out.println(status);
            }
        }
        System.out.println("Type \"help\" at any point to see a list of commands to interact with the server.");
    }
}