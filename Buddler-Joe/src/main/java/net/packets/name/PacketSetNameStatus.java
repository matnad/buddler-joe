package net.packets.name;

import net.packets.Packet;

public class PacketSetNameStatus extends Packet{
    private String status;
    /**
     * Package to respond to the client that the Login has been successful
     */

    public PacketSetNameStatus(String data) {
        super(PacketTypes.SET_NAME_STATUS);
        setData(data);
        this.status = data;
        validate();
    }

    public PacketSetNameStatus(int clientId, String status){
        super(PacketTypes.SET_NAME_STATUS);
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
        if (status.startsWith("Successfully")) {
            System.out.println(status);
        } else {
            if (hasErrors()) {
                System.out.println(createErrorMessage());
            } else {
                if(status.contains("Username already taken")){
                    //TODO: bob_001
                    //ClientLogic.recommendName(status.substring());
                }
                System.out.println(status);
            }
        }
    }
}
