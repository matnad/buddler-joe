//package entities.blocks;
//
//import engine.models.RawModel;
//import engine.models.TexturedModel;
//import engine.render.Loader;
//import engine.render.objconverter.ObjFileLoader;
//import engine.textures.ModelTexture;
//import entities.items.Dynamite;
//import entities.items.Item;
//import entities.items.ItemMaster;
//import org.joml.Random;
//import org.joml.Vector3f;
//
//public class QmarkBlock extends Block {
//
//  private static float hardness = 1f;
//  private static TexturedModel blockModel;
//
//  QmarkBlock(
//      Vector3f position, float rotX, float rotY, float rotZ, float scale, int gridX, int gridY) {
//    super(
//        BlockMaster.BlockTypes.QMARK,
//        hardness,
//        2f,
//        position,
//        rotX,
//        rotY,
//        rotZ,
//        scale,
//        gridX,
//        gridY);
//    setModel(blockModel);
//    setTextureIndex(0);
//  }
//
//  /** Shortened constructor with just position. Dont call directly. */
//  QmarkBlock(Vector3f position, int gridX, int gridY) {
//    this(position, 0, 0, 0, 3, gridX, gridY);
//  }
//
//  static void init(Loader loader) {
//    RawModel rawBlock = loader.loadToVao(ObjFileLoader.loadObj("qmark"));
//    ModelTexture blockAtlas = new ModelTexture(loader.loadTexture("qmark4x4"));
//    blockAtlas.setNumberOfRows(2);
//    blockModel = new TexturedModel(rawBlock, blockAtlas);
//  }
//
//  @Override
//  protected void onDestroy() {
//      Random random = new Random(1);
//      int r = random.nextInt(5);
//      if(r == 0){
//          Item dynamite = ItemMaster.generateItem(ItemMaster.ItemTypes.DYNAMITE, getPosition());
//          ((Dynamite) dynamite).setActive(true);
//      } else if(r == 1){
//            //Item heart = ItemMaster.generateItem(ItemMaster.ItemTypes.HEART, getPosition());
//            //((Heart) heart).setActive(true);
//      } else if(r == 2) {
//          //Item star = ItemMaster.generateItem(ItemMaster.ItemTypes.STAR, getPosition());
//          //((Star) star).setActive(true);
//      } else if(r == 3) {
//          //Item ice = ItemMaster.generateItem(ItemMaster.ItemTypes.ICE, getPosition());
//          //((Ice) ice).setActive(true);
//      } else if(r == 4) {
//          //Item diamond = ItemMaster.generateItem(ItemMaster.ItemTypes.DIAMOND, getPosition());
//          //((Diamond) diamond).setActive(true);
//      }
//
//
//  }
//
//  public static float getHardness() {
//    return hardness;
//  }
//}
