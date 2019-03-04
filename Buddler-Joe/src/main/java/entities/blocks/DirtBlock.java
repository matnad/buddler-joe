package entities.blocks;

import org.joml.Vector3f;


public class DirtBlock extends Block {

    public DirtBlock(Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        super(31, position, rotX, rotY, rotZ, scale);
        super.setHardness(.9f);
    }

    public DirtBlock(Vector3f position) {
        this(position, 0, 0 ,0, 3);

    }

    @Override
    protected void onDestroy() {

    }
}
