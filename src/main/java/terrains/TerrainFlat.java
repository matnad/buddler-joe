package terrains;

import engine.models.RawModel;
import engine.render.Loader;
import engine.textures.TerrainTexture;
import engine.textures.TerrainTexturePack;
import game.map.GameMap;
import org.joml.Vector3f;

/** Flat Terrain with blend map. */
@SuppressWarnings("Duplicates")
public class TerrainFlat {

  static final float SIZE = GameMap.getDim() * GameMap.getTerrainChunk();
  private static final int VERTEX_COUNT = 1;//GameMap.getDim() * GameMap.getTerrainChunk();
  RawModel model;
  private float coordX;
  private float coordZ;
  private TerrainTexturePack texturePack;
  private TerrainTexture blendMap;

  private Vector3f rotation = new Vector3f(0f, 0f, 180f); // in degrees

  /**
   * Create a flat terrain tile.
   *
   * @param gridX starting point X world coordinate
   * @param gridZ starting point Z world coordinate
   * @param loader main loader
   * @param texturePack texture pack with all the textures required for the blend map
   * @param blendMap "heat map" image for how to blend images (load as Texture)
   */
  public TerrainFlat(
      int gridX,
      int gridZ,
      Loader loader,
      TerrainTexturePack texturePack,
      TerrainTexture blendMap) {

    this.texturePack = texturePack;
    this.blendMap = blendMap;
    this.coordX = gridX * SIZE;
    this.coordZ = gridZ * SIZE;
    this.model = generateTerrain(loader);
  }

  /**
   * Generate Flat Terrain.
   *
   * <p>Creates Vertices, Texture Coords, Normals and Indices for a Flat terrain and loads them into
   * a raw model Size and "resolution" can be set in the class vars, this is intended to be used as
   * "Tiles" of terrain
   *
   * @param loader main loader
   * @return Raw Model of Terrain
   */
  private RawModel generateTerrain(Loader loader) {
    int count = VERTEX_COUNT * VERTEX_COUNT;
    float[] vertices = new float[count * 3];
    float[] normals = new float[count * 3];
    float[] textureCoords = new float[count * 2];
    int[] indices = new int[6 * (VERTEX_COUNT - 1) * (VERTEX_COUNT - 1)];
    int vertexPointer = 0;
    for (int i = 0; i < VERTEX_COUNT; i++) {
      for (int j = 0; j < VERTEX_COUNT; j++) {
        vertices[vertexPointer * 3] = (float) j / ((float) VERTEX_COUNT - 1) * SIZE;
        vertices[vertexPointer * 3 + 1] = (float) i / ((float) VERTEX_COUNT - 1) * SIZE;
        vertices[vertexPointer * 3 + 2] = 0;

        normals[vertexPointer * 3] = 0;
        normals[vertexPointer * 3 + 1] = 0;
        normals[vertexPointer * 3 + 2] = -1;


        textureCoords[vertexPointer * 2] = (float) j / ((float) VERTEX_COUNT - 1);
        textureCoords[vertexPointer * 2 + 1] = (float) i / ((float) VERTEX_COUNT - 1);
        vertexPointer++;
      }
    }
    int pointer = 0;
    for (int gz = 0; gz < VERTEX_COUNT - 1; gz++) {
      for (int gx = 0; gx < VERTEX_COUNT - 1; gx++) {
        int topLeft = (gz * VERTEX_COUNT) + gx;
        int topRight = topLeft + 1;
        int bottomLeft = ((gz + 1) * VERTEX_COUNT) + gx;

        indices[pointer++] = topLeft;
        indices[pointer++] = bottomLeft;
        indices[pointer++] = topRight;
        indices[pointer++] = topRight;
        indices[pointer++] = bottomLeft;

        int bottomRight = bottomLeft + 1;
        indices[pointer++] = bottomRight;
      }
    }
    return loader.loadToVao(vertices, textureCoords, normals, indices);
  }

  public float getHeightOfTerrain(float worldX, float worldZ) {
    return -SIZE;
  }

  public float getHeightOfTerrain() {
    return getHeightOfTerrain(0, 0);
  }

  public float getCoordX() {
    return coordX;
  }

  public float getCoordZ() {
    return coordZ;
  }

  public RawModel getModel() {
    return model;
  }

  public TerrainTexturePack getTexturePack() {
    return texturePack;
  }

  public TerrainTexture getBlendMap() {
    return blendMap;
  }

  public Vector3f getRotation() {
    return rotation;
  }

  public void setRotation(Vector3f rotation) {
    this.rotation = rotation;
  }
}
