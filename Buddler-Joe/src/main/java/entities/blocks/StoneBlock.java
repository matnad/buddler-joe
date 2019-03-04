package entities.blocks;

import org.joml.Vector3f;


public class StoneBlock extends AbstractBlock {

    public StoneBlock(Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        super(11, position, rotX, rotY, rotZ, scale);
        super.setHardness(5f);
    }

    public StoneBlock(Vector3f position) {
        this(position, 0, 0 ,0, 3);

    }

}
