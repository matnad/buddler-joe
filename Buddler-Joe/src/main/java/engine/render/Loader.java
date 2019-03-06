package engine.render;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import engine.models.RawModel;
import engine.render.objConverter.ModelData;
import engine.textures.Texture;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL30.*;

public class Loader {

    private List<Integer> vaos = new ArrayList<>();
    private List<Integer> vbos = new ArrayList<>();
    private List<Integer> textures = new ArrayList<>();

    public int loadToVAO(float[] positions, float[] textureCoords) {
        int vaoID = createVAO();
        storeDataInAttributeList(0, 2, positions);
        storeDataInAttributeList(1, 2, textureCoords);
        unbindVAO();
        return vaoID;
    }


    public RawModel loadToVAO(float[] positions, float[] textureCoords, float[] normals, int[] indices) {
        int vaoID = createVAO();
        bindIndicesBuffer(indices);
        storeDataInAttributeList(0, 3,positions);
        storeDataInAttributeList(1, 2,textureCoords);
        storeDataInAttributeList(2, 3,normals);
        unbindVAO();
        return new RawModel(vaoID, indices.length);
    }

    public RawModel loadToVAO(ModelData data) {
        RawModel rawModel = loadToVAO(data.getVertices(), data.getTextureCoords(), data.getNormals(), data.getIndices());
        if (data.getBoundingCoords().length == 6)
            rawModel.setBoundingCoords(data.getBoundingCoords());
        return rawModel;
    }

//    public RawModel loadToVAO(float[] positions, float[] textureCoords, float[] normals,
//                              int[] indices) {
//        int vaoID = createVAO();
//        bindIndicesBuffer(indices);
//        storeDataInAttributeList(0, 3, positions);
//        storeDataInAttributeList(1, 2, textureCoords);
//        storeDataInAttributeList(2, 3, normals);
//        unbindVAO();
//        return new RawModel(vaoID, indices.length);
//    }
//
//    public RawModel loadToVAO(float[] positions, float[] textureCoords, float[] normals, float[] tangents,
//                              int[] indices) {
//        int vaoID = createVAO();
//        bindIndicesBuffer(indices);
//        storeDataInAttributeList(0, 3, positions);
//        storeDataInAttributeList(1, 2, textureCoords);
//        storeDataInAttributeList(2, 3, normals);
//        storeDataInAttributeList(3, 3, tangents);
//        unbindVAO();
//        return new RawModel(vaoID, indices.length);
//    }

    public RawModel loadToVAO(float[] positions) {
        int vaoID = createVAO();
        this.storeDataInAttributeList(0, 2, positions);
        unbindVAO();
        return new RawModel(vaoID, positions.length / 2);
    }

    public int loadFontTexture(String fileName) {
        Texture texture = null;
        try {
            texture = TextureLoader.getTexture(fileName+".png");
            glGenerateMipmap(GL_TEXTURE_2D);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER,
                    GL_LINEAR_MIPMAP_LINEAR);
            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_LOD_BIAS, 0f);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Tried to load texture " + fileName + ".png , didn't work");
            System.exit(-1);
        }
        textures.add(texture.getTextureID());
        return texture.getTextureID();
    }

    public int loadTexture(String fileName) {
        Texture texture = null;
        try {
            texture = TextureLoader.getTexture("src/main/resources/assets/textures/" + fileName + ".png");
            glGenerateMipmap(GL_TEXTURE_2D);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
            glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_LOD_BIAS, -.3f);
//            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
//            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Tried to load texture " + fileName + ".png , didn't work");
            System.exit(-1);
        }
        int textureID = texture.getTextureID();
        textures.add(textureID);
        return textureID;
    }

    public void cleanUp() {
        for (int vao : vaos) {
            glDeleteVertexArrays(vao);
        }
        for (int vbo : vbos) {
            GL15.glDeleteBuffers(vbo);
        }
        for (int texture : textures) {
            glDeleteTextures(texture);
        }
    }

//    public int loadCubeMap(String[] textureFiles) {
//        int texID = GL11.glGenTextures();
//        GL13.glActiveTexture(GL13.GL_TEXTURE0);
//        GL11.glBindTexture(GL13.GL_TEXTURE_CUBE_MAP, texID);
//
//        for (int i = 0; i < textureFiles.length; i++) {
//            TextureData data = decodeTextureFile("res/" + textureFiles[i] + ".png");
//            GL11.glTexImage2D(GL13.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, GL11.GL_RGBA, data.getWidth(), data.getHeight(), 0,
//                    GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, data.getBuffer());
//        }
//
//        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
//        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
//        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
//        GL11.glTexParameteri(GL13.GL_TEXTURE_CUBE_MAP, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
//        textures.add(texID);
//        return texID;
//    }

//    private TextureData decodeTextureFile(String fileName) {
//        int width = 0;
//        int height = 0;
//        ByteBuffer buffer = null;
//        try {
//            FileInputStream in = new FileInputStream(fileName);
//            PNGDecoder decoder = new PNGDecoder(in);
//            width = decoder.getWidth();
//            height = decoder.getHeight();
//            buffer = ByteBuffer.allocateDirect(4 * width * height);
//            decoder.decode(buffer, width * 4, Format.RGBA);
//            buffer.flip();
//            in.close();
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.err.println("Tried to load texture " + fileName + ", didn't work");
//            System.exit(-1);
//        }
//        return new TextureData(buffer, width, height);
//    }

    private int createVAO() {
        int vaoID = glGenVertexArrays();
        vaos.add(vaoID);
        glBindVertexArray(vaoID);
        return vaoID;
    }

    private void storeDataInAttributeList(int attributeNumber, int coordinateSize, float[] data) {
        int vboID = GL15.glGenBuffers();
        vbos.add(vboID);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboID);
        FloatBuffer buffer = storeDataInFloatBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
        GL20.glVertexAttribPointer(attributeNumber, coordinateSize, GL_FLOAT, false, 0, 0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
    }

    private void unbindVAO() {
        glBindVertexArray(0);
    }

    private void bindIndicesBuffer(int[] indices) {
        int vboID = GL15.glGenBuffers();
        vbos.add(vboID);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboID);
        IntBuffer buffer = storeDataInIntBuffer(indices);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
    }

    private IntBuffer storeDataInIntBuffer(int[] data) {
        IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

    private FloatBuffer storeDataInFloatBuffer(float[] data) {
        FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
        buffer.put(data);
        buffer.flip();
        return buffer;
    }

}
