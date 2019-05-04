package util;

import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.junit.Assert;
import org.junit.Test;

public class TestMaths {

  @Test
  public void checkCreateTransformationMatrix1() {
    Maths maths = new Maths();
    Vector3f translation = new Vector3f(1, 1, 1);
    Vector3f scale = new Vector3f(1, 2, 3);
    Matrix4f matrix = Maths.createTransformationMatrix(translation, 5, 5, 5, scale);
    Assert.assertEquals(0.9924039f, matrix.m00(), 0.000001f);
  }

  @Test
  public void checkCreateTransformationMatrix2() {
    Maths maths = new Maths();
    Vector3f translation = new Vector3f(8, 2, 9);
    Vector3f scale = new Vector3f(6, 4, 2);
    Matrix4f matrix = Maths.createTransformationMatrix(translation, 5, 5, 5, scale);
    Assert.assertEquals(1.9848078f, matrix.m22(), 0.000001f);
  }

  @Test
  public void checkCreateTransformMatrixWithoutFloats1() {
    Maths maths = new Maths();
    Vector2f translation = new Vector2f(8, 2);
    Vector2f scale = new Vector2f(6, 4);
    Matrix4f matrix = Maths.createTransformationMatrix(translation, scale);
    Assert.assertEquals(6.0f, matrix.m00(), 0.0000001f);
  }

  @Test
  public void checkCreateTransformMatrixWithoutFloats2() {
    Maths maths = new Maths();
    Vector2f translation = new Vector2f(5, 12);
    Vector2f scale = new Vector2f(123, 98);
    Matrix4f matrix = Maths.createTransformationMatrix(translation, scale);
    Assert.assertEquals(98.0f, matrix.m11(), 0.0000001f);
  }

  @Test
  public void checkBarryCentric1() {
    Vector3f p1 = new Vector3f(1, 2, 3);
    Vector3f p2 = new Vector3f(3, 4, 5);
    Vector3f p3 = new Vector3f(8, 7, 6);
    Vector2f pos = new Vector2f(2, 2);
    Maths maths = new Maths();
    float f = Maths.barryCentric(p1, p2, p3, pos);
    Assert.assertEquals(2.0f, f, 0.0000001f);
  }

  @Test
  public void checkBarryCentric2() {
    Vector3f p1 = new Vector3f(5, 2, 3);
    Vector3f p2 = new Vector3f(3, 1, 34);
    Vector3f p3 = new Vector3f(8, 9, 123);
    Vector2f pos = new Vector2f(54, 98);
    Maths maths = new Maths();
    float f = Maths.barryCentric(p1, p2, p3, pos);
    Assert.assertEquals(54.726723f, f, 0.0000001f);
  }
}
