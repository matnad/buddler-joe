package entities.blocks;

import entities.items.Dynamite;
import entities.items.Item;
import entities.items.ItemMaster;
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
        super(BlockMaster.BlockTypes.GOLD, 2f, 3f,  position, rotX, rotY, rotZ, scale);
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
//        Item dynamite = ItemMaster.generateItem(ItemMaster.ItemTypes.DYNAMITE, getPosition());
//        ((Dynamite) dynamite).setActive(true);
    }
}
