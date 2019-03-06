package entities.blocks;

import bin.Game;
import entities.items.Dynamite;
import org.joml.Vector3f;


public class GoldBlock extends Block {

    private static float hardness = 2f;

    public GoldBlock(Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        super(BlockMaster.BlockTypes.GOLD, 2f, position, rotX, rotY, rotZ, scale);
    }

    public GoldBlock(Vector3f position) {
        this(position, 0, 0 ,0, 3);
    }

    @Override
    protected void onDestroy() {
//        Dynamite dynamite = new Dynamite(getPosition());
//        dynamite.setActive(true);
    }
}
