package net.packets.lobby;

import net.packets.Packet;

public class PacketCurLobbyInfo extends Packet {

    private String info;
    private String[] in;

   public PacketCurLobbyInfo(int clientId, String data){
       //server builds
       super(PacketTypes.CUR_LOBBY_INFO);
       setClientId(clientId);
       setData(data);
       info = getData();
   }

    public PacketCurLobbyInfo(String data){
        //client builds
        super(PacketTypes.CUR_LOBBY_INFO);
        setData(data);
        info = getData();
        in = data.split("║");
        validate();
    }

    @Override
    public void validate() {
       if(info != null) {
            for (String s : in) {
                isExtendedAscii(s);
            }
        }else{
            addError("No Status found.");
        }
    }

    @Override
    public void processData() {
       if(hasErrors()){//Errors ClientSide
           String s = createErrorMessage();
           System.out.println(s);
       }else if(in[0].equals("OK")) { //No Errors ServerSide
            System.out.println("-------------------------------------");
            System.out.println("Players in Lobby:");
            for (int i = 1; i < in.length; i++) {
                System.out.println(in[i]);
            }
            System.out.println("-------------------------------------");
        }else{  //Errors ServerSide
            System.out.println(in[0]);
        }
    }
}
