package net.playerhandling;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class Referee {

  private long timestamp;
  private int counter;
  private ConcurrentHashMap<Integer, Integer> allPerspectives;

  /*clientid(0,1,2,3,4 neu gesetzt), currentLives
  in map ist die perspektive jedes spielers bezüglich des einen crushes
  des einen spieler
  gespeichert.*/

  public Referee() {
    allPerspectives = new ConcurrentHashMap<>();
    this.counter = -1;
    this.timestamp = System.currentTimeMillis();
  }

  public boolean finalDecision() {
    int val = allPerspectives.get(0);
    for (int lives : allPerspectives.values()) {
      if (lives != val) {
        return false;
      }
      val = lives;
    }
    return true; // falls alle gleich
  }

  public void add(int currentLives) {
    if (System.currentTimeMillis() - timestamp <= 500) {
      allPerspectives.put(counter++, currentLives);
    }
  }

  public long getTimestamp() {
      return timestamp;
  }
}
