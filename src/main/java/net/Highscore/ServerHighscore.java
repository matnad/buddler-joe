package net.Highscore;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

public class ServerHighscore implements Serializable {

    private HashMap<String, Float> highscore;
    public ServerHighscore() {
        highscore = new HashMap<String, Float>();
    }

    public int addPlayer(String username, float time){
        //for(String s : HashMa){

        //}
        return -1;
    }

    public HashMap<String, Float> getHighscore() {
        return highscore;
    }

    public void setHighscore(HashMap<String, Float> highscore) {
        this.highscore = highscore;
    }
}
