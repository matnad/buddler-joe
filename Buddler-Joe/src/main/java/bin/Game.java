package bin;

import engine.io.InputHandler;
import engine.io.Window;
import engine.models.RawModel;
import engine.models.TexturedModel;
import engine.particles.ParticleMaster;
import engine.render.GuiRenderer;
import engine.render.Loader;
import engine.render.MasterRenderer;
import engine.render.fontRendering.TextMaster;
import engine.render.objConverter.OBJFileLoader;
import engine.textures.ModelTexture;
import entities.*;
import entities.blocks.BlockMaster;
import entities.blocks.debris.DebrisMaster;
import entities.items.ItemMaster;
import entities.light.Light;
import entities.light.LightMaster;
import gui.Chat;
import gui.FPS;
import gui.GUIString;
import gui.GuiTexture;
import net.ClientLogic;
import org.joml.Vector3f;
import terrains.Terrain;
import terrains.TerrainFlat;
import util.MousePlacer;
import util.RandomName;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

/**
 * Game is static for all intents and purposes. There will never be multiple instances of Game in the same execution.
 * Getters and setters should be static so we can access them from anywhere, but we have to be very careful
 * which variables we want to be "global"
 */
public class Game extends Thread {

    /*
     * Set your resolution here, feel free to add new entries and comment them with your name/machine
     * TODO (anyone): We need a JSON file to store user settings.
     * If someone wants to work on this, edit this comment or add an issue to the tracker in gitlab
     */
//    public static final int WIDTH = 2560/2, HEIGHT = 1600/2, FPS = 60; //Mac Book Pro Half
//    public static final int WIDTH = 2560, HEIGHT = 1600, FPS = 60; //Mac Book Pro
//    public static final int WIDTH = 800, HEIGHT = 600, FPS = 60; //Desktop Dev
    public static final int WIDTH = 1920, HEIGHT = 1080, FPS = 60; //Desktop Native


    //Set up GLFW Window
    private static boolean fullscreen = false;
    public static Window window = new Window(WIDTH, HEIGHT, FPS, "Buddler Joe");

    //Game instance and player settings
    private static ClientLogic socketClient;

    private static boolean doConnectToServer = false; //Multiplayer: true, Singleplayer: false
    private static boolean connectedToServer = false;
    public static String username = RandomName.getRandomName(); //TODO (Server Team): Username maybe needs its own class or should at least be moved to NetPlayer

    public static Chat chat; //This probably needs to go somewhere else when we work on the chat

    /*
     * TODO (Matthias): Change camera to non-static.
     * We want everything set up so we could use multiple camaras, even if we don't end up needing them.
     * Everything that relies on a camera object should know which camera it is using
     */
    public static Camera camera;


    /*
     * A Temporary, crude "skin-selector". We will work on this when we do GUI Stuff.
     */
    private int skin = 2;
    public static  String myModel;
    public static String myTexture;
    public static float myModelSize;

    /*
     * Keep track of connected players.
     * TODO (Client Network Team): This needs to be moved to a different class that handles connected players.
     */
    private List<NetPlayer> netPlayers = new ArrayList<>();
    private List<NetPlayer> loadedNetPlayers = new ArrayList<>();

    /*
     * All entities that need to be rendered. I can see this moving to a "EntityMaster" class, but is absolutely fine for now.
     * We keep all the Sub-Entities organized in Masters and keep this as a global Game-Static list with minimal maintenance.
     */
    private static List<Entity> entities = new ArrayList<>();

    /**
     * Initialize the Game Thread (Treat this like a constructor)
     */
    @Override
    public synchronized void start() {

        //select model
        switch (skin) {
            case 1:
                //Penguin
                myModel = "penguin";
                myTexture = "penguin";
                myModelSize = 2.5f;
                break;
            case 2:
                //Person
                myModel = "person";
                myTexture = "person";
                myModelSize = .4f;
                break;
            case 3:
                //Rabbit
                myModel = "bunny";
                myTexture = "bunny";
                myModelSize = .15f;
        }

        /*
         * Start client Logic and send a login packet
         * TODO (Server Network Team): Move this out of Game into a class in the network package and change it.
         */
        if(doConnectToServer) {
//            socketClient = new ClientLogic(this, "localhost");
//            socketClient.start();
//
//            while (!socketClient.isRunning()) {
//                Packet00Login loginPacket = new Packet00Login(username, myModel, myModel, myModelSize);
//                loginPacket.writeData(socketClient);
//                connectedToServer = true; //Not really, but we implement this later maybe.
//                                          //Just creating the variable for later use.
//            }
        }


        //Start the thread
        super.start();
    }

