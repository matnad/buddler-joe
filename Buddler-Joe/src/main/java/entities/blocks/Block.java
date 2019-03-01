package entities.blocks;

import engine.models.TexturedModel;
import entities.Entity;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Block extends Entity {

    private float dim;
    //private float diag;


    public Block(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        super(model, position, rotX, rotY, rotZ, scale);

        /*Dim is the distance from the center to a surface.
        For our baisc block, the dimension is equal to the scale, however this might be different for other blocks.
        This allows us to override it from sub-blocks.
        */
        this.dim = scale;
        //this.diag = (float) Math.sqrt(Math.pow(dim,2)*2);
    }

    //Functions to get the 2D (along the wall) or 3D distance from the center of the block to a point.
    public float getDistanceFrom(Vector3f pos) {
        return super.getPosition().distance(pos);
    }

    public float get2DDistanceFrom(Vector2f pos) {
        return new Vector2f(super.getPosition().x, super.getPosition().y).distance(pos);
    }

    //Getters
    public float getDim() {
        return dim;
    }
}
