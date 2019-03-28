package game;

import static game.Game.Stage.GAMEMENU;
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
import entities.light.LightMaster;
import game.map.ClientMap;
import game.map.Map;
import game.stages.GameMenu;
import game.stages.MainMenu;
import game.stages.Playing;
import gui.Chat;
import gui.Fps;
import gui.GuiString;
import java.util.ArrayList;
import java.util.List;
import org.joml.Vector3f;
import terrains.Terrain;
import terrains.TerrainFlat;
import util.RandomName;

/**
 * Playing is static for all intents and purposes. There will never be multiple instances of Playing
 * in the same execution. Getters and setters should be static so we can access them from anywhere,
 * but we have to be very careful which variables we want to be "global"
 */
public class Game extends Thread {

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

  // Playing instance and player settings
  // private static ClientLogic socketClient;
  /*
   * All entities that need to be rendered. I can see this moving to a "EntityMaster" class, but
   * is absolutely fine for now.
   * We keep all the Sub-Entities organized in Masters and keep this as a global Playing-Static
   * list with minimal maintenance.
   */
  private static final List<Entity> entities = new ArrayList<>();
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

  //map
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

  /*
   * Here are some functions that have no other place yet, but they all need to get out of this file
   * Most of them will be moved to the net package when working on the protocol
   */

  public static Player getActivePlayer() {
    return player;
  }



  /*
  public void addNetPlayer(NetPlayer netPlayer) {

    if (!netPlayer.getUsername().equals(username)) {
      boolean found = false;
      for (NetPlayer player : netPlayers) {
        if (player.equals(netPlayer)) {
          found = true;
        }
      }
      if (!found) {
        System.out.println("Adding " + netPlayer.getUsername() + " as a " +
         netPlayer.getModelStr() + ".");
        this.netPlayers.add(netPlayer);
      }
    }
  }

  public void removeNetPlayer(NetPlayer netPlayer) {
    this.netPlayers.remove(netPlayer);
  }

  public void removeNetPlayer(String username) {
    if (netPlayers.size() == 0) {
      return;
    }

    int index = 0;
    for (NetPlayer netPlayer : netPlayers) {
      if (netPlayer.getUsername().equals(username)) {
        break;
      }
      index++;
    }
    netPlayers.remove(index);

  }

  //This is just a quick mock up, please rewrite properly from scratch
  public void checkAndLoadNetPlayers(Loader loader) {

    if (loadedNetPlayers.size() < netPlayers.size()) {
      boolean found = false;
      NetPlayer enteringPlayer = null;
      for (NetPlayer netPlayer : netPlayers) {

        for (NetPlayer loadedNetPlayer : loadedNetPlayers) {
          if (netPlayer.equals(loadedNetPlayer)) {
            found = true;
            break;
          }
        }
        if (!found) {
          enteringPlayer = netPlayer;
        }
      }
      if (!found) {
        System.out.println("" + enteringPlayer.getUsername() + " has joined the game.");
        if (enteringPlayer.getModel() == null) {
          RawModel rawPlayer =
              loader.loadToVao(ObjFileLoader.loadObj(enteringPlayer.getModelStr()));
          TexturedModel defaultModel = new TexturedModel(rawPlayer,
              new ModelTexture(loader.loadTexture(enteringPlayer.getTextureStr())));
          enteringPlayer.setModel(defaultModel);
        }
        enteringPlayer.loadDirectionalUsername();
        entities.add(enteringPlayer);
        loadedNetPlayers.add(enteringPlayer);
      }
    } else if (loadedNetPlayers.size() > netPlayers.size()) {
      boolean found = false;
      NetPlayer leavingPlayer = null;
      for (NetPlayer loadedNetPlayer : loadedNetPlayers) {
        for (NetPlayer netPlayer : netPlayers) {
          if (loadedNetPlayer.equals(netPlayer)) {
            found = true;
            break;
          }
        }
        if (!found) {
          leavingPlayer = loadedNetPlayer;
        }
      }
      if (!found && leavingPlayer != null) {
        System.out.println("" + leavingPlayer.getUsername() + " has left the game.");
        loadedNetPlayers.remove(leavingPlayer);
        int index = 0;
        for (Entity entity : entities) {
          if (entity instanceof NetPlayer && entity.equals(leavingPlayer)) {
            break;
          }
          index++;
        }
        entities.remove(index);

      }
    }
  }*/

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

    // Used to load 3D models (.obj) and convert them to coordinates for the shaders, also
    // initializes the Bounding Boxes
    Loader loader = new Loader();

    // Initialize World. We can do this a little better once we have a proper algorithm to

    // Initialise blocks
    BlockMaster.init(loader);

    // generate the world
    GenerateWorld.generateTerrain(loader);
    //GenerateWorld.generateBlocks(loader);
    aboveGround = GenerateWorld.getAboveGround();
    belowGround = GenerateWorld.getBelowGround();
    if (aboveGround == null || belowGround == null) {
      System.err.println("Could not generate terrain.");
    }

    // generate map
    map = new ClientMap(30, 40, 1);
    System.out.println(map);

    // Initialize NetPlayerModels
    NetPlayerMaster.init(loader);

    // Initialize items
    ItemMaster.init(loader);

    // Initialize debris
    DebrisMaster.init();

    // Initiate the master renderer class
    MasterRenderer renderer = new MasterRenderer();

    // Generate the player. TODO (later): Move this in some player related class when more work on
    // network is done
    RawModel rawPlayer = loader.loadToVao(ObjFileLoader.loadObj(myModel));
    TexturedModel playerModel =
        new TexturedModel(rawPlayer, new ModelTexture(loader.loadTexture(myTexture)));
    player = new Player(playerModel, new Vector3f(90, 2, 3), 0, 0, 0, myModelSize);

    // GUI / HUD
    // TODO (Matthias): We will need a GuiMaster class to initialize and manage the GUI elements
    TextMaster.init(loader);
    GuiString.loadFont(loader);
    Fps fpsCounter = new Fps();
    // List<GuiTexture> guis = new ArrayList<>();
    chat = new Chat(loader);
    // guis.add(chat.getChatGui());
    guiRenderer = new GuiRenderer(loader);

    // Load Particle Master
    ParticleMaster.init(loader, MasterRenderer.getProjectionMatrix());

    // Lights and cameras (just one for now)
    LightMaster.generateLight(
        LightMaster.LightTypes.SUN, new Vector3f(0, 600, 200), new Vector3f(.3f, .3f, .3f));
    camera = new Camera(player, window);

    MainMenu.init(loader);
    GameMenu.init(loader);

    addActiveStage(PLAYING);

    //Connect after everything is loaded

    /*
    **************************************************************
    ---------HERE STARTS THE GAME LOOP!
    **************************************************************
    */
    while (!window.isClosed()) {
      if (window.isOneSecond()) {
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
        if (activeStages.contains(PLAYING)) {
          Playing.update(renderer);
        }

        if (activeStages.contains(MAINMENU)) {
          MainMenu.update();
        }

        if (activeStages.contains(GAMEMENU)) {
          GameMenu.update();
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

  private void disconnectFromServer() {
    // Stuff to do on disconnect
  }

  // Valid Stages
  public enum Stage {
    MAINMENU,
    LOBBIES,
    GAMEMENU,
    PLAYING
  }
}
