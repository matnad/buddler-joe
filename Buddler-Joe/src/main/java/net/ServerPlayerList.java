package net;

import java.util.HashMap;

public class ServerPlayerList {

    private HashMap<Integer, Player> players;

    public int addPlayer(Player player){
        if(players.containsKey(player.getClientId())){
            return -1;
        }
//        for(Player p : players.values()){
//            if(player.getUsername() == p.getUsername()) {
//                return -2;
//            }
//        }
        players.put(player.getClientId(), player);
        return 1;

    }

    public String searchName(int threadNr){
        if(players.containsKey(threadNr)){
            return players.get(threadNr).getUsername();
        } else{
            return null;
        }
    }

    public int searchClientId(String username){
        for(Player p : players.values()){
            if(username == p.getUsername()) {
                return p.getClientId();
            }
        }
        return -1;
    }

    public boolean removePlayer(int clientId){
        if(players.containsKey(clientId)){
            players.remove(clientId);
            return true;
        } else{
            return false;
        }
    }

    @Override
    public String toString() {
        return "ServerPlayerList{" +
                "players=" + players +
                '}';
    }
}
