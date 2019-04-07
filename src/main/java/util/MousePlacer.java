package util;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;

import engine.io.InputHandler;
import entities.Camera;
import entities.Entity;
import entities.blocks.Block;
import entities.blocks.BlockMaster;
import entities.items.Dynamite;
import entities.items.Item;
import entities.items.ItemMaster;
import entities.items.Torch;
import game.Game;
import java.util.HashMap;
import java.util.Map;
import net.packets.items.PacketSpawnItem;
import org.joml.AABBf;
import org.joml.Intersectionf;
import org.joml.Rayf;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * Uses Ray Casting in {@link InputHandler} to place an entity with the mouse cursor. Checks if the
 * item collides with a block to make sure the item is placed in an empty space.
 */
public class MousePlacer {

  private static Entity entity;
  private static int mode;
  private static Block intersectionBlock;

  /**
   * Run every frame while placer Modes is on. Updates position of the entity to be placed and
   * detects the intent to place an item. If successful, it places the item and disables placer
   * Modes.
   *
   * @param camera active camera (origin of mouse ray)
   */
  public static void update(Camera camera) {

    if (!InputHandler.isPlacerMode()) {
      return;
    }

    // Placing the item
    if (mode == Modes.Z3OFFSET.getMode()) {
      entity.setPosition(getZ3Intersection(camera));
      // Place only if not colliding with a block
      if (InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && !doesCollide(2)) {
        InputHandler.setPlacerMode(false);

        // Place item
        onPlace();

        MousePlacer.entity = null;
      }
    } else if (mode == Modes.BLOCK.getMode()) {
      Map<Block, Vector3f> intersections = new HashMap<>();
      for (Block block : BlockMaster.getBlocks()) {
        AABBf aabb = block.getBbox().getAabbf();
        Rayf ray = new Rayf(camera.getPosition(), InputHandler.getMouseRay());
        Vector2f result = new Vector2f();
        boolean intersectRayAab = Intersectionf.intersectRayAab(ray, aabb, result);
        if (intersectRayAab) {
          Vector3f intersection =
              new Vector3f(InputHandler.getMouseRay())
                  .mul(result.x)
                  .add(camera.getPosition().x, camera.getPosition().y, camera.getPosition().z);
          intersections.put(block, new Vector3f(intersection.x, intersection.y, intersection.z));
        }
      }

      Vector3f intersection = null;
      float distanceSq = Float.POSITIVE_INFINITY;
      intersectionBlock = null;
      if (intersections.size() == 0) {
        /*When no block intersection was found, use normal wall intersection,
         but we place it closer to the wall.
        */
        Vector3f wallIntersection = getZ3Intersection(camera);
        entity.setPosition(
            new Vector3f(wallIntersection.x, wallIntersection.y, wallIntersection.z - 2));
      } else if (intersections.size() == 1) {
        // If exactly one intersection was found, we just use that
        for (Block block : intersections.keySet()) {
          intersection = intersections.get(block);
          intersectionBlock = block;
        }
      } else {
        // More than 1 candidate for intersection, find the closest to the camera
        for (Block block : intersections.keySet()) {
          Vector3f inters = intersections.get(block);
          if (intersection == null) {
            intersection = inters;
            intersectionBlock = block;
            distanceSq = inters.distanceSquared(camera.getPosition());
          } else if (inters.distanceSquared(camera.getPosition()) < distanceSq) {
            intersection = inters;
            intersectionBlock = block;
            distanceSq = inters.distanceSquared(camera.getPosition());
          }
        }
      }
      if (intersection != null && intersectionBlock != null) {
        // Ensure some distance from the intersecting block.

        // Add an offset as a percentage of the distance from the center of the block to the object
        float offset = .35f;

        /*Take the vector that goes from the center of the block to the entity
         scale that Vector by the offset factor, then add it to the intersection point.
         This "pushes the entity away" from the block.
        */
        Vector3f scaledDistance =
            new Vector3f(intersection)
                .sub(intersectionBlock.getBbox().getCenter())
                .normalize()
                .mul(offset);

        intersection.add(scaledDistance);

        entity.setPosition(intersection);
      }

      if (InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1) && !doesCollide(3)) {
        // Place item
        onPlace();

        InputHandler.setPlacerMode(false);
        MousePlacer.entity = null;
      }
    }
  }

  private static void onPlace() {
    if (entity instanceof Dynamite) {
      ((Dynamite) entity).setActive(true);
    } else if (entity instanceof Torch) {
      ((Torch) entity).setBlock(intersectionBlock);
    }

    if (entity instanceof Item) {
      ItemMaster.ItemTypes itemType = ((Item) entity).getType();
      if (itemType != null) {
        // Send packet
        if (Game.isConnectedToServer() && ((Item) entity).isOwned()) {
          new PacketSpawnItem(itemType, entity.getPosition()).sendToServer();
        }
      }
    }
  }

  private static Vector3f getZ3Intersection(Camera camera) {
    float distance =
        Intersectionf.intersectRayPlane(
            camera.getPosition(),
            InputHandler.getMouseRay(),
            new Vector3f(0, 0, 3),
            new Vector3f(0, 0, 1),
            1e-5f);
    // Calculate intersection of plane and ray
    return getPointOnRay(camera.getPosition(), InputHandler.getMouseRay(), distance);
  }

  /**
   * Check if the entity collides with a block. (other entities might be added here or in another
   * method in the future)
   *
   * <p>Returns true if the entity is not in an empty space
   */
  private static boolean doesCollide(int dim) {
    for (Block block : BlockMaster.getBlocks()) {
      if (block.collidesWith(entity, dim)) {
        return true;
      }
    }
    return false;
  }

  public static Entity getEntity() {
    return entity;
  }

  /**
   * This is how you activate the mouse placer. Pass en entity to this method.
   *
   * @param entity entity to place
   */
  public static void placeEntity(Entity entity) {
    // Set entity if valid and if the placer is currently not used
    if (entity == null || InputHandler.isPlacerMode()) {
      // Can't place nothing or placer is already active
      return;
    }
    MousePlacer.entity = entity;
    mode = entity.getPlacerMode();

    InputHandler.setPlacerMode(true);
  }

  /** Cancel mouse placing and destroy the object being placed. */
  public static void cancelPlacing() {
    InputHandler.setPlacerMode(false);
    entity.setDestroyed(true);
    MousePlacer.entity = null;
  }

  /**
   * Simple vector math to get point on a ray.
   *
   * @param origin starting point of the ray. Usually a camera position
   * @param ray direction vector
   * @param distance distance to travel on the vector
   * @return point at distance from origin in direction of the ray
   */
  private static Vector3f getPointOnRay(Vector3f origin, Vector3f ray, float distance) {
    Vector3f start = new Vector3f(origin.x, origin.y, origin.z);
    Vector3f scaledRay = new Vector3f(ray.x * distance, ray.y * distance, ray.z * distance);
    return start.add(scaledRay);
  }

  public static int getMode() {
    return mode;
  }

  public static void setMode(int mode) {
    MousePlacer.mode = mode;
  }

  public enum Modes {
    Z3OFFSET(0), // Place on an XY plane with Z=3 (centered on blocks with dim=3)
    BLOCK(1); // Full 3D placement around the wall terrain and the blocks

    private int mode;

    Modes(int mode) {
      this.mode = mode;
    }

    public int getMode() {
      return mode;
    }
  }
}
