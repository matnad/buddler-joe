package entities.blocks;

import org.joml.Vector3f;


public class GoldBlock extends AbstractBlock {

    public GoldBlock(Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        super(30, position, rotX, rotY, rotZ, scale);
        super.setHardness(2f);
    }

    public GoldBlock(Vector3f position) {
        this(position, 0, 0 ,0, 3);
    }

}
