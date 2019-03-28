package entities.blocks;

import org.joml.Vector3f;

public class AirBlock extends Block {


  /**
   * Dummy Block for empty space.
   */
  public AirBlock() {
    // Must pass block type and hardness here as they are required
    super(BlockMaster.BlockTypes.AIR, 0f, 1f, new Vector3f(),0,0,0,0);
  }

  @Override
  protected void onDestroy() {

  }
}
