package game;

import static game.Game.Stage.GAMEMENU;
import static game.Game.Stage.LOADINGSCREEN;
import static game.Game.Stage.MAINMENU;
import static game.Game.Stage.PLAYING;

import engine.io.Window;
import engine.models.RawModel;
import engine.models.TexturedModel;
import engine.particles.ParticleMaster;
import engine.render.GuiRenderer;
import engine.render.Loader;
import engine.render.MasterRenderer;
import engine.render.fontrendering.TextMaster;
import engine.render.objconverter.ObjFileLoader;
import engine.textures.ModelTexture;
import entities.Camera;
import entities.Entity;
import entities.Player;
import entities.blocks.BlockMaster;
import entities.blocks.debris.DebrisMaster;
import entities.items.ItemMaster;
import entities.light.Light;
import entities.light.LightMaster;
import game.map.ClientMap;
import game.stages.GameMenu;
import game.stages.LoadingScreen;
import game.stages.MainMenu;
import game.stages.Playing;
import gui.Chat;
import gui.Fps;
import gui.GuiString;
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

/**
 * Playing is static for all intents and purposes. There will never be multiple instances of Playing
 * in the same execution. Getters and setters should be static so we can access them from anywhere,
 * but we have to be very careful which variables we want to be "global"
 */
public class Game extends Thread {

  private static final Logger logger = LoggerFactory.getLogger(Game.class);

  /*
   * Set your resolution here, feel free to add new entries and comment them with your name/machine
   * TODO (anyone): We need a JSON file to store user settings.
   * If someone wants to work on this, edit this comment or add an issue to the tracker in gitlab
   */

  private static final int WIDTH = 1280;
  private static final int HEIGHT = 800;
  private static final int FPS = 60;
  public static final Window window = new Window(WIDTH, HEIGHT, FPS, "Buddler Joe");
  // Set up GLFW Window
  private static final boolean fullscreen = false;
  private static final List<Stage> activeStages = new ArrayList<>();
  // Network related variables, still temporary/dummies
  // private static boolean doConnectToServer = false; //Multiplayer: true, Singleplayer: false
  private static boolean connectedToServer = false;
  private static boolean loggedIn = false;
  private static boolean lobbyCreated = false; // temporary

  // Playing instance and player settings
  // private static ClientLogic socketClient;
  /*
   * All entities that need to be rendered. I can see this moving to a "EntityMaster" class, but
   * is absolutely fine for now.
   * We keep all the Sub-Entities organized in Masters and keep this as a global Playing-Static
   * list with minimal maintenance.
   */
  private static final List<Entity> entities = new CopyOnWriteArrayList<>();
  public static String username = RandomName.getRandomName(); // TODO (Server Team): Username
  // maybe needs its own class or should at least be moved to NetPlayer
  /*
   * TODO (Matthias): Change camera to non-static.
   * We want everything set up so we could use multiple cameras, even if we don't end up needing
   * them.
   * Everything that relies on a camera object should know which camera it is using
   */
  public static Camera camera;
  /*
   * A Temporary, crude "skin-selector". We will work on this when we do GUI Stuff.
   */
  public static String myModel;
  public static String myTexture;
  public static float myModelSize;
  // This probably needs to go somewhere else when we work on the chat
  private static Chat chat;
  private static Player player;
  private Fps fpsCounter;

  // map
  private static ClientMap map;

  /*
   * Keep track of connected players.
   * TODO (Client Network Team): This needs to be moved to a different class that handles
   * connected players.
   */
  // private List<NetPlayer> netPlayers = new ArrayList<>();
  // private List<NetPlayer> loadedNetPlayers = new ArrayList<>();
  private static Terrain aboveGround;
  private static TerrainFlat belowGround;
  private static GuiRenderer guiRenderer;

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

  public static String getUsername() {
    return username;
  }

