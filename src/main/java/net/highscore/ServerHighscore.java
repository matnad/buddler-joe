package net.highscore;

import java.io.Serializable;
import java.util.ArrayList;

public class ServerHighscore implements Serializable {

  private class Standing {

    float time;
    String username;

    public Standing(float time, String username) {
      this.time = time;
      this.username = username;
    }

    @Override
    public String toString() {
      return username + "║" + time;
    }
  }

  private ArrayList<Standing> highscore;

  public ServerHighscore() {
    this.highscore = new ArrayList<>();
  }

  public void addPlayer(float time, String username) {
    if (highscore.size() == 0) {
      highscore.add(new Standing(time, username));
    } else {
      for (int i = 0; i < 10; i++) {
        if (highscore.get(i).time < time) {
          highscore.add(i, new Standing(time, username));
        }
      }
    }
  }

  @Override
  public String toString(){
    return String.join("║", highscore.toString());
  }
}
