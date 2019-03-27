import game.Game;
import net.StartNetworkOnlyClient;

/** Start the Main Game Thread. */
public class Main {

  public static void main(String[] args) {
    Game game = new Game();
    game.start();
    StartNetworkOnlyClient.main(new String[] {});
  }
}
