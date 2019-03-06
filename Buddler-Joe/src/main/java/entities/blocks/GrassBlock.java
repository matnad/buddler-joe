package entities.blocks;

import org.joml.Vector3f;


public class GrassBlock extends Block {

    private static float hardness = .7f;

    public GrassBlock(Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        super(4, position, rotX, rotY, rotZ, scale);
        super.setHardness(hardness);
    }

    public GrassBlock(Vector3f position) {
        this(position, 0, 0 ,0, 3);

    }

    @Override
    protected void onDestroy() {

    }
}
