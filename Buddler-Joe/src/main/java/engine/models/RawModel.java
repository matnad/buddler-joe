package engine.models;

public class RawModel {

    private int vaoID;
    private int vertexCount;

    float[] BB = new float[] {};

    public RawModel(int vaoID, int vertexCount){
        this.vaoID = vaoID;
        this.vertexCount = vertexCount;
    }

    public int getVaoID() {
        return vaoID;
    }

    public int getVertexCount() {
        return vertexCount;
    }

    public void setBB(float[] BB) {
        this.BB = BB;
    }

    public float[] getBB() {
        return BB;
    }
}