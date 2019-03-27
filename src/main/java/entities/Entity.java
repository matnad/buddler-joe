package entities;

import collision.BoundingBox;
import engine.models.TexturedModel;
import org.joml.Vector2f;
import org.joml.Vector3f;
import util.MousePlacer;

/**
 * All 3D entities are derived or spawned from this class. Defines position, bounding box, rotation
 * and scale of an object as well as the model, texture (with index if applicable) and if it is
 * destroyed.
 */
public class Entity {

  private TexturedModel model;
  private Vector3f position;
  private float rotX;
  private float rotY;
  private float rotZ;
  private Vector3f scale;
  private int textureIndex = 0;
  private int placerMode = MousePlacer.Modes.Z3OFFSET.getMode();

  private boolean destroyed;

  private BoundingBox bbox;

  /**
   * Default constructor if no texture atlas is used.
   *
   * @param model Textured Model
   * @param position World Coordinates
   * @param rotX X Rotation
   * @param rotY Y Rotation
   * @param rotZ Z Rotation
   * @param scale Scaling factor
   */
  public Entity(
      TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
    this(model, -1, position, rotX, rotY, rotZ, scale);
  }

  /**
   * Constructor if texture atlas is used.
   *
   * @param model Textured Model
   * @param index Texture index in texture atlas
   * @param position World Coordinates
   * @param rotX X Rotation
   * @param rotY Y Rotation
   * @param rotZ Z Rotation
   * @param scale Scaling factor
   */
  public Entity(
      TexturedModel model,
      int index,
      Vector3f position,
      float rotX,
      float rotY,
      float rotZ,
      float scale) {
    if (index > -1) {
      this.textureIndex = index;
    }

    this.model = model;

    this.position = position;
    this.rotX = rotX;
    this.rotY = rotY;
    this.rotZ = rotZ;
    this.scale = new Vector3f(scale, scale, scale);

    // Set bounding box which is stored in the raw model
    if (model != null && model.getRawModel().getBoundingCoords().length == 6) {
      bbox = new BoundingBox(model.getRawModel().getBoundingCoords());
      bbox.scale(getScale());
      updateBoundingBox();
    }
  }

  // Entity collision

  /**
   * Call this after you reposition the entity. If you use proper setters, you probably never have
   * to call it manually. (Still leaving it public, just in case)
   */
  private void updateBoundingBox() {
    if (bbox == null) {
      return;
    }
    bbox.moveTo(getPosition());
  }

  /**
   * Check if this entity collides with another entity in X, XY or XYZ dimension.
   *
   * @param entity The entity to check for collision with
   * @param dim number of dimensions 1=x, 2=xy, 3=xyz
   * @return true if the two entities' Bounding Boxes overlap in dim
   */
  public boolean collidesWith(Entity entity, int dim) {
    if (bbox == null || entity.getBbox() == null) {
      return false;
    }
    return bbox.collidesWith(entity.getBbox(), dim);
  }

  /**
   * Check if this entity collides with another entity in the XY dimension.
   *
   * @param entity The entity to check for collision with
   * @return true if the two entities' Bounding Boxes overlap in XY
   */
  public boolean collidesWith(Entity entity) {
    return collidesWith(entity, 2);
  }

  /**
   * Returns col in the texture atlas.
   *
   * @return col in the texture atlas.
   */
  public float getTextureXOffset() {
    int column = textureIndex % model.getTexture().getNumberOfRows();
    return (float) column / (float) model.getTexture().getNumberOfRows();
  }

  /**
   * Returns row in the texture atlas.
   *
   * @return row in the texture atlas.
   */
  public float getTextureYOffset() {
    int row = textureIndex / model.getTexture().getNumberOfRows();
    return (float) row / (float) model.getTexture().getNumberOfRows();
  }

  /**
   * Move this entity in world space. Updates Bounding Box.
   *
   * @param dx move this entity by dx units in the positive x direction
   * @param dy move this entity by dy units in the positive y direction
   * @param dz move this entity by dz units in the positive z direction
   */
  public void increasePosition(float dx, float dy, float dz) {
    this.position.x += dx;
    this.position.y += dy;
    this.position.z += dz;
    updateBoundingBox();
  }

