package net.packets;

import net.ClientLogic;
import net.ServerLogic;

public class Packet00Login extends Packet{

    private String username;
    private String model;
    private String texture;
    private float modelSize;

    public Packet00Login(byte[] data) {
        super(00);
        String[] dataArray = readData(data).split(",");
        this.username = dataArray[0];
        this.model = dataArray[1];
        this.texture = dataArray[2];
        try {
            this.modelSize = Float.parseFloat(dataArray[3]);
        } catch (NumberFormatException e) {
            this.setPacketId((byte) -1); //INVALID PACKET
        }
    }

    public Packet00Login(String username, String model, String texture, float modelSize) {
        super(00);
        this.username = username;
        this.model = model;
        this.texture = texture;
        this.modelSize = modelSize;
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
                        "00"+
                        this.username+","+
                        this.model+","+
                        this.texture+","+
                        this.modelSize
        ).getBytes();
        //TODO Codierung weg und alles Klartext!
    }

    public String getUsername() {
        return username;
    }

    public String getModel() {
        return model;
    }

    public String getTexture() {
        return texture;
    }

    public float getModelSize() {
        return modelSize;
    }
}
