package net.LobbyHandling;

import net.LobbyHandling.Lobby;

import java.util.HashMap;

public class ServerLobbyList {
    private HashMap<Integer, Lobby> lobbies;


    /**
     * Method to add a lobby tho the HashMap of all players. of this lobby
     * @param lobby The lobby to be added to the HashMap
     * @return statement to let for example the PackageCreatLobby instance know,
     * whether the action was successful or not
     */

    public int addLobby(Lobby lobby){
        if(lobbies.containsKey(lobby.getLobbyId())){
            return -1;
        }
        for(Lobby l : lobbies.values()){
            if(lobby.getLobbyName().equals(l.getLobbyName())) {
                return -2;
            }
        }
        lobbies.put(lobby.getLobbyId(), lobby);
        return 1;

    }


    /**
     * Method to remove a lobby by the lobbyId
     * @param lobbyId the lobbyId of the lobby to be removed
     * @return true or false depending on whether the lobby was in the list or not
     */
    public int removeLobby(int lobbyId){
        if(lobbies.containsKey(lobbyId)){
            lobbies.remove(lobbyId);
            return 1;
        } else{
            return -1;
        }
    }

    /**
     * Method to search for a lobbies name in the Hashmap by using the lobbyId
     * @param lobbyId the looked for lobbyId
     * @return either the correct name or null
     */

    public String searchName(int lobbyId){
        return lobbies.get(lobbyId).getLobbyName();
    }

    /**
     * search a lobby via the lobbyName to find the lobbyId in case only one of the two was supplied
     * to the method.
     * @param lobbyName of the lobby to be found in the list
     * @return either the lobbyId or -1 if not found
     */

    public int searchLobbyId(String lobbyName){
        for(Lobby l : lobbies.values()){
            if(lobbyName.equals(l.getLobbyName())) {
                return l.getLobbyId();
            }
        }
        return -1;
    }

    /**
     * Method to get lobbyId, lobbyName and
     * @return the full lobby List  as a String
     */

    public String getInfo(){
        String res = "";
        for(Lobby l : lobbies.values()){
            res = l.getLobbyId() + ";" + l.getLobbyName() + ";" + l.getPlayerAmount() + ";";
        }
        return res;
    }

    /**
     * Method to print out the full lobby List
     * @return the full lobby List  as a String
     */
    //TODO: print out the lobbies list
    @Override
    public String toString() {
        return "LobbyPlayerList{" +
                "lobbies=" +

                '}';
    }
}
