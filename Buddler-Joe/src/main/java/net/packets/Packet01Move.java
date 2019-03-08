package net.packets;

import net.ClientLogic;
import net.ServerLogic;
import org.joml.Vector3f;

public class Packet01Move extends Packet{

    private String username;
    private Vector3f moveCoords;
    private float rotX, rotY, rotZ;

    public Packet01Move(byte[] data) {
        super(01);
        String[] dataArray = readData(data).split(",");
        this.username = dataArray[0];
        try {
            this.moveCoords = new Vector3f(
                    Float.parseFloat(dataArray[1]),
                    Float.parseFloat(dataArray[2]),
                    Float.parseFloat(dataArray[3])
            );
            rotX = Float.parseFloat(dataArray[4]);
            rotY = Float.parseFloat(dataArray[5]);
            rotZ = Float.parseFloat(dataArray[6]);
        } catch (NumberFormatException e) {
            //Packet invalid
            setPacketId((byte) -1);
        }

    }

    public Packet01Move(String username, Vector3f moveCoords, float rotX, float rotY, float rotZ) {
        super(01);
        this.username = username;
        this.moveCoords = moveCoords;
        this.rotX = rotX;
        this.rotY = rotY;
        this.rotZ = rotZ;
    }

    @Override
    public void writeData(ClientLogic client) {
        //client.sendData(getData());
    }

    @Override
    public void writeData(ServerLogic server) {
        //server.sendDataToAllClients(getData());
    }

    @Override
    public byte[] getData() {
        return (
                "01" + this.username+","+
                this.moveCoords.x+","+
                this.moveCoords.y+","+
                this.moveCoords.z+","+
                this.rotX+","+
                this.rotY+","+
                this.rotZ
        ).getBytes();
    }

    public String getUsername() {
        return username;
    }

    public Vector3f getMoveCoords() {
        return moveCoords;
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
}
