package entities.blocks;

import bin.Game;
import engine.render.Loader;
import org.joml.Vector3f;

import java.util.*;

public class BlockMaster {
    //Organize Blocks in lists that can be accessed by their type
    private static Map<BlockTypes, List<Block>> blockLists = new HashMap<>();
    //Keep a list with just blocks
    private static List<Block> blocks = new ArrayList<>();
    //List of debris (small blocks)
    public static List<Block> debris;

    public enum BlockTypes {
        GRASS(4),
        DIRT(31),
        GOLD(30),
        STONE(11);

        private int textureId;
        BlockTypes(int textureId) {
            this.textureId = textureId;
        }

        public int getTextureId() {
            return textureId;
        }
    }

    public static void init(Loader loader) {
        Block.loadBlockModels(loader);
    }

    public static void generateBlock(BlockTypes type, Vector3f position) {
        Block block;
        switch (type) {
            case GRASS:
                block = new GrassBlock(position);
                break;
            case DIRT:
                block = new DirtBlock(position);
                break;
            case GOLD:
                block = new GoldBlock(position);
                break;
            case STONE:
                block = new StoneBlock(position);
                break;
            default:
                block = null;
                break;
        }
        addBlockToList(block);
    }

    public static void update() {
        //Remove destroyed blocks from the list and update entities
        Iterator<Map.Entry<BlockTypes, List<Block>>> mapIterator = blockLists.entrySet().iterator();
        while (mapIterator.hasNext()) {
            List<Block> list = mapIterator.next().getValue();
            Iterator<Block> iterator = list.iterator();
            while (iterator.hasNext()) {
                Block block = iterator.next();
                if (block.isDestroyed()) {
                    //Remove block from list and entities
                    Game.removeEntity(block);
                    iterator.remove();
                    blocks.remove(block);
                    //Clean up list if empty
                    if(list.isEmpty()) {
                        mapIterator.remove();
                    }
                }
            }
        }
    }

    public static void addBlockToList(Block block) {
        //Get the list with the type of the block, if the list is absent, create it
        List<Block> list = blockLists.computeIfAbsent(block.getType(), k -> new ArrayList<>());

        //If the block is not destroyed, add it to the Game to be rendered
        if (!block.isDestroyed()) {
            //Add block to its type-specific list
            list.add(block);
            //Add to type-unspecific list
            blocks.add(block);
            //Add to render list
            Game.addEntity(block);
        }
    }

    public static Map<BlockTypes, List<Block>> getBlockLists() {
        return blockLists;
    }

    public static List<Block> getBlocks() {
        return blocks;
    }
}
