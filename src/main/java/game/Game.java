package game;

import static game.Game.Stage.CHOOSELOBBY;
import static game.Game.Stage.CREDITS;
import static game.Game.Stage.GAMEMENU;
import static game.Game.Stage.GAMEOVER;
import static game.Game.Stage.HIGHSCORE;
import static game.Game.Stage.INLOBBBY;
import static game.Game.Stage.LOADINGSCREEN;
import static game.Game.Stage.LOBBYCREATION;
import static game.Game.Stage.LOGIN;
import static game.Game.Stage.MAINMENU;
import static game.Game.Stage.OPTIONS;
import static game.Game.Stage.PLAYING;
import static game.Game.Stage.WELCOME;

import engine.io.InputHandler;
import engine.io.Window;
import engine.particles.ParticleMaster;
import engine.render.GuiRenderer;
import engine.render.Loader;
import engine.render.MasterRenderer;
import engine.render.fontrendering.TextMaster;
import entities.Camera;
import entities.Entity;
import entities.NetPlayer;
import entities.Player;
import entities.blocks.BlockMaster;
import entities.blocks.debris.DebrisMaster;
import entities.items.ItemMaster;
import entities.light.LightMaster;
import game.map.ClientMap;
import game.stages.ChooseLobby;
import game.stages.Credits;
import game.stages.GameMenu;
import game.stages.GameOver;
import game.stages.Highscore;
import game.stages.InLobby;
import game.stages.LoadingScreen;
import game.stages.LobbyCreation;
import game.stages.Login;
import game.stages.MainMenu;
import game.stages.Options;
import game.stages.Playing;
import game.stages.Welcome;
import gui.chat.Chat;
import gui.text.CurrentGold;
import gui.text.CurrentLives;
import gui.text.Fps;
import gui.text.GuiString;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import net.ClientLogic;
import net.StartNetworkOnlyClient;
import net.packets.lobby.PacketCreateLobby;
import net.packets.lobby.PacketJoinLobby;
import net.packets.loginlogout.PacketLogin;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import terrains.Terrain;
import terrains.TerrainFlat;
import util.RandomName;

//  import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

/**
 * Playing is static for all intents and purposes. There will never be multiple instances of Playing
 * in the same execution. Getters and setters should be static so we can access them from anywhere,
 * but we have to be very careful which variables we want to be "global"
 */
public class Game extends Thread {

  private static final Logger logger = LoggerFactory.getLogger(Game.class);

  // Set up GLFW Window
  private static final List<Stage> activeStages = new ArrayList<>();
  private static final List<Stage> stagesToBeAdded = new ArrayList<>();
  // Playing instance and player settings
  // private static ClientLogic socketClient;
  /*
   * All entities that need to be rendered. I can see this moving to a "EntityMaster" class, but
   * is absolutely fine for now.
   * We keep all the Sub-Entities organized in Masters and keep this as a global Playing-Static
   * list with minimal maintenance.
   */
  private static final List<Entity> entities = new CopyOnWriteArrayList<>();
  // TODO: window to private and create getter
  public static Window window = new Window(1080, 600, 60, "Buddler Joe");
  // maybe needs its own class or should at least be moved to NetPlayer
  /*
   * We want everything set up so we could use multiple cameras, even if we don't end up needing
   * them.
   * Everything that relies on a camera object should know which camera it is using.
   * Update: We now have 2 camera objects, a spectator camera is generated when the player is
   * defeated.
   */
  public static Camera camera;
  private static SettingsSerialiser settingsSerialiser = new SettingsSerialiser();
  // Network related variables, still temporary/dummies
  private static boolean connectedToServer = false;
  private static boolean loggedIn = false;
  private static boolean lobbyCreated = false; // temporary

  private static String serverIp;
  private static int serverPort;
  // This probably needs to go somewhere else when we work on the chat
  private static Chat chat;
  private static Player player;
  private static CurrentGold goldGuiText;
  private static CurrentLives livesGuiText;
  // map
  private static ClientMap map;
  /*
   * Keep track of connected players.
   * TODO (Client Network Team): This needs to be moved to a different class that handles
   * connected players.
   */
  private static Terrain aboveGround;
  private static TerrainFlat belowGround;
  private static GuiRenderer guiRenderer;
  private static CopyOnWriteArrayList<LobbyEntry> lobbyCatalog = new CopyOnWriteArrayList<>();
  private static CopyOnWriteArrayList<HighscoreEntry> highscoreCatalog =
      new CopyOnWriteArrayList<>();
  private static CopyOnWriteArrayList<LobbyPlayerEntry> lobbyPlayerCatalog =
      new CopyOnWriteArrayList<>();
  public String username = RandomName.getRandomName(); // TODO (Server Team): Username
  /*
   * Set your resolution here, feel free to add new entries and comment them with your name/machine
   * If someone wants to work on this, edit this comment or add an issue to the tracker in gitlab
   */
  private boolean autoJoin = false;
  private static Settings settings;

