package entities;

import collision.BoundingBox;
import engine.models.RawModel;
import engine.models.TexturedModel;
import engine.render.Loader;
import engine.render.objConverter.ModelData;
import engine.render.objConverter.OBJFileLoader;
import engine.textures.ModelTexture;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Entity {

    private TexturedModel model;
    private Vector3f position;
    private Vector3f positionBeforeMove;
    private float rotX, rotY, rotZ;
    private float scale;
    private int textureIndex = 0;

    private BoundingBox bBox;

    public Entity(TexturedModel model, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        //Default model if none is chosen
        this(model, -1, position, rotX, rotY, rotZ, scale);
    }

    public Entity(TexturedModel model, int index, Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        //Default model if none is chosen
        if (index > -1)
            this.textureIndex = index;

        this.model = model;
        this.position = position;
        this.rotX = rotX;
        this.rotY = rotY;
        this.rotZ = rotZ;
        this.scale = scale;

        if(model != null && model.getRawModel().getBB().length == 6) {
            bBox = new BoundingBox(model.getRawModel().getBB());
            bBox.scale(scale);
            updateBoundingBox();
        }

    }

    //Entity collision

    public void updateBoundingBox() {
        if(bBox == null)
            return;

        bBox.moveTo(getPosition());
    }

    public boolean collidesWith(Entity entity, int dim) {
        if(bBox == null || entity.getbBox() == null)
            return false;
        return bBox.collidesWith(entity.getbBox(), dim);
    }

    public boolean collidesWith(Entity entity) {
        return collidesWith(entity, 2);
    }

//    // -9999 means no collision
//    public float getCollisionX(Entity entity) {
//        if(bBox == null || entity.getbBox() == null)
//            return -9999; //
//
//        if (bBox.collidesX(entity.bBox)) {
//            if(bBox.getMaxX() > entity.bBox.getMaxX()) { //We are to the right
//                return entity.bBox.getMaxX();
//            } else {
//                return entity.bBox.getMinX();
//            }
//        }
//
//        return -9999;
//    }
//
//    public float getCollisionY(Entity entity) {
//        if(bBox == null || entity.getbBox() == null)
//            return -9999; //
//
//        if (bBox.collidesY(entity.bBox)) {
//            if(bBox.getMaxY() > entity.bBox.getMaxY()) { //We are above
//                return entity.bBox.getMaxY();
//            } else {
//                return entity.bBox.getMinY();
//            }
//        }
//
//        return -9999;
//    }

    public float getTextureXOffset(){
        int column = textureIndex%model.getTexture().getNumberOfRows();
        return (float)column/(float)model.getTexture().getNumberOfRows();
    }

    public float getTextureYOffset(){
        int row = textureIndex/model.getTexture().getNumberOfRows();
        return (float)row/(float)model.getTexture().getNumberOfRows();
    }

    public void increasePosition(float dx, float dy, float dz) {
        this.position.x += dx;
        this.position.y += dy;
        this.position.z += dz;
    }

    public void increaseRotation(float dx, float dy, float dz) {
        this.rotX += dx;
        this.rotY += dy;
        this.rotZ += dz;
    }

    public TexturedModel getModel() {
        return model;
    }

    public Vector3f getPosition() {
        return position;
    }

    public Vector2f getPositionXY() {
        return new Vector2f(position.x, position.y);
    }

    public float getRotX() {
        return rotX;
    }

    public float getRotY() {
        return rotY;
    }

    public float getRotZ() {
        return rotZ;
    }

    public float getScale() {
        return scale;
    }

    public void setModel(TexturedModel model) {
        this.model = model;
        bBox = new BoundingBox(model.getRawModel().getBB());
    }

    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void setRotX(float rotX) {
        this.rotX = rotX;
    }

    public void setRotY(float rotY) {
        this.rotY = rotY;
    }

    public void setRotZ(float rotZ) {
        this.rotZ = rotZ;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public BoundingBox getbBox() {
        return bBox;
    }

    public Vector3f getPositionBeforeMove() {
        return positionBeforeMove;
    }

    public void setPositionBeforeMove(Vector3f positionBeforeMove) {
        this.positionBeforeMove = positionBeforeMove;
    }

    public void setPositionX(float x) {
        setPosition(new Vector3f(x, getPosition().y, getPosition().z));
    }
    public void setPositionY(float y) {
        setPosition(new Vector3f(getPosition().x, y, getPosition().z));
    }
    public void setPositionZ(float z) {
        setPosition(new Vector3f(getPosition().x, getPosition().y, z));
    }
}
