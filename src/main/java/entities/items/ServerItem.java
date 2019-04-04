package entities.items;

public class ServerItem {

    private int owner;
    private ItemMaster.ItemTypes type;
    private Long creationtime;
    private boolean exists;

    public ServerItem(int owner, ItemMaster.ItemTypes type) {
        this.owner = owner;
        this.type = type;
        this.creationtime = System.currentTimeMillis();
        this.exists = true;
    }


}
