import game.Game;

/**
 * Start the Main Playing Thread
 */
public class Main {

    private static Game game;

    public static void main(String[] args) {
        game = new Game();
        game.start();
    }
}