  /**
   * The constructor for the game to be called from the main class.
   *
   * @param ipAddress The ip address to be connected to
   * @param port The port to be connected to
   * @param username The chosen username of the user
   */
  public Game(String ipAddress, int port, String username) {
    serverIp = ipAddress;
    serverPort = port;
    this.username = username;
  }

  public static CopyOnWriteArrayList<LobbyEntry> getLobbyCatalog() {
    return lobbyCatalog;
  }

  public static void setLobbyCatalog(CopyOnWriteArrayList<LobbyEntry> lobbyCatalog) {
    Game.lobbyCatalog = lobbyCatalog;
  }

  public static CopyOnWriteArrayList<HighscoreEntry> getHighscoreCatalog() {
    return highscoreCatalog;
  }

  public static void setHighscoreCatalog(CopyOnWriteArrayList<HighscoreEntry> highscoreCatalog) {
    Game.highscoreCatalog = highscoreCatalog;
  }

  public static CopyOnWriteArrayList<LobbyPlayerEntry> getLobbyPlayerCatalog() {
    return lobbyPlayerCatalog;
  }

  public static void setLobbyPlayerCatalog(
      CopyOnWriteArrayList<LobbyPlayerEntry> lobbyPlayerCatalog) {
    Game.lobbyPlayerCatalog = lobbyPlayerCatalog;
  }

  /**
   * Any entity added via this function will be passed to the Master Renderer and rendered in the
   * Playing World.
   *
   * @param entity any entity that can be rendered
   */
  public static void addEntity(Entity entity) {
    entities.add(entity);
  }

  /**
   * Stops an entity from being passed to the Master Renderer and being rendered in the Playing
   * World. Can be used to effectively destroy an entity in the game.
   *
   * @param entity entity that should no longer be rendered
   */
  public static void removeEntity(Entity entity) {
    entities.remove(entity);
  }

  public static List<Entity> getEntities() {
    return entities;
  }

  public static boolean isConnectedToServer() {
    return connectedToServer;
  }

  public static void setConnectedToServer(boolean connectedToServer) {
    Game.connectedToServer = connectedToServer;
  }

  /**
   * Returns the active camera that determines which View Matrix is used in the shaders.
   *
   * @return returns the active camera object for the game
   */
  public static Camera getActiveCamera() {
    return camera;
  }

  public static SettingsSerialiser getSettingsSerialiser() {
    return settingsSerialiser;
  }

  public static void setActiveCamera(Camera camera) {
    Game.camera = camera;
  }

  public static Player getActivePlayer() {
    return player;
  }

  public static Terrain getAboveGround() {
    return aboveGround;
  }

  public static TerrainFlat getBelowGround() {
    return belowGround;
  }

  public static Chat getChat() {
    return chat;
  }

  public static List<Stage> getActiveStages() {
    return activeStages;
  }

  public static ClientMap getMap() {
    return map;
  }

  public static void setMap(ClientMap map) {
    Game.map = map;
  }

  public static void setLoggedIn(boolean loggedIn) {
    Game.loggedIn = loggedIn;
  }

  public static void setLobbyCreated(boolean lobbyCreated) {
    Game.lobbyCreated = lobbyCreated;
  }

  public static Settings getSettings() {
    return settings;
  }

  /**
   * Add a stage to the game loop.
   *
   * <p>Will include this stage to be processed each game loop. Can not add the same stage multiple
   * times.
   *
   * @param stage stage to add to the game loop
   */
  public static void addActiveStage(Stage stage) {
    if (!activeStages.contains(stage) && !stagesToBeAdded.contains(stage)) {
      // activeStages.add(stage);
      stagesToBeAdded.add(stage);
    }
  }

  public static void removeActiveStage(Stage stage) {
    activeStages.remove(stage);
  }

  public static GuiRenderer getGuiRenderer() {
    return guiRenderer;
  }

  public static void setWindow(Window window) {
    Game.window = window;
  }