    /**
     * Here we initialize all the Masters and other classes and generate the world.
     */
    @Override
    public void run() {
        //Create GLFW Window, we run this in a thread.
        window.setSize(WIDTH, HEIGHT);
        window.setFullscreen(fullscreen);
        window.create();

        //Used to load 3D models (.obj) and convert them to coordinates for the shaders, also initializes the Bounding Boxes
        Loader loader = new Loader();

        //Initialize World. We can do this a little better once we have a proper algorithm to generate the world
        GenerateWorld.generateTerrain(loader);
        GenerateWorld.generateBlocks(loader);
        Terrain aboveGround = GenerateWorld.getAboveGround();
        TerrainFlat belowGround = GenerateWorld.getBelowGround();
        if(aboveGround == null || belowGround == null) {
            System.err.println("Could not generate terrain.");
        }

        //Initialize items
        ItemMaster.init(loader);

        //Initialize debris
        DebrisMaster.init();

        //Initiate the master renderer class
        MasterRenderer renderer = new MasterRenderer();

        //Generate the player. TODO (later): Move this in some player related class when more work on network is done
        RawModel rawPlayer = loader.loadToVAO(OBJFileLoader.loadOBJ(myModel));
        TexturedModel playerModel = new TexturedModel(rawPlayer, new ModelTexture(loader.loadTexture(myTexture)));
        Player player = new Player(playerModel, new Vector3f(90, 2, 3), 0, 0, 0, myModelSize);

        //GUI / HUD
        //TODO (Matthias): We will need a GuiMaster class to initialize and manage the GUI elements
        TextMaster.init(loader);
        GUIString.loadFont(loader);
        FPS fpsCounter = new FPS();
        List<GuiTexture> guis = new ArrayList<>();
        chat = new Chat(loader);
        guis.add(chat.getChatGui());
        GuiRenderer guiRenderer = new GuiRenderer(loader);

        //Load Particle Master
        ParticleMaster.init(loader, MasterRenderer.getProjectionMatrix());


        //Lights and cameras (just one for now)
        LightMaster.generateLight(
                LightMaster.LightTypes.SUN,
                new Vector3f(0, 600, 200),
                new Vector3f(.3f, .3f, .3f));
        camera = new Camera(player, window);

//        ItemMaster.generateItem(ItemMaster.ItemTypes.TORCH, new Vector3f(100, 10, 5));



        /*
        **************************************************************
---------HERE STARTS THE GAME LOOP!
        **************************************************************
         */
        while (!window.isClosed()) {
            if (window.isOneSecond()) {
                //This runs once per second, we can use it for stuff that needs less frequent updates
                fpsCounter.updateString(""+window.getCurrentFPS());
            }

            //...

            //This runs super often, we shouldn't use it

            //...

            if (window.isUpdating()) {
                /* This runs once per frame. Do all the updating here.
                   The order of things is quite relevant here
                   Optimally this should be mostly Masters here
                 */

                //ESC = Exit... we will add a menu later
                if(InputHandler.isKeyPressed(GLFW_KEY_ESCAPE))
                    window.stop();

                /*Check if a new player joined and add the player to the local game
                  This needs to go to some net package*/
                checkAndLoadNetPlayers(loader);

                /*InputHandler needs to be BEFORE polling (window.update()) so we still have access to the events of last Frame.
                  Everythine else should be after polling.*/
                InputHandler.update();
                window.update();

                //Update positions of camera, player and 3D Mouse Pointer
                camera.move();
                player.move();
                MousePlacer.update(camera);

                //Masters check their slaves
                ItemMaster.update();
                BlockMaster.update();
                DebrisMaster.update();
                ParticleMaster.update(camera);
                LightMaster.update(camera, player);

                //Prepare and render the entities
                renderer.processEntity(player);
                renderer.processTerrain(aboveGround);
                renderer.processTerrain(belowGround);
                for (Entity entity : entities) {
                    if (entity != null) {
                        //All the NetPlayer stuff will need to move to a different class and update it from there
                        if(entity instanceof NetPlayer) {
                            ((NetPlayer) entity).getDirectionalUsername().updateString();
                        }
                        renderer.processEntity(entity);
                    }
                }

                //Render other stuff, order is important
                renderer.render(LightMaster.getLightsToRender(), camera);
                chat.checkInputs();
                //GUI goes over everything else and then text on top of GUI
                ParticleMaster.renderParticles(camera);
//                guiRenderer.render(guis);
                TextMaster.render();

                //Done with one frame
                window.swapBuffers();
            }
        }

        /*
        **************************************************************
---------HERE ENDS THE GAME LOOP!
        **************************************************************
         */

        //Clean up memory and unbind openGL shader programs
        TextMaster.cleanUp();
        guiRenderer.cleanUp();
        renderer.cleanUp();
        loader.cleanUp();
        ParticleMaster.cleanUp();

        //Close and disconnect (still need a window close callback)
        window.kill();
        if(connectedToServer) {
            disconnectFromServer();
        }
        System.exit(1); //For now...

    }

