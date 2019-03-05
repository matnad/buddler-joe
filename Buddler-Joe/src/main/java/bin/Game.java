package bin;

import engine.io.InputHandler;
import engine.io.Window;
import engine.models.RawModel;
import engine.models.TexturedModel;
import engine.particles.*;
import engine.render.*;
import engine.render.fontRendering.TextMaster;
import engine.render.objConverter.ModelData;
import engine.render.objConverter.OBJFileLoader;
import engine.textures.ModelTexture;
import engine.textures.TerrainTexture;
import engine.textures.TerrainTexturePack;
import entities.*;
import entities.blocks.*;
import entities.items.Dynamite;
import gui.Chat;
import gui.FPS;
import gui.GUIString;
import gui.GuiTexture;
import net.ClientLogic;
import net.packets.Packet00Login;
import net.packets.Packet99Disconnect;
import org.joml.Vector3f;
import terrains.Terrain;
import terrains.TerrainFlat;
import util.MousePlacer;
import util.RandomName;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.lwjgl.glfw.GLFW.*;

public class Game extends Thread {
//    public static final int WIDTH = 2560/2, HEIGHT = 1600/2, FPS = 60; //Mac Book Pro Half
//    public static final int WIDTH = 2560, HEIGHT = 1600, FPS = 60; //Mac Book Pro
//    public static final int WIDTH = 800, HEIGHT = 600, FPS = 60; //Desktop Dev
    public static final int WIDTH = 1920, HEIGHT = 1080, FPS = 60; //Desktop Native
    public static Window window = new Window(WIDTH, HEIGHT, FPS, "LWJGL Engine");

    private static boolean fullscreen = false;

    private static ClientLogic socketClient;

    private boolean running = false;
    private static boolean doConnectToServer = true; //Multiplayer: true, Singleplayer: false
    private static boolean connectedToServer = false;
    public static String username = RandomName.getRandomName();

    public static Chat chat;
    public static Camera camera;

    //Temp Player model
    private int skin = 2;
    public static  String myModel;
    public static String myTexture;
    public static float myModelSize;

    //Containers
    private List<NetPlayer> netPlayers = new ArrayList<>();
    private List<NetPlayer> loadedNetPlayers = new ArrayList<>();

    private static List<Entity> entities = new ArrayList<>();
    private static List<Block> blocks = new ArrayList<>();
    private static List<Dynamite> dynamites = new ArrayList<>();

    @Override
    public synchronized void start() {

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
                myModelSize = .5f;
                break;
            case 3:
                //Rabbit
                myModel = "bunny";
                myTexture = "bunny";
                myModelSize = .15f;
        }

        running = true;

        if(doConnectToServer) {
            socketClient = new ClientLogic(this, "localhost");
            socketClient.start();

            while (!socketClient.isRunning()) {
                Packet00Login loginPacket = new Packet00Login(username, myModel, myModel, myModelSize);
                loginPacket.writeData(socketClient);
                connectedToServer = true; //Not really, but we implement this later maybe.
                                          //Just creating the variable for later use.
            }
        }