  public static CurrentGold getGoldGuiText() {
    return goldGuiText;
  }

  public static CurrentLives getLivesGuiText() {
    return livesGuiText;
  }

  public String getUsername() {
    return settings.getUsername();
  }

  public void setUsername(String username) {
    settings.setUsername(username);
    settingsSerialiser.serialiseSettings(settings);
  }

  /** Initialize the Playing Thread (Treat this like a constructor). */
  @Override
  public synchronized void start() {
    // Start the thread
    super.start();
  }

  /** Here we initialize all the Masters and other classes and generate the world. */
  @Override
  public void run() {
    loadSettings();
    // Create GLFW Window, we run this in a thread.
    window.setSize(settings.getWidth(), settings.getHeight());
    window.setFullscreen(settings.isFullscreen());
    window.create();

    // Initiate the master renderer class
    MasterRenderer renderer = new MasterRenderer();

    // Used to load 3D models (.obj) and convert them to coordinates for the shaders, also
    // initializes the Bounding Boxes
    Loader loader = new Loader();

    // Stuff before loading screen
    // generate the world
    GenerateWorld.generateTerrain(loader);
    // GenerateWorld.generateBlocks(loader);
    aboveGround = GenerateWorld.getAboveGround();
    belowGround = GenerateWorld.getBelowGround();
    if (aboveGround == null || belowGround == null) {
      logger.error("Could not generate terrain.");
    }

    // Initialize NetPlayerModels
    NetPlayerMaster.init(loader);

    // Initialize TextMaster
    TextMaster.init(loader);
    // Initialise blocks
    BlockMaster.init(loader);
    guiRenderer = new GuiRenderer(loader);
    GuiString.loadFont(loader);

    // Stages
    LoadingScreen.init(loader);
    activeStages.add(LOADINGSCREEN);
    LoadingScreen.updateLoadingMessage("starting game");

    // Connect to server and load level in an extra thread
    try {
      loadGame(loader);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    chat = new Chat(loader, 12, 0.34f);
    Fps fpsCounter = new Fps();

    // Initialize Particle Master
    ParticleMaster.init(loader, MasterRenderer.getProjectionMatrix());

    // Initialize items
    ItemMaster.init(loader);

    // Initialize debris
    DebrisMaster.init();

    // Lights and cameras (just one for now)
    LightMaster.generateLight(
            LightMaster.LightTypes.SUN,
            new Vector3f(0, 400, 200),
            new Vector3f(1, 1, 1).normalize())
        .setBrightness(2f);
    LightMaster.generateLight(
            LightMaster.LightTypes.SUN,
            new Vector3f(300, 400, 200),
            new Vector3f(1, 1, 1).normalize())
        .setBrightness(2f);

    // Connect after everything is loaded

    /*
    **************************************************************
    ---------HERE STARTS THE GAME LOOP!
    **************************************************************
    */
    while (!window.isClosed()) {
      if (window.isOneSecond() && activeStages.contains(PLAYING)) {
        // This runs once per second, we can use it for stuff that needs less frequent updates
        fpsCounter.updateString("" + window.getCurrentFps());
      }

      // ...

      // This runs super often, we shouldn't use it

      // ...

      if (window.isUpdating()) {
        /* This runs once per frame. Do all the updating here.
           The order of things is quite relevant here
           Optimally this should be mostly Masters here
        */

        // List<Stage> stagesForThisFrame = new ArrayList<>(activeStages);

        if (activeStages.contains(LOADINGSCREEN)) {
          LoadingScreen.update();
        } else if (activeStages.contains(GAMEOVER)) {
          GameOver.update();
        } else {
          /*InputHandler needs to be BEFORE polling (window.update()) so we still have access to
          the events of last Frame. Everything else should be after polling.*/
          InputHandler.update();
          Game.window.update();

          if (activeStages.contains(PLAYING)) {
            Playing.update(renderer);
          }

          if (activeStages.contains(MAINMENU)) {
            MainMenu.update();
          }

          if (activeStages.contains(GAMEMENU)) {
            GameMenu.update();
          }

          if (activeStages.contains(CHOOSELOBBY)) {
            ChooseLobby.update();
          }

          if (activeStages.contains(HIGHSCORE)) {
            Highscore.update();
          }

          if (activeStages.contains(CREDITS)) {
            Credits.update();
          }

          if (activeStages.contains(OPTIONS)) {
            Options.update();
          }

          if (activeStages.contains(WELCOME)) {
            Welcome.update();
          }

          if (activeStages.contains(LOGIN)) {
            Login.update();
          }

          if (activeStages.contains(INLOBBBY)) {
            InLobby.update();
          }

          if (activeStages.contains(LOBBYCREATION)) {
            LobbyCreation.update();
          }
        }

        activeStages.addAll(stagesToBeAdded);
        stagesToBeAdded.clear();

        // System.out.println("-----------------------------------");
        // System.out.println(activeStages);
        // Done with one frame

        window.swapBuffers();
      }
    }

    /*
    **************************************************************
    ---------HERE ENDS THE GAME LOOP!
    **************************************************************
    */

    // Clean up memory and unbind openGL shader programs
    TextMaster.cleanUp();
    guiRenderer.cleanUp();
    renderer.cleanUp();
    loader.cleanUp();
    ParticleMaster.cleanUp();

    // Close and disconnect (still need a window close callback)
    window.kill();
    if (connectedToServer) {
      disconnectFromServer();
    }
    System.exit(1); // For now...
  }

  private void loadGame(Loader loader) throws InterruptedException {
    // Load Stages
    MainMenu.init(loader);
    LoadingScreen.progess();
    GameMenu.init(loader);
    LoadingScreen.progess();
    ChooseLobby.init(loader);
    LoadingScreen.progess();
    Credits.init(loader);
    LoadingScreen.progess();
    Options.init(loader);
    LoadingScreen.progess();
    Welcome.init(loader);
    LoadingScreen.progess();
    Login.init(loader);
    LoadingScreen.progess();
    Highscore.init(loader);
    LoadingScreen.progess();
    InLobby.init(loader);
    LoadingScreen.progess();
    GameOver.init(loader);
    LoadingScreen.progess();
    LobbyCreation.init(loader);

    // Generate Player
    NetPlayer.init(loader);
    player = new Player(getUsername(), new Vector3f(90, 2, 3), 0, 0, 0);

    // Connecting to Server
    LoadingScreen.updateLoadingMessage("connecting to server");
    new Thread(() -> StartNetworkOnlyClient.startWith(serverIp, serverPort)).start();
    while (!ClientLogic.isConnected()) {
      Thread.sleep(50);
    }

    // Logging in
    LoadingScreen.updateLoadingMessage("logging in");
    new PacketLogin(getUsername()).sendToServer();
    while (!loggedIn) {
      Thread.sleep(50);
    }
    System.out.println("logged in");

    // Creating and joining Lobby
    // if (autoJoin) {
    LoadingScreen.updateLoadingMessage("joining lobby");
    new PacketCreateLobby("lob1║m").sendToServer();
    while (!lobbyCreated) {
      Thread.sleep(50);
    }
    // }
    // Generate dummy map
    map = new ClientMap(1, 1, 1);
    if (autoJoin) {
      new PacketJoinLobby("lob1").sendToServer();
      while (!NetPlayerMaster.getLobbyname().equals("lob1")) {
        Thread.sleep(50);
      }
      LoadingScreen.updateLoadingMessage("generating map");
      while (map.isLocal()) {
        Thread.sleep(500);
      }
    }
    // Camera
    camera = new Camera(player, window);

    // GUI / Other
    goldGuiText = new CurrentGold();
    livesGuiText = new CurrentLives();
    Playing.init(loader);

    LoadingScreen.updateLoadingMessage("Ready!");
    Thread.sleep(500);
    LoadingScreen.done();
    if (autoJoin) {
      addActiveStage(PLAYING);
    } else {
      addActiveStage(MAINMENU);
    }
    removeActiveStage(LOADINGSCREEN);
  }

  private void disconnectFromServer() {
    // Stuff to do on disconnect
  }

  /** Method to load the settings out of the serialised file. */
  public void loadSettings() {
    if (settingsSerialiser.readSettings() != null) {
      settings = settingsSerialiser.readSettings();
      if (!username.equals(settings.getUsername())) {
        this.username = settings.getUsername();
      }
    } else {
      settings = new Settings();
    }
  }

  // Valid Stages
  public enum Stage {
    MAINMENU,
    CHOOSELOBBY,
    GAMEMENU,
    PLAYING,
    LOADINGSCREEN,
    CREDITS,
    OPTIONS,
    WELCOME,
    LOGIN,
    INLOBBBY,
    HIGHSCORE,
    GAMEOVER,
    LOBBYCREATION
  }
}
