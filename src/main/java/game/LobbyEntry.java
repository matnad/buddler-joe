package game;

public class LobbyEntry {

    private String name;
    private int players;

    public LobbyEntry(String name, String n){
        this.name = name;
        this.players = Integer.parseInt(n);
    }

    public int getPlayers() {
        return players;
    }
    public String getName() {
        return name;
    }
    public String toString(){
        return name;
    }

}
