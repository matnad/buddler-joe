package net.playerhandling;

import java.util.HashMap;

public class Referee {

  private int counter;
  private HashMap<Integer, Integer> allPerspectives;
  // clientid(0,1,2,3,4 neu gesetzt), currentLives
  // in map ist die perspektive jedes spielers bezüglich des einen crushes
  // des einen spieler
  // gespeichert.
  public Referee() {
    allPerspectives = new HashMap<>();
    this.counter = -1;
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
    allPerspectives.put(counter++, currentLives);
  }
}
