package entities.blocks;

import engine.models.RawModel;
import engine.models.TexturedModel;
import engine.render.Loader;
import engine.render.fontRendering.TextMaster;
import engine.render.objConverter.OBJFileLoader;
import engine.textures.ModelTexture;
import entities.Entity;
import org.joml.Vector2f;
import org.joml.Vector3f;

public abstract class Block extends Entity {


    private float hardness;
    private float damage;
    private float dim;

    private static TexturedModel blockModel;


    public Block(int index, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        super(blockModel, index, position, rotX, rotY, rotZ, scale);

        if (blockModel == null) {
            System.out.println("WARNING! Load the block models first!");
        }

        setDestroyed(false);
        this.damage = 0;

        /*Dim is the distance from the center to a surface.
        For our baisc block, the dimension is equal to the scale, however this might be different for other blocks.
        This allows us to override it from sub-blocks.
        */
        this.dim = scale;
    }


    public static void loadBlockModels(Loader loader) {
        RawModel rawBlock = loader.loadToVAO(OBJFileLoader.loadOBJ("dirt"));
        ModelTexture blockAtlas = new ModelTexture(loader.loadTexture("blockAtlas"));
        blockAtlas.setNumberOfRows(6);
        blockModel = new TexturedModel(rawBlock, blockAtlas);
    }

    //Functions to get the 2D (along the wall) or 3D distance from the center of the block to a point.
    public float getDistanceFrom(Vector3f pos) {
        return super.getPosition().distance(pos);
    }

    public float get2DDistanceFrom(Vector2f pos) {
        return new Vector2f(super.getPosition().x, super.getPosition().y).distance(pos);
    }

    public float getHardness() {
        return hardness;
    }

    public void setHardness(float hardness) {
        this.hardness = hardness;
    }

    public float getDamage() {
        return damage;
    }

    public void increaseDamage(float damage) {
        this.damage += damage;
        if (this.damage > this.hardness) {
            setDestroyed(true);
            //Send server packet
        }
    }


    public float getDim() {
        return dim;
    }

    public void setDim(float dim) {
        this.dim = dim;
    }

    public void setDestroyed(boolean destroyed) {
        super.setDestroyed(destroyed);
        if(destroyed) {
            onDestroy();
        }
    }

    protected abstract void onDestroy();
}
