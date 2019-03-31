package entities.light;

import entities.Camera;
import entities.Player;
import game.Game;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.joml.Vector3f;

/** Create and manage lights. Only ever create lights using this class */
public class LightMaster {

  /*The Maximum amount of lights that will be passed to the shader.
   This is limited to keep the amount of work for the shaders bounded.
   Depending on how it feels, we can increase or decrease the max lights, but it shouldn't be
   uncapped.
   CHANGING THIS VARIABLE IS NOT ENOUGH! You also need to change the shader code in
         -> entity.vs, entity.fs, terrain.fs and terrain.vs
  */
  private static final int maxLights = 8;

  // Organize Items in lists that can be accessed by their PRIORITY (ItemTypes.TYPE.getPriority())
  private static final Map<Integer, List<Light>> lightLists = new HashMap<>();

  // Keep a list with all active lights
  private static final List<Light> allLights = new ArrayList<>();

  // Keep a list with the closest maxLights to pass to the renderer
  private static List<Light> lightsToRender = new ArrayList<>();

  public static void init() {
    // Currently we don't need to initialize anything for lights. But every master has an init().
  }

  /**
   * ONLY USE THIS METHOD TO GENERATE LIGHTS.
   *
   * <p>Generates a light of the chosen type and adds it to all relevant lists. Keeps track of the
   * light and cleans it up when destroyed.
   *
   * @param type Light type
   * @param position Position in world coordinates
   * @param colour colour in R, G, B
   * @return The generated light object
   */
  public static Light generateLight(LightTypes type, Vector3f position, Vector3f colour) {
    Light light;
    switch (type) {
      case SUN:
        light = new Light(LightTypes.SUN, position, colour, new Vector3f(1, 0, 0), 180);
        break;
      case FLASH:
        light = new Light(LightTypes.FLASH, position, colour, new Vector3f(1, 0, 0), 180);
        break;
      case TORCH:
        light = new Light(LightTypes.TORCH, position, colour, new Vector3f(1, 0, 0), 180);
        break;
      case SPOT:
        light = new Light(LightTypes.SPOT, position, colour, new Vector3f(1, 0, 0), 60);
        break;
      default:
        light = null;
        break;
    }

    addToLightList(light);
    return light;
  }

  /**
   * Game loop update function. Called every frame to update lights. Will determine which lights are
   * rendered depending on their distance to the camera: The closest maxLights will be rendered.
   *
   * @param camera active camera
   * @param player active player
   */
  public static void update(Camera camera, Player player) {

    for (Light light : allLights) {
      light.update(camera);

      if (light.getType() == LightTypes.SUN) {
        // Adjust sun strength according to depth
        float pctBrightness = Game.getMap().getLightLevel(player.getPosition().y);
        light.setColour(new Vector3f(pctBrightness, pctBrightness, pctBrightness));
      }
    }

    // Go through all the lists in ascending priority until all lists have been processed
    int numberOfLists = lightLists.size();
    int processedLists = 0;
    int prioCounter = 0;
    lightsToRender = new ArrayList<>(); // Reset rendered Lights
    while (processedLists < numberOfLists) {
      List<Light> list = lightLists.get(prioCounter);
      if (list != null) {
        /*Sort the list so that lights closer to the camera get added to the render list
        first.
          We use insertion sort here, since the list will most likely be pre-sorted from
          last update.
          Insertion sort is efficient if the list is close to pre-sorted.
         */
        sortByDistance(list);
        for (int lightIndex = 0; lightIndex < list.size(); lightIndex++) {
          if (list.get(lightIndex).isDestroyed()) {
            allLights.remove(list.get(lightIndex));
            //noinspection SuspiciousListRemoveInLoop
            list.remove(lightIndex);
          } else if (lightsToRender.size() < maxLights
              && list.get(lightIndex).getAdjustedColour().length() > 0) {
            // If a light has a colour of 0 (all dark), then dont add it
            lightsToRender.add(list.get(lightIndex));
          }
        }
        processedLists++;
      }
      prioCounter++;
    }
  }

  private static void sortByDistance(List<Light> list) {
    for (int i = 1; i < list.size(); i++) {
      Light light = list.get(i);
      if (light.getDistanceSq() < list.get(i - 1).getDistanceSq()) {
        int attemptPos = i - 1;
        while (attemptPos != 0
            && list.get(attemptPos - 1).getDistanceSq() > light.getDistanceSq()) {
          attemptPos--;
        }
        //noinspection SuspiciousListRemoveInLoop
        list.remove(i);
        list.add(attemptPos, light);
      }
    }
  }

  private static void addToLightList(Light light) {
    // Get the list with the priority of the light, if the list is absent, create it
    List<Light> list =
        lightLists.computeIfAbsent(light.getType().getPriority(), k -> new ArrayList<>());

    // Add block to its priority-specific list
    list.add(light);
    // Add to type-unspecific list
    allLights.add(light);

    // Adding to render-list will be done in update
  }

  public static int getMaxLights() {
    return maxLights;
  }

  public static List<Light> getLightsToRender() {
    return lightsToRender;
  }

  public enum LightTypes {
    SUN(0, new Vector3f(1, 0, 0)),
    FLASH(1, new Vector3f(1, .001f, .0005f)),
    TORCH(3, new Vector3f(1, .02f, .01f)),
    SPOT(2, new Vector3f(1, .02f, .01f));

    private final int priority;
    private final Vector3f baseAttenuation;

    LightTypes(int priority, Vector3f baseAttenuation) {
      this.priority = priority;
      this.baseAttenuation = baseAttenuation;
    }

    int getPriority() {
      return priority;
    }

    public Vector3f getBaseAttenuation() {
      return baseAttenuation;
    }
  }
}
