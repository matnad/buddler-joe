package net.packets;

import game.Game;
import game.LobbyEntry;

import java.util.concurrent.CopyOnWriteArrayList;

public class PacketHistory extends Packet {

    private String in[];

    public PacketHistory(String data) {
        // Client receives
        super(PacketTypes.HISTORY);
        setData(data);
        in = getData().split("║");
        validate();
    }


    public PacketHistory(int clientId, String data) {
        // server builds
        super(PacketTypes.HISTORY);
        setClientId(clientId);
        setData(data);
        in = getData().split("║");
        validate();
    }

    @Override
    public void validate() {
        if (getData() != null) {
            for (String s : in) {
                isExtendedAscii(s);
            }
        } else {
            addError("No data has been found");
        }
    }

    @Override
    public void processData() {
        if (hasErrors()) {
            System.out.println(createErrorMessage());
        } else if (in[0].equals("OK")) {
            System.out.println("-----------------------------------------------------");
            for (int i = 1; i < in.length; i++) {
                System.out.println(in[i]);
            }
            System.out.println("-----------------------------------------------------");
        } else {
            System.out.println(in[0]);
        }
    }
}
