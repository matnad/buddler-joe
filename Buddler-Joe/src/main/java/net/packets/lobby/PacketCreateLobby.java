package net.packets.lobby;

import net.lobbyhandling.Lobby;
import net.lobbyhandling.ServerLobbyList;
import net.ServerLogic;
import net.packets.Packet;

import java.util.StringJoiner;

public class PacketCreateLobby extends Packet {


    private String lobbyname;


    /**
     * A packed which is send from the client to the Server if he wants
     * to create a new lobby. Containing the information to do so.
     */
    public PacketCreateLobby(String data) {
        //client builds
        super(PacketTypes.CREATE_LOBBY);
        setData(data);
        lobbyname = getData();
        validate();
    }

    public PacketCreateLobby(int clientId, String data) {
        //server builds
        super(PacketTypes.CREATE_LOBBY);
        setClientId(clientId);
        setData(data);
        lobbyname = getData();
        validate();
    }

    @Override
    public void validate() {
        if(lobbyname == null){
            addError("No lobbyname found.");
            return;
        }
        if(lobbyname.length() > 16){
            addError("Lobbyname to long. Maximum is 16 Characters.");
        }else if(lobbyname.length() < 4){
            addError("Lobbyname to short. Minimum is 4 Characters.");
        }
        isExtendedAscii(lobbyname);
    }

    @Override
    public void processData() {
        //System.out.println("--------------" + ServerLogic.getLobbyList().toString());
        String status;
        if(hasErrors()){
            StringJoiner statusJ = new StringJoiner("\n","ERRORS:","");
            for (String error : getErrors()) {
                statusJ.add(error);
            }
            status = statusJ.toString();
        }else{
            Lobby lobby = new Lobby(lobbyname);
            status = ServerLogic.getLobbyList().addLobby(lobby);
        }
        PacketCreateLobbyStatus pcls = new PacketCreateLobbyStatus(getClientId(),status);
        pcls.sendToClient(getClientId());
        //Creat a LobbyOverview-Packet to be send to all Clients.
        if(!hasErrors() && status.equals("OK")){
            String info = ServerLogic.getLobbyList().getTopTen();
            PacketLobbyOverview p = new PacketLobbyOverview(getClientId(),info);
            p.sendToAllClients();
        }
    }
}
