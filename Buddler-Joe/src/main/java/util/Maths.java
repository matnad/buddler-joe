package util;


import game.Game;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * Math functions to create Transformation Matrices and the barryCentric
 */
public class Maths {

    /**
     * Matrix used to TRANSLATE (=move) an object for projection.
     * This gets us from local coordinates to world coordinates
     *
     * @param translation movement along the axis
     * @param rx Rotation around X axis
     * @param ry Rotation around Y axis
     * @param rz Rotation around Z axis
     * @param scale Scaling factor
     * @return A translation Matrix
     */
    public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry, float rz, float scale) {
        Matrix4f matrix = new Matrix4f();
        matrix.setTranslation(translation);
        matrix.rotateZYX((float) Math.toRadians(rx), (float) Math.toRadians(ry), (float) Math.toRadians(rz));
        matrix.scale(scale);
        return matrix;
    }


    /**
     * TODO: Make camera non static everywhere
     *
     * View Matrix.
     * Used to transform from World Coordinats to Camera Coordinates.
     * Used and calculated a lot!
     *
     * Intuituion: Shifts an object in the "opposite" directions of the camera.
     *
     * @return Camera View Matrix
     */
    public static Matrix4f createViewMatrix() {
        Matrix4f matrix = new Matrix4f();
        matrix.rotate((float) Math.toRadians(Game.camera.getPitch()), new Vector3f(1, 0, 0));
        matrix.rotate((float) Math.toRadians(Game.camera.getYaw()), new Vector3f(0, 1, 0));
//        matrix.rotate((float) Math.toRadians(camera.getRoll()), new Vector3f(0, 0, 1));
        Vector3f cameraPos = Game.camera.getPosition();
        Vector3f negativeCameraPos = new Vector3f(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        matrix.translate(negativeCameraPos);
        return matrix;
    }

    /**
     * Used to get the height in a triangle
     * Only used to generate terrain with a height map. To find the exact height of a point inside a vertex.
     * https://en.wikipedia.org/wiki/Barycentric_coordinate_system
     *
     * @return height (Y) in a specified triangle
     */
    public static float barryCentric(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos) {
        float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
        float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
        float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
        float l3 = 1.0f - l1 - l2;
        return l1 * p1.y + l2 * p2.y + l3 * p3.y;
    }

    /**
     * Used to place and scale GUI and HUD elements
     *
     * @param translation Position of the gui on screen
     * @param scale size of the gui
     * @return Matrix to transform the GUI
     */
    public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale) {
        return new Matrix4f()
            .translate(translation.x, translation.y, 0)
            .scale(new Vector3f(scale.x, scale.y, 1f));
    }

}