  /**
   * Returns the active camera that determines which View Matrix is used in the shaders.
   *
   * @return returns the active camera object for the game
   */
  public static Camera getActiveCamera() {
    return camera;
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

  public static void setLoggedIn(boolean loggedIn) {
    Game.loggedIn = loggedIn;
  }

  public static void setLobbyCreated(boolean lobbyCreated) {
    Game.lobbyCreated = lobbyCreated;
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
    if (!activeStages.contains(stage)) {
      activeStages.add(stage);
    }
  }

  public static void removeActiveStage(Stage stage) {
    activeStages.remove(stage);
  }

  public static GuiRenderer getGuiRenderer() {
    return guiRenderer;
  }

  /** Initialize the Playing Thread (Treat this like a constructor). */
  @Override
  public synchronized void start() {

    // select model
    /*//Penguin
    myModel = "penguin";
    myTexture = "penguin";
    myModelSize = 2.5f;

    //Rabbit
    myModel = "bunny";
    myTexture = "bunny";
    myModelSize = .15f;*/

    // Person
    myModel = "person";
    myTexture = "person";
    myModelSize = .4f;

    // Start the thread
    super.start();
  }

  /** Here we initialize all the Masters and other classes and generate the world. */
  @Override
  public void run() {
    // Create GLFW Window, we run this in a thread.
    window.setSize(WIDTH, HEIGHT);
    window.setFullscreen(fullscreen);
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

    RawModel rawPlayer = loader.loadToVao(ObjFileLoader.loadObj(myModel));
    TexturedModel playerModel =
        new TexturedModel(rawPlayer, new ModelTexture(loader.loadTexture(myTexture)));

    //Stages
    MainMenu.init(loader);
    GameMenu.init(loader);

    // Initialize NetPlayerModels
    NetPlayerMaster.init(loader);

    // Initialize TextMaster
    TextMaster.init(loader);
    // Initialise blocks
    BlockMaster.init(loader);
    guiRenderer = new GuiRenderer(loader);
    GuiString.loadFont(loader);
    LoadingScreen.init(loader);
    addActiveStage(LOADINGSCREEN);
    new Thread(
            () -> {
              try {
                loadGame(playerModel);
              } catch (InterruptedException e) {
                logger.error("Problem with sleep in Game Loader.");
              }
            })
        .start();

    chat = new Chat(loader);
    fpsCounter = new Fps();

    // Initialize Particle Master
    ParticleMaster.init(loader, MasterRenderer.getProjectionMatrix());

    // Initialize items
    ItemMaster.init(loader);

    // Initialize debris
    DebrisMaster.init();

    // Lights and cameras (just one for now)
    LightMaster.generateLight(
        LightMaster.LightTypes.SUN, new Vector3f(0, 600, 200), new Vector3f(.3f, .3f, .3f));


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

        if (activeStages.contains(LOADINGSCREEN)) {
          LoadingScreen.update();
        } else {

          if (activeStages.contains(PLAYING)) {
            Playing.update(renderer);
          }

          if (activeStages.contains(MAINMENU)) {
            MainMenu.update();
          }

          if (activeStages.contains(GAMEMENU)) {
            GameMenu.update();
          }
        }

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

  private void loadGame(TexturedModel playerModel) throws InterruptedException {

    // Generate Player
    player = new Player(playerModel, new Vector3f(90, 2, 3), 0, 0, 0, myModelSize);

    // Connecting to Server
    LoadingScreen.updateLoadingMessage("connecting to server");
    new Thread(() -> StartNetworkOnlyClient.main(new String[] {})).start();
    while (!ClientLogic.isConnected()) {
      Thread.sleep(50);
    }
    connectedToServer = true;

    // Logging in
    LoadingScreen.updateLoadingMessage("logging in");
    new PacketLogin(Game.getUsername()).sendToServer();
    while (!loggedIn) {
      Thread.sleep(50);
    }
    System.out.println("logged in");

    // Creating and joining Lobby
    LoadingScreen.updateLoadingMessage("joining lobby");
    new PacketCreateLobby("lob1").sendToServer();
    while (!lobbyCreated) {
      Thread.sleep(50);
    }

    // Generate dummy map
    map = new ClientMap(1, 1, 1);

    new PacketJoinLobby("lob1").sendToServer();
    while (!NetPlayerMaster.getLobbyname().equals("lob1")) {
      Thread.sleep(50);
    }

    LoadingScreen.updateLoadingMessage("generating map");

    while (map.isLocal()) {
      Thread.sleep(50);
    }

    // Camera
    camera = new Camera(player, window);

    LoadingScreen.updateLoadingMessage("done!");
    Thread.sleep(500);
    LoadingScreen.done();
    addActiveStage(PLAYING);
    removeActiveStage(LOADINGSCREEN);
  }

  private void disconnectFromServer() {
    // Stuff to do on disconnect
  }

  // Valid Stages
  public enum Stage {
    MAINMENU,
    LOBBIES,
    GAMEMENU,
    PLAYING,
    LOADINGSCREEN
  }
}
