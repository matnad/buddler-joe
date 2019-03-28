import game.Game;
import net.StartNetworkOnlyClient;

/** Start the Main Game Thread. */
public class Main {

  /**
   * Start the GUI and the Network client of the game.
   *
   * @param args not used*/
  public static void main(String[] args) {
    Game game = new Game();
    game.start();
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    //StartNetworkOnlyClient.main(new String[] {});
  }
}
