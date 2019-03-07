package entities.blocks;

import org.joml.Vector3f;

/**
 * Dirt Block
 *
 * Holds methods and variables specific to Dirt Blocks.
 */
public class StoneBlock extends Block {

    /**
     * Extended Constructor, dont call directly.
     */
    StoneBlock(Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        super(BlockMaster.BlockTypes.STONE, 5f, 2f, position, rotX, rotY, rotZ, scale);
    }

    /**
     Shortened constructer with just position. Dont call directly.
     */
    StoneBlock(Vector3f position) {
        this(position, 0, 0 ,0, 3);
    }

    @Override
    protected void onDestroy() {

    }


}
