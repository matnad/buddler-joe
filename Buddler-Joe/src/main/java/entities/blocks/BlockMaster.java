package entities.blocks;

import org.joml.Vector3f;

import javax.sound.sampled.BooleanControl;
import java.util.List;

public class BlockMaster {

    public static List<Block> blocks;
    public static List<Block> debris;

    public static enum BlockTypes {
        GRASS(4),
        DIRT(31),
        GOLD(30),
        STONE(11);

        private int blockId;
        BlockTypes(int blockId) {
            this.blockId = blockId;
        }

        public int getBlockId() {
            return blockId;
        }
    }

    public static void generateBlock(BlockTypes type, Vector3f position) {
        int index = type.getBlockId();
        float hardness = 0;
        switch (type) {
            case GRASS:
                break;
            case DIRT:
                break;
            case GOLD:
                break;
            case STONE:
                break;
            default:
                break;

        }
    }

}