    /**
     * Any entity added via this function will be passed to the Master Renderer and rendered in the Game World.
     * @param entity any entity that can be rendered
     */
    public static void addEntity(Entity entity) {
        entities.add(entity);
    }

    /**
     * Stops an entity from being passed to the Master Renderer and being rendered in the Game World.
     * Can be used to effectively destroy an entity in the game.
     * @param entity entity that should no longer be rendered
     */
    public static void removeEntity(Entity entity) {
        entities.remove(entity);
    }

    /*
     * Here are some functions that have no other place yet, but they all need to get out of this file
     * Most of them will be moved to the net package when working on the protocoll
     */

    private void disconnectFromServer() {
//        packet.writeData(socketClient);
//        socketClient.interrupt(); //Does this even work?
    }

    public void addNetPlayer(NetPlayer netPlayer) {

        if(!netPlayer.getUsername().equals(username)) {
            boolean found = false;
            for (NetPlayer player : netPlayers) {
                if (player.equals(netPlayer)) {
                    found = true;
                }
            }
            if(!found) {
                System.out.println("Adding "+netPlayer.getUsername()+" as a "+netPlayer.getModelStr()+".");
                this.netPlayers.add(netPlayer);
            }
        }
    }

    public void removeNetPlayer(NetPlayer netPlayer) {
        this.netPlayers.remove(netPlayer);
    }
    public void removeNetPlayer(String username) {
        if (netPlayers.size() == 0)
            return;

        int index = 0;
        for (NetPlayer netPlayer : netPlayers) {
            if(netPlayer.getUsername().equals(username)){
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
                if(!found) {
                    enteringPlayer = netPlayer;
                }
            }
            if (!found) {
                System.out.println("" + enteringPlayer.getUsername() + " has joined the game.");
                if (enteringPlayer.getModel() == null) {
                    RawModel rawPlayer = loader.loadToVAO(OBJFileLoader.loadOBJ(enteringPlayer.getModelStr()));
                    TexturedModel defaultModel = new TexturedModel(rawPlayer, new ModelTexture(loader.loadTexture(enteringPlayer.getTextureStr())));
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
                if(!found) {
                    leavingPlayer = loadedNetPlayer;
                }
            }
            if (!found && leavingPlayer != null) {
                System.out.println("" + leavingPlayer.getUsername() + " has left the game.");
                loadedNetPlayers.remove(leavingPlayer);
                int index = 0;
                for (Entity entity : entities) {
                    if( entity instanceof  NetPlayer && entity.equals(leavingPlayer))
                        break;
                    index++;
                }
                entities.remove(index);

            }
        }
    }

    //Getters
    public List<NetPlayer> getNetPlayers() {
        return netPlayers;
    }

    public static boolean isConnectedToServer() {
        return connectedToServer;
    }

    public static String getUsername() {
        return username;
    }

    public static ClientLogic getSocketClient() {
        return socketClient;
    }

    public List<NetPlayer> getLoadedNetPlayers() {
        return loadedNetPlayers;
    }

    /**
     * Returns the active camera that determines which View Matrix is used in the shaders
     */
    public static Camera getActiveCamera() {
        return camera;
    }


}

