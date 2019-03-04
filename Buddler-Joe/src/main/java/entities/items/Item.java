package entities.items;

import engine.models.TexturedModel;
import entities.Entity;
import org.joml.Vector3f;

public abstract class Item extends Entity {

    private static TexturedModel preloadedModel;

    public Item(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        super(model, position, rotX, rotY, rotZ, scale);
        if (model == null) {
            System.out.println("WARNING! No model preloaded!");
        }
    }

    public static void setPreloadedModel(TexturedModel preloadedModel) {
        Item.preloadedModel = preloadedModel;
    }

    public static TexturedModel getPreloadedModel() {
        return preloadedModel;
    }
}
