package net.lobbyhandling;

import java.util.HashMap;

public class ServerLobbyList {
    private HashMap<Integer, Lobby> lobbies;


    public ServerLobbyList(){
        lobbies = new HashMap<>();
    }

    /**
     * Method to add a lobby tho the HashMap of all players. of this lobby
     * @param lobby The lobby to be added to the HashMap
     * @return statement to let for example the PackageCreatLobby instance know,
     * whether the action was successful or not
     */

    public String addLobby(Lobby lobby){
        if(lobbies.containsKey(lobby.getLobbyId())){
            return "Lobby already created.";
        }
        for(Lobby l : lobbies.values()){
            if(lobby.getLobbyName().equals(l.getLobbyName())) {
                return "Lobbyname already taken.";
            }
        }
        lobbies.put(lobby.getLobbyId(), lobby);
        return "OK";
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

    public String getName(int lobbyId){
        return lobbies.get(lobbyId).getLobbyName();
    }

    /**
     * Method to search for a lobby by its lobbyId
     * @param lobbyId the looked for lobbyId
     * @return either the lobby or null
     */

    public Lobby getLobby(int lobbyId){
        return lobbies.get(lobbyId);
    }

    /**
     * search a lobby via the lobbyName to find the lobbyId in case only one of the two was supplied
     * to the method.
     * @param lobbyName of the lobby to be found in the list
     * @return either the lobbyId or -1 if not found
     */

    public int getLobbyId(String lobbyName){
        for(Lobby l : lobbies.values()){
            if(lobbyName.equals(l.getLobbyName())) {
                return l.getLobbyId();
            }
        }
        return -1;
    }

    /*
    public String toString(){
        String s = "";
        for (Lobby lobby : lobbies.values()) {
            s = s + lobby.getLobbyName() + " ";
        }
        return s;
    }*/

    /**
     * A method to get a List of at max 10 lobbies.
     * @return A String that contains a List of max 10 lobbies (that are not full).
     * Each line contains the Lobbies: Name,LobbyId, and the Amount of Players in the Lobby.
     * If no such lobbies are available the String contains the information about that.
     */

    public String getTopTen(){
        String s = "";
        int counter = 0;
        if(lobbies.size()>0){
            for(Lobby l : lobbies.values()){
                if(counter == 10){
                    break;
                }
                if(l.getPlayerAmount() == 1000){//TODO:100 durch maximale Spielerzahl ersetzen
                    continue;
                }else{
                    s = s + l.toString()+"║";
                    counter++;
                }
            }
            if(s.equals("")){
                s = "All Lobbies are full";
            }
        } else {
            s = "No Lobbies online";
        }
        return s;
    }


}
