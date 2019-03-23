package net.packets.chat;

import net.packets.Packet;

public class PacketChatMessageStatus extends Packet{

    private String status;
    private String input[];


    /**
     * Package to respond to the client that the  chat message send successful
     */
    //client
    public PacketChatMessageStatus(String data){
        super(PacketTypes.CHAT_MESSAGE_STATUS);
        setData(data);
        status = getData();
        //input = status.split(" ");
        validate();
    }
    //server
    public PacketChatMessageStatus(int clientID,String data){
        super(PacketTypes.CHAT_MESSAGE_STATUS);
        setClientId(clientID);
        setData(data);
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
        if(status.startsWith("OK")){
//            System.out.println("Successfully send a Message");
        }else{
//            for (String s : input) {
//                System.out.println(s);
//            }
            System.out.println(status);
        }
    }
}
