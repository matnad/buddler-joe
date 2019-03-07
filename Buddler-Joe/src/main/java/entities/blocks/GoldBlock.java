package entities.blocks;

import bin.Game;
import entities.items.Dynamite;
import org.joml.Vector3f;

/**
 * Dirt Block
 *
 * Holds methods and variables specific to Dirt Blocks.
 */
public class GoldBlock extends Block {

    private static float hardness = 2f;

    /**
     * Extended Constructor, dont call directly.
     */
    GoldBlock(Vector3f position, float rotX, float rotY, float rotZ, float scale) {
        super(BlockMaster.BlockTypes.GOLD, 2f, position, rotX, rotY, rotZ, scale);
    }

    /**
     Shortened constructer with just position. Dont call directly.
     */
    GoldBlock(Vector3f position) {
        this(position, 0, 0 ,0, 3);
    }

    @Override
    protected void onDestroy() {
        //Drop some dynamite!
//        Dynamite dynamite = new Dynamite(getPosition());
//        dynamite.setActive(true);
    }
}
