package util;


import bin.Game;
import engine.io.InputHandler;
import entities.Camera;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import terrains.TerrainFlat;

/*******
 * WORK IN PROGRESS, THIS DOES NOT WORK YET!
 ******/
public class MousePicker {

    private static final int RECURSION_COUNT = 200;
    private static final float RAY_RANGE = 600;

    private Vector3f currentRay = new Vector3f();

    private Matrix4f projectionMatrix;
    private Matrix4f viewMatrix;
    private Camera camera;

    private TerrainFlat terrain;
    private Vector3f currentTerrainPoint;

    public MousePicker(Camera cam, Matrix4f projection, TerrainFlat terrain) {
        camera = cam;
        projectionMatrix = projection;
        viewMatrix = Maths.createViewMatrix();
        this.terrain = terrain;
    }

    public Vector3f getCurrentTerrainPoint() {
        return currentTerrainPoint;
    }

    public Vector3f getCurrentRay() {
        return currentRay;
    }

    public void update() {
        viewMatrix = Maths.createViewMatrix();
        currentRay = calculateMouseRay();
        if (intersectionInRange(0, RAY_RANGE, currentRay)) {
            currentTerrainPoint = binarySearch(0, 0, RAY_RANGE, currentRay);
        } else {
            currentTerrainPoint = null;
        }
    }

    private Vector3f calculateMouseRay() {
        float mouseX = (float) InputHandler.getMouseX();
        float mouseY = (float) InputHandler.getMouseY();

        Vector2f normalizedCoords = getNormalisedDeviceCoordinates(mouseX, mouseY);
        Vector4f clipCoords = new Vector4f(normalizedCoords.x, normalizedCoords.y, -1.0f, 1.0f);
        Vector4f eyeCoords = toEyeCoords(clipCoords);
        Vector3f worldRay = toWorldCoords(eyeCoords);
        return worldRay;
    }

    private Vector3f toWorldCoords(Vector4f eyeCoords) {
//        Matrix4f invertedView = new Matrix4f();
//        viewMatrix.invert(invertedView);
//
//        Vector4f rayWorld = new Vector4f();
//        invertedView.transform(eyeCoords, rayWorld);
//
//        Vector3f mouseRay = new Vector3f(rayWorld.x, rayWorld.y, rayWorld.z);
//        mouseRay.normalize();

        Vector4f rayWorld = new Matrix4f()
                .sub(new Matrix4f())
                .add(viewMatrix)
                .invert()
                .transform(eyeCoords);

        Vector3f mouseRay = new Vector3f(rayWorld.x, rayWorld.y, rayWorld.z)
                .normalize();

        return mouseRay;
    }

    private Vector4f toEyeCoords(Vector4f clipCoords) {

        Vector4f eyeCoords = new Matrix4f()
                .sub(new Matrix4f())
                .add(projectionMatrix)
                .invert()
                .transform(clipCoords);

        return new Vector4f(eyeCoords.x, eyeCoords.y, -1f, 0f);
    }

    private Vector2f getNormalisedDeviceCoordinates(float mouseX, float mouseY) {
        float x = (2.0f * mouseX) / Game.window.getWidth() - 1f;
        float y = (2.0f * mouseY) / Game.window.getHeight() - 1f;
        return new Vector2f(x, y);
    }

    //**********************************************************

    private Vector3f getPointOnRay(Vector3f ray, float distance) {
        Vector3f camPos = camera.getPosition();
        Vector3f start = new Vector3f(camPos.x, camPos.y, camPos.z);
        Vector3f scaledRay = new Vector3f(ray.x * distance, ray.y * distance, ray.z * distance);
        return start.add(scaledRay);
    }

    private Vector3f binarySearch(int count, float start, float finish, Vector3f ray) {
        float half = start + ((finish - start) / 2f);
        if (count >= RECURSION_COUNT) {
            Vector3f endPoint = getPointOnRay(ray, half);
            TerrainFlat terrain = getTerrain(endPoint.x, endPoint.x);
            if (terrain != null) {
                return endPoint;
            } else {
                return null;
            }
        }
        if (intersectionInRange(start, half, ray)) {
            return binarySearch(count + 1, start, half, ray);
        } else {
            return binarySearch(count + 1, half, finish, ray);
        }
    }

    private boolean intersectionInRange(float start, float finish, Vector3f ray) {
        Vector3f startPoint = getPointOnRay(ray, start);
        Vector3f endPoint = getPointOnRay(ray, finish);
        if (!isUnderGround(startPoint) && isUnderGround(endPoint)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isUnderGround(Vector3f testPoint) {
        TerrainFlat terrain = getTerrain(testPoint.x, testPoint.x);
        float height = 0;
        if (terrain != null) {
            height = terrain.getHeightOfTerrain(testPoint.x, testPoint.x);
        }
        if (testPoint.y < height) {
            return true;
        } else {
            return false;
        }
    }

    private TerrainFlat getTerrain(float worldX, float worldZ) {
        return terrain;
    }

}