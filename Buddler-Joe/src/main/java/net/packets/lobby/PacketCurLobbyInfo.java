package net.packets.lobby;

import net.packets.Packet;

public class PacketCurLobbyInfo extends Packet {

    private String status;
    private String[] in;

   public PacketCurLobbyInfo(int clientId, String data){
       //server builds
       super(PacketTypes.CUR_LOBBY_INFO);
       setClientId(clientId);
       setData(data);
   }

    public PacketCurLobbyInfo(String data){
        //client builds
        super(PacketTypes.CUR_LOBBY_INFO);
        setData(data);
        status = getData();
        in = data.split("║");
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
        if(in[0].equals("OK")) {
            System.out.println("-------------------------------------");
            System.out.println("Players in Lobby:");
            for (int i = 1; i < in.length; i++) {
                System.out.println(in[i]);
            }
            System.out.println("-------------------------------------");
        }else{
            System.out.println(in[0]);
        }
    }
}
