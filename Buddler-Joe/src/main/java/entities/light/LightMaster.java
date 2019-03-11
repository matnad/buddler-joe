package entities.light;


import entities.Camera;
import entities.Player;
import org.joml.Vector3f;

import java.util.*;

public class LightMaster {

    /*The Maximum amount of lights that will be passed to the shader.
      This is limited to keep the amount of work for the shaders bounded.
      Depending on how it feels, we can increase or decrease the max lights, but it shouldn't be uncapped.
      CHANGING THIS VARIABLE IS NOT ENOUGH! You also need to change the shader code in
            -> entity.vs, entity.fs, terrain.fs and terrain.vs
     */
    private static final int MAX_LIGHTS = 8;

    //Organize Items in lists that can be accessed by their PRIORITY (ItemTypes.TYPE.getPriority())
    private static Map<Integer, List<Light>> lightLists = new HashMap<>();

    //Keep a list with all active lights
    private static List<Light> allLights = new ArrayList<>();

    //Keep a list with the closest MAX_LIGHTS to pass to the renderer
    private static List<Light> lightsToRender = new ArrayList<>();


    public enum LightTypes {
        SUN(0, new Vector3f(1, 0,0)),
        FLASH(1, new Vector3f(1, .001f, .0005f)),
        TORCH(2, new Vector3f(1, .01f, .002f));

        private int priority;
        private Vector3f baseAttenuation;

        LightTypes(int priority, Vector3f baseAttenuation) {
            this.priority = priority;
            this.baseAttenuation = baseAttenuation;
        }

        public int getPriority() {
            return priority;
        }

        public Vector3f getBaseAttenuation() {
            return baseAttenuation;
        }
    }

    public static void init() {
        //Currently we don't need to initialize anything for lights. But every master has an init().
    }

    public static Light generateLight(LightTypes type, Vector3f position, Vector3f colour) {
        Light light;
        switch (type) {
            case SUN:
                light = new Light(LightTypes.SUN, position, colour);
                break;
            case FLASH:
                light = new Light(LightTypes.FLASH, position, colour);
                break;
            case TORCH:
                light = new Light(LightTypes.TORCH, position, colour);
                break;
            default:
                light = null;
                break;
        }

        if(light != null) {
            addToLightList(light);
        }
        return light;
    }

    public static void update(Camera camera, Player player) {

        for (Light light : allLights) {
            light.update(camera);

            if(light.getType() == LightTypes.SUN) {
                //Adjust sun strength according to depth Depth 200 = Darkness
                float col = Math.max(0,200+player.getPositionXY().y)/200;
                System.out.println(col);
                light.setColour(new Vector3f(col, col, col));
            }

        }

        //Go through all the lists in ascending priority until all lists have been processed
        int numberOfLists = lightLists.size();
        int processedLists = 0, prioCounter = 0;
        lightsToRender = new ArrayList<>(); //Reset rendered Lights
        while ( processedLists < numberOfLists) {
            List<Light> list = lightLists.get(prioCounter);
            if (list != null) {
                /*Sort the list so that lights closer to the camera get added to the render list first.
                  We use insertion sort here, since the list will most likely be pre-sorted from last update.
                  Insertion sort is efficient if the list is close to pre-sorted.
                 */
                sortByDistance(list);
                for (int lightIndex = 0; lightIndex < list.size(); lightIndex++) {
                    if (list.get(lightIndex).isDestroyed()){
                        allLights.remove(list.get(lightIndex));
                        list.remove(lightIndex);
                    } else if(lightsToRender.size() < MAX_LIGHTS && list.get(lightIndex).getColour().length() > 0) {
                        //If a light has a colour of 0 (all dark), then dont add it
                        System.out.println(list.get(lightIndex).getType().getPriority());
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
            if(light.getDistanceSq() < list.get(i-1).getDistanceSq()) {
                int attemptPos = i-1;
                while (attemptPos != 0 && list.get(attemptPos - 1).getDistanceSq() > light.getDistanceSq()) {
                    attemptPos--;
                }
                list.remove(i);
                list.add(attemptPos, light);
            }
        }
    }

    private static void addToLightList(Light light) {
        //Get the list with the priority of the light, if the list is absent, create it
        List<Light> list = lightLists.computeIfAbsent(light.getType().getPriority(), k -> new ArrayList<>());

        //Add block to its priority-specific list
        list.add(light);
        //Add to type-unspecific list
        allLights.add(light);

        //Adding to render-list will be done in update
    }


    public static int getMAX_LIGHTS() {
        return MAX_LIGHTS;
    }

    public static List<Light> getLightsToRender() {
        return lightsToRender;
    }
}
