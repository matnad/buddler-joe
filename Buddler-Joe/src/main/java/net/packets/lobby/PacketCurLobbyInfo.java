package net.packets.lobby;

import net.packets.Packet;

public class PacketCurLobbyInfo extends Packet {

    private String info;
    private String[] infoArray;

   public PacketCurLobbyInfo(int clientId, String data){
       //server builds
       super(PacketTypes.CUR_LOBBY_INFO);
       setClientId(clientId);
       setData(data);
       info = getData();
       infoArray = new String[0];   //necessary since infoArray is not really used on the Server side,
                                    // but needed in validate
       validate();
   }

    public PacketCurLobbyInfo(String data){
        //client builds
        super(PacketTypes.CUR_LOBBY_INFO);
        setData(data);
        info = getData();
        infoArray = data.split("║");
        validate();
    }

    @Override
    public void validate() {
       if(info != null) {
           for (String s : infoArray) {
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
       }else if(infoArray[0].equals("OK")) { //No Errors ServerSide
            System.out.println("-------------------------------------");
            System.out.println("Players in Lobby:");
            for (int i = 1; i < infoArray.length; i++) {
                System.out.println(infoArray[i]);
            }
            System.out.println("-------------------------------------");
        }else{  //Errors ServerSide
            System.out.println(infoArray[0]);
        }
    }
}
