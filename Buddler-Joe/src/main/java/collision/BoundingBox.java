package collision;


import org.joml.Vector3f;

public class BoundingBox {

    private float minX, maxX, minY, maxY, minZ, maxZ;
    private float minXO, maxXO, minYO, maxYO, minZO, maxZO;
    private float dimX, dimY, dimZ;
    private Vector3f position;

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
    
    //Internal collision stuff - all to check if two Bounding Boxes are Overlapping
    public boolean collidesWith(BoundingBox box){
        return collidesWith(box, 2);
    }

    public boolean collidesWith(BoundingBox box, int dim){
        if(dim >= 1 &&!collidesX(box))
            return false;
        if(dim >= 2 &&!collidesY(box))
            return false;

        // check for 3D collision if dim3 is true
        if(dim >= 3 && !collidesZ(box))
            return false;

        return true;
    }

    public boolean collidesX(BoundingBox box) {
        return !(box.minX > this.maxX) && !(this.minX > box.maxX);
    }
    public boolean collidesY(BoundingBox box) {
        return !(box.minY > this.maxY) && !(this.minY > box.maxY);
    }
    public boolean collidesZ(BoundingBox box) {
        return !(box.minZ > this.maxZ) && !(this.minZ > box.maxZ);
    }


    public float [] getCoords () {
        return new float[] {minX, maxX, minY, maxY, minZ, maxZ};
    }

    public void scale(float scale) {
        minXO *= scale;
        maxXO *= scale;
        minYO *= scale;
        maxYO *= scale;
        minZO *= scale;
        maxZO *= scale;
    }

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

    public Vector3f getPosition() {
        return position;
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
