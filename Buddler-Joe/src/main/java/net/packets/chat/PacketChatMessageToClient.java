package net.packets.chat;

import net.packets.Packet;

public class PacketChatMessageToClient extends Packet {

    private String chatmsg;
    private String timestamp;
    private String receiver;

//client
    public PacketChatMessageToClient(String chatmsg){
        super(PacketTypes.CHAT_MESSAGE_TO_CLIENT);
        this.chatmsg = chatmsg;
        validate();
    }


//server
    public PacketChatMessageToClient(int clientID,String data){
        super(PacketTypes.CHAT_MESSAGE_TO_CLIENT);
        setClientId(clientID);
        setData(data);
        validate();
    }


    /**
     * Check message length
     */
    public void validate() {
        if (chatmsg == null) {
            addError("No Message found");
            return;
        }
        if(chatmsg.length() > 100){
            addError("Message to long. Maximum is 100 Characters.");
        }
    }

    /**
     * Test for errors and print the message.
     */
    public void processData(){
        String status;
        if(hasErrors()){
            status = createErrorMessage();
        }else{
            System.out.println(chatmsg);
        }
    }
}
