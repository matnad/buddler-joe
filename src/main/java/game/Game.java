package game;

import static game.Game.Stage.CHANGENAME;
import static game.Game.Stage.CHOOSELOBBY;
import static game.Game.Stage.CREDITS;
import static game.Game.Stage.GAMEMENU;
import static game.Game.Stage.GAMEOVER;
import static game.Game.Stage.HIGHSCORE;
import static game.Game.Stage.HISTORYMENU;
import static game.Game.Stage.INLOBBBY;
import static game.Game.Stage.LOADINGSCREEN;
import static game.Game.Stage.LOBBYCREATION;
import static game.Game.Stage.MAINMENU;
import static game.Game.Stage.OPTIONS;
import static game.Game.Stage.PLAYERLIST;
import static game.Game.Stage.PLAYING;

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
import game.stages.ChangeName;
import game.stages.ChooseLobby;
import game.stages.Credits;
import game.stages.GameMenu;
import game.stages.GameOver;
import game.stages.Highscore;
import game.stages.HistoryMenu;
import game.stages.InLobby;
import game.stages.LoadingScreen;
import game.stages.LobbyCreation;
import game.stages.MainMenu;
import game.stages.Options;
import game.stages.PlayerList;
import game.stages.Playing;
import gui.chat.Chat;
import gui.lifestatus.LifeStatus;
import gui.text.CurrentGold;
import gui.text.Fps;
import gui.text.GuiString;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import net.ClientLogic;
import net.StartNetworkOnlyClient;
import net.packets.lobby.PacketCreateLobby;
import net.packets.lobby.PacketJoinLobby;
import net.packets.loginlogout.PacketLogin;
import org.joml.Vector3f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import terrains.TerrainFlat;

/**
 * Playing is static for all intents and purposes. There will never be multiple instances of Playing
 * in the same execution. Getters and setters should be static so we can access them from anywhere,
 * but we have to be very careful which variables we want to be "global"
 */
public class Game extends Thread {

  private static final Logger logger = LoggerFactory.getLogger(Game.class);
  // Set up list of stages, stages will be updated at the end of every frame
  private static final List<Stage> activeStages = new CopyOnWriteArrayList<>();
  private static final List<Stage> stagesToBeAdded = new CopyOnWriteArrayList<>();
  /*
   * All entities that need to be rendered.
   * We keep all the Sub-Entities organized in Masters and keep this as a global Playing-Static
   * list with minimal maintenance.
   */
  private static final List<Entity> entities = new CopyOnWriteArrayList<>();
  // Set up windows with default vaules. However, setting values will be used later
  public static Window window = new Window(1080, 600, 60, "Buddler Joe");
  /*
   * We want everything set up so we could use multiple cameras, even if we don't end up needing
   * them.
   * Everything that relies on a camera object should know which camera it is using.
   * Update: We now have 2 camera objects, a spectator camera is generated when the player is
   * defeated.
   */
  public static Camera camera;
  // The duration of the last frame (delta time)
  private static double dt;
  // Set to true one frame every second
  private static boolean oncePerSecond = false;
  private static Loader loader = new Loader();
  // Settings
  private static SettingsSerialiser settingsSerialiser = new SettingsSerialiser();
  private static Settings settings;
  // Network related variables, still temporary/dummies
  private static boolean connectedToServer = false;
  private static boolean loggedIn = false;
  private static boolean lobbyCreated = false; // temporary
  // Variables to connect to the server
  private static String serverIp;
  private static int serverPort;
  // TODO (Moritz): Move chat to playing stage
  private static Chat chat;
  // Player and Gui elements
  private static Player player;
  private static LifeStatus lifeStatus;
  private static CurrentGold goldGuiText;
  // Map and Terrain
  private static ClientMap map;
  private static TerrainFlat[][] terrainChunks;
  private static GuiRenderer guiRenderer;
  // Lobby and Highscore lists
  private static CopyOnWriteArrayList<LobbyEntry> lobbyCatalog = new CopyOnWriteArrayList<>();
  private static CopyOnWriteArrayList<HighscoreEntry> highscoreCatalog =
      new CopyOnWriteArrayList<>();
  private static CopyOnWriteArrayList<String> historyCatalog = new CopyOnWriteArrayList<>();
  private static CopyOnWriteArrayList<String> playerList = new CopyOnWriteArrayList<>();
  private static CopyOnWriteArrayList<LobbyPlayerEntry> lobbyPlayerCatalog =
      new CopyOnWriteArrayList<>();
  public String username;
  // Set to true to create and join a lobby. For quicker testing.
  private boolean autoJoin = true;

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

