package collision;


import org.joml.Vector3f;

/**
 * A 3D Box around an object that determines the boundaries of the model.
 * has absolute and relative minimum and maximum for each of the three coordinates as well as quick access to dimension
 *
 * @author Joe's Buddler Corp.
 *
 */
public class BoundingBox {

    private float minX, maxX, minY, maxY, minZ, maxZ;
    private float minXO, maxXO, minYO, maxYO, minZO, maxZO;
    private float dimX, dimY, dimZ;

    /**
     * Generate the box around (0/0/0), then wait for the Entity to move it to their position.
     * @param BB a float array of exactly size 6 with the relative coordinates for all dimensions and 0/0/0 being the center
     */
    public BoundingBox(float[] BB) {
        if (BB.length < 6)
            return;
        minXO = BB[0];
        maxXO = BB[1];
        minYO = BB[2];
        maxYO = BB[3];
        minZO = BB[4];
        maxZO = BB[5];
    }

    /**
     * Move the Bounding Box to a new position. Usually called when the entity moves.
     * @param newPos this coordinate will be the center of the new bounding box.
     */
    public void moveTo(Vector3f newPos) {
        minX = newPos.x+minXO;
        maxX = newPos.x+maxXO;
        minY = newPos.y+minYO;
        maxY = newPos.y+maxYO;
        minZ = newPos.z+minZO;
        maxZ = newPos.z+maxZO;
        dimX = Math.abs(Math.abs(maxX) - Math.abs(minX));
        dimY = Math.abs(Math.abs(maxY) - Math.abs(minY));
        dimZ = Math.abs(Math.abs(maxZ) - Math.abs(minZ));
    }

    /**
     * Check if two bounding boxes overlap in the X and Y dimension.
     * @param box Bounding Box from a different entity
     * @return true if the boxes overlap
     */
    public boolean collidesWith(BoundingBox box){
        return collidesWith(box, 2);
    }

    /**
     * Check if two bounding boxes overlap in 1, 2 or 3 dimensions.
     * @param box Bounding Box from a different entity
     * @param dim 1 = X line, 2 = XY plane, 3 = XYZ space
     * @return true if the boxes overlap
     */
    public boolean collidesWith(BoundingBox box, int dim){
        if(dim >= 1 &&!collidesX(box))
            return false;
        if(dim >= 2 &&!collidesY(box))
            return false;
        if(dim >= 3 && !collidesZ(box))
            return false;

        return true;
    }

    /**
     * Check if two boxes overlap on the X-axis
     * @param box Bounding Box from a different entity
     * @return true if the boxes overlap
     */
    private boolean collidesX(BoundingBox box) {
        return !(box.minX > this.maxX) && !(this.minX > box.maxX);
    }

    /**
     * Check if two boxes overlap on the Y-axis
     * @param box Bounding Box from a different entity
     * @return true if the boxes overlap
     */
    private boolean collidesY(BoundingBox box) {
        return !(box.minY > this.maxY) && !(this.minY > box.maxY);
    }

    /**
     * Check if two boxes overlap on the Z-axis
     * @param box Bounding Box from a different entity
     * @return true if the boxes overlap
     */
    private boolean collidesZ(BoundingBox box) {
        return !(box.minZ > this.maxZ) && !(this.minZ > box.maxZ);
    }

    /**
     * Scale a box to match the new size of an entity. Usually called when an entity is scaled.
     * @param scale factor to scale with
     */
    public void scale(float scale) {
        minXO *= scale;
        maxXO *= scale;
        minYO *= scale;
        maxYO *= scale;
        minZO *= scale;
        maxZO *= scale;
    }

    //Getters

    public float getMinX() {
        return minX;
    }

    public float getMaxX() {
        return maxX;
    }

    public float getMinY() {
        return minY;
    }

    public float getMaxY() {
        return maxY;
    }

    public float getMinZ() {
        return minZ;
    }

    public float getMaxZ() {
        return maxZ;
    }

    public float getMinXO() {
        return minXO;
    }

    public float getMaxXO() {
        return maxXO;
    }

    public float getMinYO() {
        return minYO;
    }

    public float getMaxYO() {
        return maxYO;
    }

    public float getMinZO() {
        return minZO;
    }

    public float getMaxZO() {
        return maxZO;
    }


    public float getDimX() {
        return dimX;
    }

    public float getDimY() {
        return dimY;
    }

    public float getDimZ() {
        return dimZ;
    }
}
