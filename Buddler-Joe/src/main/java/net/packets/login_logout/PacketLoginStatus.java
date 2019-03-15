package net.packets.login_logout;

import net.packets.Packet;

public class PacketLoginStatus extends Packet {

    private String status;
    /**
     * Package to respond to the client that the Login has been successful
     */

    public PacketLoginStatus(String data) {
        //Client receives packed
        super(PacketTypes.LOGIN_STATUS);
        setData(data);
        this.status = data;
        validate();
    }

    public PacketLoginStatus(int clientId, String status){
        super(PacketTypes.LOGIN_STATUS);
        setData(status);
        setClientId(clientId);
        this.status = status;
        validate();
    }

    @Override
    public void validate() {
        if(status != null) {
            isExtendedAscii(status);
        }else{
            addError("No Status found.");
        }
    }

    @Override
    public void processData() {
        if (status.startsWith("OK")) {
            System.out.println("Login Successful");
        } else {
            if (hasErrors()) {
                System.out.println(status + "\n" + createErrorMessage());
            } else {
                System.out.println(status);
            }
        }
    }
}