  public static double dt() {
    return dt;
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
      stagesToBeAdded.add(stage);
    }
  }

  public static void removeActiveStage(Stage stage) {
    activeStages.remove(stage);
  }

  public static boolean isOncePerSecond() {
    return oncePerSecond;
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

  // Getters and Setters

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

  public static CopyOnWriteArrayList<String> getHistoryCatalog() {
    return historyCatalog;
  }

  public static void setHistoryCatalog(CopyOnWriteArrayList<String> historyCatalog) {
    Game.historyCatalog = historyCatalog;
  }

  /**
   * Returns the active camera that determines which View Matrix is used in the shaders.
   *
   * @return returns the active camera object for the game
   */
  public static Camera getActiveCamera() {
    return camera;
  }

  public static void setActiveCamera(Camera camera) {
    Game.camera = camera;
  }

  public static SettingsSerialiser getSettingsSerialiser() {
    return settingsSerialiser;
  }

  public static Player getActivePlayer() {
    return player;
  }

  /**
   * Get a 2D array of all wall terrain chunks for the current game map. If the terrain doesn't
   * exist yet, it is generated.
   *
   * @return 2D array with all wall terrain chunks [X][Y]
   */
  public static TerrainFlat[][] getTerrainChunks() {
    if (terrainChunks == null) {
      if (map == null) {
        throw new IllegalStateException("No Map found, could not generate Terrain.");
      }
      terrainChunks = map.generateTerrains(loader);
    }
    return terrainChunks;
  }

  public static void setTerrainChunks(TerrainFlat[][] terrainChunks) {
    Game.terrainChunks = terrainChunks;
  }

  public static Chat getChat() {
    return chat;
  }

  public static LifeStatus getLifeStatus() {
    return lifeStatus;
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

  public static List<Entity> getEntities() {
    return entities;
  }

  public static boolean isConnectedToServer() {
    return connectedToServer;
  }

  public static void setConnectedToServer(boolean connectedToServer) {
    Game.connectedToServer = connectedToServer;
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

  public static Loader getLoader() {
    return loader;
  }

  public static String getServerIp() {
    return serverIp;
  }

  public static CopyOnWriteArrayList<String> getPlayerList() {
    return playerList;
  }

  public static void setPlayerList(CopyOnWriteArrayList<String> playerList) {
    Game.playerList = playerList;
  }

  /** Here we initialize all the Masters and other classes and generate the world. */
  @Override
  public void run() {
    loadSettings();

    // Create GLFW Window
    window.setSize(settings.getWidth(), settings.getHeight());
    window.setFullscreen(settings.isFullscreen());
    window.create();

    // Initiate the master renderer class
    MasterRenderer renderer = new MasterRenderer();

    // Initialize NetPlayerModels
    NetPlayerMaster.init(loader);

    // Initialize TextMaster
    TextMaster.init(loader);

    // Initialise blocks
    BlockMaster.init(loader);

    // Initialize GUI Renderer
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
    lifeStatus = new LifeStatus(loader);
    Fps fpsCounter = new Fps();

    // Initialize Particle Master
    ParticleMaster.init(loader, MasterRenderer.getProjectionMatrix());

    // Initialize items
    ItemMaster.init(loader);

    // Initialize debris
    DebrisMaster.init();

    // Lights ("Suns")
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

    // Debug

    /*
    **************************************************************
    ---------HERE STARTS THE GAME LOOP!
    **************************************************************
    */

    // Variables for Time Invariance and Frame Rate control
    double timePerFrame = 1000 / 60.0;
    double secondTimer = 0;
    int frames = 0;
    double frameStartTime;
    while (!window.isClosed()) {

      // Note when we start the frame to calculate the duration later
      frameStartTime = System.nanoTime();

      // This will be true exactly once per second, independent of frame rate
      // Used for actions that need to be done infrequently. oncePerSecond can be used anywhere
      if (secondTimer > 1e9) {
        oncePerSecond = true;
        secondTimer -= 1e9;
        fpsCounter.updateString("" + frames); // Display Frame counter
        frames = 0;
      }

      // check each stage in sequence and render it if active
      // Order matters, later stages will be rendered on top of earlier stages

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

        if (activeStages.contains(INLOBBBY)) {
          InLobby.update();
        }

        if (activeStages.contains(LOBBYCREATION)) {
          LobbyCreation.update();
        }

        if (activeStages.contains(CHANGENAME)) {
          ChangeName.update();
        }

        if (activeStages.contains(PLAYERLIST)) {
          PlayerList.update();
        }
        if (activeStages.contains(HISTORYMENU)) {
          HistoryMenu.update();
        }
      }

      activeStages.addAll(stagesToBeAdded);
      stagesToBeAdded.clear();

      // Done with one frame

      window.swapBuffers();
      oncePerSecond = false;

      // Calculate how long the current frame took to process
      double frameTime = (System.nanoTime() - frameStartTime) / 1e6;

      // If it was less than the allowed time, wait the rest
      if (frameTime < timePerFrame) {
        try {
          Thread.sleep((long) (timePerFrame - frameTime));
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }

      // Save the time needed for this frame to be used in the next frame
      dt = (System.nanoTime() - frameStartTime) / 1e9;

      // Count actual fps and keep a running timer
      frames++;
      secondTimer += (System.nanoTime() - frameStartTime);
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
    // Credits.init(loader);
    LoadingScreen.progess();
    Options.init(loader);
    LoadingScreen.progess();
    Highscore.init(loader);
    LoadingScreen.progess();
    PlayerList.init(loader);
    LoadingScreen.progess();
    InLobby.init(loader);
    LoadingScreen.progess();
    GameOver.init(loader);
    LoadingScreen.progess();
    LobbyCreation.init(loader);
    LoadingScreen.progess();
    ChangeName.init(loader);
    LoadingScreen.progess();
    HistoryMenu.init(loader);

    // Generate ServerPlayer
    NetPlayer.init(loader);
    player = new Player(getUsername(), new Vector3f(90, 2, 3), 0, 0, 0);

    // Generate dummy map
    map = new ClientMap(1, 1, -1);

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
    String lobname = String.valueOf(new Random().nextInt((int) 10e15));
    LoadingScreen.updateLoadingMessage("joining lobby");
    new PacketCreateLobby(lobname + "║s").sendToServer();
    while (!lobbyCreated) {
      Thread.sleep(50);
    }
    // }

    if (autoJoin) {

      new PacketJoinLobby(lobname).sendToServer();
      while (!NetPlayerMaster.getLobbyname().equals(lobname)) {
        Thread.sleep(50);
      }
      LoadingScreen.updateLoadingMessage("generating map");
      while (map.isLocal()) {
        Thread.sleep(500);
      }
    }

    // Camera
    camera = new Camera(player, window);
    // camera = new SpectatorCamera(window);

    // GUI / Other
    goldGuiText = new CurrentGold();
    // livesGuiText = new CurrentLives();
    // lifestatus = new LifeStatus(loader);
    Playing.init(loader);

    LoadingScreen.updateLoadingMessage("Ready!");
    Thread.sleep(500);
    LoadingScreen.done();
    if (autoJoin) {
      // new PacketReady().sendToServer();
      addActiveStage(INLOBBBY);
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

  public String getUsername() {
    return settings.getUsername();
  }

  /**
   * Set new username and save it to settings.
   *
   * <p>Will be serialized and stored on the harddisk.
   *
   * @param username new Username
   */
  public void setUsername(String username) {
    settings.setUsername(username);
    settingsSerialiser.serialiseSettings(settings);
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
    CHANGENAME,
    PLAYERLIST,
    HISTORYMENU,
    LOBBYCREATION
  }
}
