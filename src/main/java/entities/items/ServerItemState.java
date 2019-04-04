package entities.items;

import java.util.concurrent.CopyOnWriteArrayList;

public class ServerItemState {
    private static CopyOnWriteArrayList<ServerItem> serverItemsList;

    public ServerItemState() {
        this.serverItemsList = new CopyOnWriteArrayList<>();
    }

    public static void addItem(ServerItem item) {
        if(!serverItemsList.contains(item)) {
            serverItemsList.add(item);
        }
    }

    public static void removeItem(ServerItem item) {
        if(serverItemsList.contains(item)){
            serverItemsList.remove(item);
        }
    }

}
