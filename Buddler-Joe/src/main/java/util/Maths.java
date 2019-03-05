package util;


import bin.Game;
import entities.Camera;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class Maths {

    public static Matrix4f createTransformationMatrix(Vector3f translation, float rx, float ry, float rz, float scale) {
        Matrix4f matrix = new Matrix4f();
        matrix.setTranslation(translation);
        matrix.rotateZYX((float) Math.toRadians(rx), (float) Math.toRadians(ry), (float) Math.toRadians(rz));
        matrix.scale(scale);
        return matrix;
    }

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

    //Used to calculate yCoord (height) in a triangle
    public static float barryCentric(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos) {
        float det = (p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z);
        float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) / det;
        float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) / det;
        float l3 = 1.0f - l1 - l2;
        return l1 * p1.y + l2 * p2.y + l3 * p3.y;
    }

    //Gui transoformer
    public static Matrix4f createTransformationMatrix(Vector2f translation, Vector2f scale) {
        Matrix4f matrix = new Matrix4f();
        matrix.identity();
        matrix.translate(translation.x, translation.y, 0);
        matrix.scale(new Vector3f(scale.x, scale.y, 1f));
//        System.out.println(matrix);
        return matrix;
    }

}
