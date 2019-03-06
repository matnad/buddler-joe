package entities.blocks;

import org.joml.Vector3f;


public class StoneBlock extends Block {

    private static float hardness = 5f;

    public StoneBlock(Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        super(11, position, rotX, rotY, rotZ, scale);
        super.setHardness(hardness);
    }

    public StoneBlock(Vector3f position) {
        this(position, 0, 0 ,0, 3);

    }

    @Override
    protected void onDestroy() {

    }


}