  /**
   * Move this entity in world space. Updates Bounding Box.
   *
   * @param velocity velocity to move
   */
  public void increasePosition(Vector3f velocity) {
    position.add(velocity);
    updateBoundingBox();
  }

  /**
   * Rotate this entity in world space. Updates Bounding Box.
   *
   * @param dx Rotate this entity by dx degrees (not radians) along the X axis
   * @param dy Rotate this entity by dy degrees (not radians) along the Y axis
   * @param dz Rotate this entity by dz degrees (not radians) along the Z axis
   */
  public void increaseRotation(float dx, float dy, float dz) {
    this.rotX += dx;
    this.rotY += dy;
    this.rotZ += dz;
    updateBoundingBox();
  }

  /**
   * Rotate this entity in world space. Updates Bounding Box.
   *
   * @param spin Rotate this entity along all 3 axes
   */
  public void increaseRotation(Vector3f spin) {
    this.rotX += spin.x;
    this.rotY += spin.y;
    this.rotZ += spin.z;
    updateBoundingBox();
  }

  public TexturedModel getModel() {
    return model;
  }

  public Vector3f getPosition() {
    return position;
  }

  /**
   * Move this entity to a new point X,Y,Z in the world. Updates Bounding Box.
   *
   * @param position x,y,z world coordinates
   */
  public void setPosition(Vector3f position) {
    this.position = position;
    updateBoundingBox();
  }

  public Vector2f getPositionXy() {
    return new Vector2f(position.x, position.y);
  }

  public float getRotX() {
    return rotX;
  }

  /**
   * Rotate this entity around the X axis. Updates Bounding Box.
   *
   * @param rotX Degrees to rotate
   */
  public void setRotX(float rotX) {
    this.rotX = rotX;
    updateBoundingBox();
  }

  public float getRotY() {
    return rotY;
  }

  /**
   * Rotate this entity around the Y axis. Updates Bounding Box.
   *
   * @param rotY Degrees to rotate
   */
  public void setRotY(float rotY) {
    this.rotY = rotY;
    updateBoundingBox();
  }

  public float getRotZ() {
    return rotZ;
  }

  /**
   * Rotate this entity around the Z axis. Updates Bounding Box.
   *
   * @param rotZ Degrees to rotate
   */
  public void setRotZ(float rotZ) {
    this.rotZ = rotZ;
    updateBoundingBox();
  }

  public Vector3f getScale() {
    return scale;
  }

  /**
   * Scale this unit up (scale greater than 1) or down (scale less than 1) by a scaling factor.
   * Updates Bounding Boxes.
   *
   * @param scale Scaling factor
   */
  public void setScale(Vector3f scale) {
    this.scale = scale;
    // TODO: Update bounding box scale
  }

  public BoundingBox getBbox() {
    return bbox;
  }

  /**
   * Move this entity to a new point X coordinate in the world. Updates Bounding Box.
   *
   * @param x world coordinate
   */
  public void setPositionX(float x) {
    setPosition(new Vector3f(x, getPosition().y, getPosition().z));
    updateBoundingBox();
  }

  /**
   * Move this entity to a new point Y coordinate in the world. Updates Bounding Box.
   *
   * @param y world coordinate
   */
  public void setPositionY(float y) {
    setPosition(new Vector3f(getPosition().x, y, getPosition().z));
    updateBoundingBox();
  }

  /**
   * Move this entity to a new point Z coordinate in the world. Updates Bounding Box.
   *
   * @param z world coordinate
   */
  public void setPositionZ(float z) {
    setPosition(new Vector3f(getPosition().x, getPosition().y, z));
    updateBoundingBox();
  }

  public boolean isDestroyed() {
    return destroyed;
  }

  public void setDestroyed(boolean destroyed) {
    this.destroyed = destroyed;
  }

  public int getPlacerMode() {
    return placerMode;
  }

  public void setPlacerMode(int placerMode) {
    this.placerMode = placerMode;
  }
}