        super.start();
//        new Thread(this).start();
    }

    @Override
    public void run() {

        window.setSize(WIDTH, HEIGHT);
        window.setFullscreen(fullscreen);
        window.create();
        window.setBackgroundColor(.6f, .2f, .2f);

        //Used to lead 3D models and convert them to coordinates for the shaders
        Loader loader = new Loader();

        //Terrain Texture
        TerrainTexture grass = new TerrainTexture(loader.loadTexture("grass"));
        TerrainTexture mud = new TerrainTexture(loader.loadTexture("mud"));
        TerrainTexture grassFlowers = new TerrainTexture(loader.loadTexture("grassFlowers"));
        TerrainTexture path = new TerrainTexture(loader.loadTexture("path"));
        //Blend map defines how the textures get applies (each color = one texture with smooth transition)
        //Check out the picture in resources
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));

        //Terrain Generation
        TerrainTexturePack texturePack = new TerrainTexturePack(grass, mud, grassFlowers, path);
        Terrain terrain2 = new Terrain(0,-1,loader, texturePack, blendMap, "heightMap");

        texturePack = new TerrainTexturePack(mud, mud, grass, mud);
        TerrainFlat terrain = new TerrainFlat(0,0, loader, texturePack, blendMap);
        terrain.setRotation(new Vector3f(0,0,90));

        //Tree
        ModelData data = OBJFileLoader.loadOBJ("tree");
        RawModel rawTree = loader.loadToVAO(data);
        TexturedModel tree = new TexturedModel(rawTree, new ModelTexture(loader.loadTexture("tree")));

        //Tree2
        data = OBJFileLoader.loadOBJ("lowPolyTree");
        RawModel rawTree2 = loader.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());
        TexturedModel tree2 = new TexturedModel(rawTree2, new ModelTexture(loader.loadTexture("lowPolyTree")));

        //Fern
        data = OBJFileLoader.loadOBJ("fern");
        RawModel rawFern = loader.loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());
        ModelTexture fernAtlas = new ModelTexture(loader.loadTexture("fernAtlas"));
        fernAtlas.setNumberOfRows(2);
        TexturedModel fern = new TexturedModel(rawFern, fernAtlas);
        fern.getTexture().setHasTransparency(true);



        //Place some vegetation
        Random random = new Random(676453);
        for (int i = 0; i < 200; i++) {
            if (i % 3 == 0) {
                entities.add(new Entity(fern, random.nextInt(4), getNextRandomVector3f(terrain2, random), 0, 0, 0, .9f));
                entities.add(new Entity(tree2, getNextRandomVector3f(terrain2, random), 0, random.nextFloat() * 360, 0,
                        random.nextFloat() * 0.4f + .2f));
                entities.add(new Entity(tree, getNextRandomVector3f(terrain2, random), 0, 0, 0,
                        random.nextFloat() * 1 + 4));

            }
        }


