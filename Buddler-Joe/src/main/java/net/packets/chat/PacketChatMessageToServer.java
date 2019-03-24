package net.packets.chat;

import net.ServerLogic;
import net.packets.Packet;
import java.util.Date;
import net.playerhandling.Player;
import java.text.SimpleDateFormat;

public class PacketChatMessageToServer extends Packet {

    private String chatmsg;
    private String timestamp;
    private String receiver;

    /**
     * Packet to send a chat message to other players in the same lobby
     * @param chatmsg the message from the client
     */
//client
    public PacketChatMessageToServer(String chatmsg) {
        super(PacketTypes.CHAT_MESSAGE_TO_SERVER);
        this.chatmsg = chatmsg;
        SimpleDateFormat simpleFormat = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        timestamp = simpleFormat.format(date);
        receiver = "0";
        setData(chatmsg + "║" + timestamp + "║" + receiver);
        validate();
    }


//server
    public PacketChatMessageToServer(int clientId, String data) {
        super(PacketTypes.CHAT_MESSAGE_TO_SERVER);
        setClientId(clientId);
        if(data == null) {
            data = ""; //To prevent nullpointer when splitting
        }
        String[] input = data.split("║");
        if(input.length != 3){
            addError("Invalid Input");
            return;
        }
        chatmsg = input[0];
        timestamp = input[1];
        receiver = input[2];
        setData(data);
        validate();
    }


    @Override
    public void validate() {
        if (chatmsg == null) {
            addError("No Message found");
            return;
        }
        if(chatmsg.length() > 100){
            addError("Message to long. Maximum is 100 Characters.");
        }
    }

    @Override
    public void processData(){
        String status = null;
        if(!hasErrors()){
            Player client = ServerLogic.getPlayerList().getPlayer(getClientId());
            if(client == null){
                addError("Not logged in");
            }else{
                int lobbyID = client.getCurLobbyId();
                if(lobbyID == 0){
                    addError("Must been in a Lobby to use the chat.");
                }else{
                    String fullmessage = "["+ client.getUsername() + "-" + timestamp + "]  " + chatmsg;
                    PacketChatMessageToClient sendMessage = new PacketChatMessageToClient(getClientId(),fullmessage);
                    sendMessage.sendToLobby(client.getCurLobbyId());
                }
            }
        }
        if(hasErrors()){
            status = createErrorMessage();
        }else{
            status = "OK";
        }
        PacketChatMessageStatus sendMessage = new PacketChatMessageStatus(getClientId(),status);
        sendMessage.sendToClient(getClientId());
    }
}








