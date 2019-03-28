package game;

import static entities.blocks.BlockMaster.BlockTypes.DIRT;
import static entities.blocks.BlockMaster.BlockTypes.GOLD;
import static entities.blocks.BlockMaster.BlockTypes.GRASS;
import static entities.blocks.BlockMaster.BlockTypes.STONE;

import engine.models.RawModel;
import engine.models.TexturedModel;
import engine.render.Loader;
import engine.render.objconverter.ModelData;
import engine.render.objconverter.ObjFileLoader;
import engine.textures.ModelTexture;
import engine.textures.TerrainTexture;
import engine.textures.TerrainTexturePack;
import entities.Entity;
import entities.blocks.BlockMaster;
import java.util.Random;
import org.joml.Vector3f;
import terrains.Terrain;
import terrains.TerrainFlat;

class GenerateWorld {

  // Static random seed for world generation
  private static final Random random = new Random(676453);

  private static Terrain aboveGround;
  private static TerrainFlat belowGround;

  // We can write something more pretty here once we have world generation down. For now this is
  // very flexible.

  /**
   * Generate all the Terrains and save them in static variables that can be accessed by the Game
   * Generates different types of terrain and has no return value.
   *
   * @param loader Pass the main loader from the Game class. There is no reason to have more than
   *     one loader.
   */
  static void generateTerrain(Loader loader) {
    // Terrain Texture
    TerrainTexture grass = new TerrainTexture(loader.loadTexture("grass"));
    TerrainTexture mud = new TerrainTexture(loader.loadTexture("mud"));
    TerrainTexture grassFlowers = new TerrainTexture(loader.loadTexture("grassFlowers"));
    TerrainTexture path = new TerrainTexture(loader.loadTexture("path"));
    // Blend map defines how the textures get applies (each color = one texture with smooth
    // transition)
    // Check out the picture in resources
    TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("blendMap"));

    // Terrain Generation
    TerrainTexturePack texturePack = new TerrainTexturePack(grass, mud, grassFlowers, path);
    aboveGround = new Terrain(0, -1, loader, texturePack, blendMap, "heightMap");

    texturePack = new TerrainTexturePack(mud, mud, grass, mud);
    belowGround = new TerrainFlat(0, 0, loader, texturePack, blendMap);
    belowGround.setRotation(new Vector3f(0, 0, 90));

    // Tree
    ModelData data = ObjFileLoader.loadObj("tree");
    RawModel rawTree = loader.loadToVao(data);
    TexturedModel tree = new TexturedModel(rawTree, new ModelTexture(loader.loadTexture("tree")));

    // Tree2
    data = ObjFileLoader.loadObj("lowPolyTree");
    RawModel rawTree2 =
        loader.loadToVao(
            data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());
    TexturedModel tree2 =
        new TexturedModel(rawTree2, new ModelTexture(loader.loadTexture("lowPolyTree")));

    // Fern
    data = ObjFileLoader.loadObj("fern");
    RawModel rawFern =
        loader.loadToVao(
            data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());
    ModelTexture fernAtlas = new ModelTexture(loader.loadTexture("fernAtlas"));
    fernAtlas.setNumberOfRows(2);
    TexturedModel fern = new TexturedModel(rawFern, fernAtlas);
    fern.getTexture().setHasTransparency(true);

    // Place some vegetation
    for (int i = 0; i < 200; i++) {
      if (i % 3 == 0) {
        Game.addEntity(
            new Entity(fern, random.nextInt(4), getNextRandomVector3f(aboveGround), 0, 0, 0, .9f));
        Game.addEntity(
            new Entity(
                tree2,
                getNextRandomVector3f(aboveGround),
                0,
                random.nextFloat() * 360,
                0,
                random.nextFloat() * 0.4f + .2f));
        Game.addEntity(
            new Entity(
                tree, getNextRandomVector3f(aboveGround), 0, 0, 0, random.nextFloat() * 1 + 4));
      }
    }
  }

  // We change seed management once we have World generation down.

  /**
   * Generate the diggable blocks on top of the terrain. Needs to be called only once at the start
   * of the game. Currently uses the seed of the GenerateWorld class.
   *
   * @param loader Pass the main loader from the Game class. There is no reason to have more than
   *     one loader.
   */
  static void generateBlocks(Loader loader) {
    // Generate some blocks
    float padding = .0f; // Distance between blocks
    float size = 3; // If this is not 3, you need to use the full block constructor and modify the
    // Block Master or the Block Files. Just use 3 for now.
    float dim = size * 2 + padding;

    // First row is grass
    for (int i = 0; i < 33; i++) {
      BlockMaster.generateBlock(GRASS, new Vector3f(i * dim + 3f, -size, size));
    }

    /*
     * Just slap some random blocks in there for now
     * BlockMaster will manage everything and pass them to the renderer
     */
    for (int i = 0; i < 33; i++) {
      for (int j = 0; j < 33; j++) {
        float k = random.nextFloat();
        Vector3f position = new Vector3f(i * dim + 3f, -j * dim - size * 3, size);
        if (k < .4f) {
          BlockMaster.generateBlock(DIRT, position);
        } else if (k < .8f) {
          BlockMaster.generateBlock(STONE, position);
        } else if (k < .85f) {
          BlockMaster.generateBlock(GOLD, position);
        }
      }
    }
  }

  /**
   * Get a random point on the terrain surface for a 200x200 standard terrain, respecting height
   * maps. Only use this for terrains with height map, otherwise simply randomize two coordinates on
   * a plane.
   *
   * @param terrain the Terrain you want to place the object on
   * @return an xyz position coordinate
   */
  private static Vector3f getNextRandomVector3f(Terrain terrain) {
    float x = GenerateWorld.random.nextFloat() * 200;
    float z = GenerateWorld.random.nextFloat() * -200;
    float y = terrain.getHeightOfTerrain(x, z);
    return new Vector3f(x, y, z);
  }

  static Terrain getAboveGround() {
    return aboveGround;
  }

  static TerrainFlat getBelowGround() {
    return belowGround;
  }
}