//        //Blocks ->> TODO: make texture atlas
//        RawModel rawStone = loader.loadToVAO(OBJFileLoader.loadOBJ("cube"));
//        TexturedModel stone = new TexturedModel(rawStone, new ModelTexture(loader.loadTexture("rocks")));
//
//        RawModel rawBox = loader.loadToVAO(OBJFileLoader.loadOBJ("cube"));
//        TexturedModel box = new TexturedModel(rawBox, new ModelTexture(loader.loadTexture("box")));
//
//        RawModel rawDirt = loader.loadToVAO(OBJFileLoader.loadOBJ("cube"));
//        TexturedModel dirt = new TexturedModel(rawDirt, new ModelTexture(loader.loadTexture("mud")));
//        //Later we can calculate position and index of a block to optimize, for now we search by looping


        Block.loadBlockModels(loader);
        Dynamite.loadModel(loader);


        //Generate blocks
        float padding = .0f;
        float size = 3;
        float m = size*2+padding;

        for (int i = 0; i < 33; i++) {
            blocks.add(new GrassBlock(new Vector3f(i * m + 3f, -size, size),0, 0, 0, size));
        }

        for (int i = 0; i < 33; i++) {
            for (int j = 0; j < 33; j++) {
                float k = random.nextFloat();
                if (k < .5f) {
                    blocks.add(new DirtBlock(new Vector3f(i * m + 3f, -j * m - size*3, size),0, 0, 0, size));
                } else if (k < .8f) {
                    blocks.add(new StoneBlock(new Vector3f(i * m + 3f, -j * m - size*3, size), 0, 0, 0, size));
                } else if (k < .85f) {
                    blocks.add(new GoldBlock(new Vector3f(i * m + 3f, -j * m - size*3, size), 0, 0, 0, size));
                }
            }
        }
        entities.addAll(blocks);


        //Initiate the master renderer class
        MasterRenderer renderer = new MasterRenderer(window);

        //Generate the player, this will change
        RawModel rawPlayer = loader.loadToVAO(OBJFileLoader.loadOBJ(myModel));
        TexturedModel playerModel = new TexturedModel(rawPlayer, new ModelTexture(loader.loadTexture(myTexture)));
        Player player = new Player(playerModel, new Vector3f(97, 0, 3), 0, 0, 0, myModelSize);

        //Light, Cameras, GUI, etc initialization
        GUIString.loadFont(loader);

        Light light = new Light(new Vector3f(2e4f,4e4f,2e4f), new Vector3f(.5f,1, 1));
        camera = new Camera(player, window);
        FPS fpsCounter = new FPS();
        TextMaster.init(loader);
        chat = new Chat(loader);
        List<GuiTexture> guis = new ArrayList<>();
        guis.add(chat.getChatGui());
        GuiRenderer guiRenderer = new GuiRenderer(loader);

        //Particles
        ParticleMaster.init(loader, MasterRenderer.getProjectionMatrix());

        /********************************************************
         * HERE STARTS THE GAME LOOP!
         ********************************************************/
        while (!window.isClosed()) {
            if (window.isOneSecond()) {
                //This runs once per second
                fpsCounter.updateString(""+window.getCurrentFPS());
            }
            //This runs super often, don't think we need it

            //...

            if (window.isUpdating()) {
                //This runs once per frame. Do all the updating here.
                //The order of things is quite relevant here
                if(InputHandler.isKeyPressed(GLFW_KEY_ESCAPE))
                    window.stop();


                //Maybe rework with iterators?
                List<Entity> entitiesToRemove = new ArrayList<>();
                for (Entity entity : entities) {
                    if(entity.isDestroyed()) {
                        entitiesToRemove.add(entity);
                        if (entity instanceof Block) {
                            blocks.remove(entity);
                        } else if (entity instanceof Dynamite) {
                            dynamites.remove(entity);
                        }
                    }
                }
                entities.removeAll(entitiesToRemove);


                checkAndLoadNetPlayers(loader);
                window.update();

                //Can't do foreach since and explosion could spawn a dynamite (concurrent modification exception)
                for (int i = 0; i < dynamites.size(); i++) {
                    dynamites.get(i).move();
                }

                player.updateCloseBlocks(blocks);

                camera.move();
                player.move();
                MousePlacer.move();

//                system.generateParticles(new Vector3f(0,15,0).add(player.getPosition()));
                ParticleMaster.update(camera);

                //Prepare and render the entities
                renderer.processEntity(player);
                renderer.processTerrain(terrain);
                renderer.processTerrain(terrain2);
                for (Entity entity : entities) {
                    if (entity != null) {
                        if(entity instanceof NetPlayer) {
                            //entity.updateBoundingBox();
                            ((NetPlayer) entity).getDirectionalUsername().updateString();
                        }
                        renderer.processEntity(entity);
                    }
                }

                //Render the light and gui and text
                renderer.render(light, camera);
                chat.checkInputs();
                ParticleMaster.renderParticles(camera);
//                guiRenderer.render(guis);
                TextMaster.render();

                window.swapBuffers();
            }
        }
        /************************************
         * HERE ENDS THE GAME LOOP
         ***********************************/
        //Clean up memory
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

    /************************************************
     * Here are some functions that have no other place yet, but they all need to get out of this file
     * Most of them will be moved to the net package when working on the protocoll
     **********************************************/

    //For terrain generation
    private Vector3f getNextRandomVector3f(Terrain terrain, Random random) {
        float x = random.nextFloat() * 200;
        float z = random.nextFloat() * -200;
        float y = terrain.getHeightOfTerrain(x, z);
        return new Vector3f(x, y, z);
    }

    private void disconnectFromServer() {
        Packet99Disconnect packet = new Packet99Disconnect(username);
        packet.writeData(socketClient);
        socketClient.interrupt(); //Does this even work?
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
                    if( entity instanceof  NetPlayer && ((NetPlayer) entity).equals(leavingPlayer))
                        break;;
                    index++;
                }
                entities.remove(index);

            }
        }
    }

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

    public static void addEntity(Entity entity) {
        entities.add(entity);
        if (entity instanceof Dynamite) {
            dynamites.add((Dynamite) entity);
        }
    }

    public static void removeEntity(Entity entity) {
        entities.remove(entity);
        if (entity instanceof Dynamite) {
            dynamites.remove(entity);
        }
    }


    public static List<Block> getBlocks() {
        return blocks;
    }
}

