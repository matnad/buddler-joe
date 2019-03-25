package entities.blocks;

import engine.models.RawModel;
import engine.models.TexturedModel;
import engine.render.Loader;
import engine.render.objconverter.ObjFileLoader;
import engine.textures.ModelTexture;
import entities.Entity;
import entities.blocks.debris.DebrisMaster;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * Abstract class for Blocks
 *
 * - Loads the texture Atlas
 * - Handles damage to blocks
 * - Provides getters and setters for mandatory properties
 *
 */
public abstract class Block extends Entity {


    private float hardness;
    private float mass;
    private float damage;
    private float dim;
    private BlockMaster.BlockTypes type;
    private Entity destroyedBy;
    private static TexturedModel blockModel;

    /**
     * Abstract Constructor
     *
     * @param type type of block, described by Block Master enum
     * @param hardness damage before block is destroyed
     * @param position 3D coordinate of block center
     * @param rotX rotation around X axis
     * @param rotY rotation around Y axis
     * @param rotZ rotation around Z axis
     * @param scale scaling multiplier
     */
    public Block(BlockMaster.BlockTypes type, float hardness, float mass, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        //blockModel is a texture atlas, containing all block textures, ID is the position of the texture on the atlas
        super(blockModel, type.getTextureId(), position, rotX, rotY, rotZ, scale);

        this.type = type;
        this.hardness = hardness;
        this.mass = mass;

        if (blockModel == null) {
            //Maybe need more than a warning
            //This is loaded in the BlockMaster.init() function
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


    /**
     * Preload texture atlas. Called by the Block Master's init()
     *
     * @param loader main loader
     */
    static void loadBlockModels(Loader loader) {
        RawModel rawBlock = loader.loadToVao(ObjFileLoader.loadObj("dirt"));
        ModelTexture blockAtlas = new ModelTexture(loader.loadTexture("blockAtlas"));
        blockAtlas.setNumberOfRows(6);
        blockModel = new TexturedModel(rawBlock, blockAtlas);
    }


    /**
     * 3D distance between block and a 3D point
     *
     * @param pos distance from block to this point
     * @return distance in units
     */
    public float getDistanceFrom(Vector3f pos) {
        return super.getPosition().distance(pos);
    }

    /**
     * Squared 3D distance between block and a 3D point.
     * Squared is faster
     *
     * @param pos distance from block to this point
     * @return distance in units
     */
    public float getDistanceSquaredFrom(Vector3f pos) {
        return super.getPosition().distanceSquared(pos);
    }

    /**
     * Assuming the objects are on the same Z-plane, this gets the 2D distance between the objects
     *
     * @param pos X and Y coordinates for a position in the world
     * @return distance in units
     */
    public float get2dDistanceFrom(Vector2f pos) {
        return new Vector2f(super.getPosition().x, super.getPosition().y).distance(pos);
    }

    /**
     * Assuming the objects are on the same Z-plane, this gets the squared 2D distance between the objects
     * Squared is faster!
     *
     * @param pos X and Y coordinates for a position in the world
     * @return distance in units
     */
    public float get2dDistanceSquaredFrom(Vector2f pos) {
        return new Vector2f(super.getPosition().x, super.getPosition().y).distanceSquared(pos);
    }

    /**
     * @param damage damage done to the block
     * @param entity entity that inflicted the damage
     */
    public void increaseDamage(float damage, Entity entity) {
        this.damage += damage;
        if (this.damage > this.hardness) {
            setDestroyedBy(entity);
            setDestroyed(true); //Destroy block
        }
    }

    private void setDestroyedBy(Entity destroyedBy) {
        this.destroyedBy = destroyedBy;
    }

    Entity getDestroyedBy() {
        return destroyedBy;
    }

    /**
     * Only change destroyed status via this method, never call the super directly.
     * This will trigger the abstract method onDestroy() for the block.
     * -> The block type will determine what happens
     *
     * @param destroyed true if you want to destroy the block
     */
    public void setDestroyed(boolean destroyed) {
        super.setDestroyed(destroyed);
        if(destroyed) {
            DebrisMaster.generateDebris(this);
            onDestroy();
        }
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

    public float getDim() {
        return dim;
    }

    public void setDim(float dim) {
        this.dim = dim;
    }


    /**
     * Specifies what happens when this block is destroyed.
     * Must be implemented by the block child classes.
     */
    protected abstract void onDestroy();


    public BlockMaster.BlockTypes getType() {
        return type;
    }

    public static TexturedModel getBlockModel() {
        return blockModel;
    }

    public float getMass() {
        return mass;
    }
}
