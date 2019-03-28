package game.stages;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

import engine.io.InputHandler;
import engine.particles.ParticleMaster;
import engine.render.MasterRenderer;
import engine.render.fontrendering.TextMaster;
import entities.Entity;
import entities.NetPlayer;
import entities.blocks.BlockMaster;
import entities.blocks.debris.DebrisMaster;
import entities.items.ItemMaster;
import entities.light.LightMaster;
import game.Game;
import game.NetPlayerMaster;
import gui.GuiTexture;
import java.util.ArrayList;
import java.util.List;
import util.MousePlacer;

/**
 * MAIN GAME LOOP specification and rendering. Contains and manages the Game Loop while the player
 * is playing the game. All the rendering and updating is done here.
 */
public class Playing {

  /**
   * Game Loop. This runs every frame as long as the payer is playing the game. Include all
   * rendering and input handling here.
   *
   * @param renderer master renderer from game loop
   */
  public static void update(MasterRenderer renderer) {

    List<GuiTexture> guis = new ArrayList<>();
    guis.add(Game.getChat().getChatGui());

    // ESC = Game Menu
    if (InputHandler.isKeyPressed(GLFW_KEY_ESCAPE)) {
      Game.addActiveStage(Game.Stage.GAMEMENU);
    }

    /*InputHandler needs to be BEFORE polling (window.update()) so we still have access to
    the events of last Frame. Everythine else should be after polling.*/
    InputHandler.update();
    Game.window.update();

    // Update positions of camera, player and 3D Mouse Pointer
    Game.getActiveCamera().move();
    Game.getActivePlayer().move();
    MousePlacer.update(Game.getActiveCamera());

    // Masters check their slaves
    ItemMaster.update();
    Game.getMap().checkFallingBlocks();
    BlockMaster.update();
    DebrisMaster.update();
    ParticleMaster.update(Game.getActiveCamera());
    LightMaster.update(Game.getActiveCamera(), Game.getActivePlayer());

    // Prepare and render the entities
    renderer.processEntity(Game.getActivePlayer());
    NetPlayerMaster.update(renderer);
    renderer.processTerrain(Game.getAboveGround());
    renderer.processTerrain(Game.getBelowGround());
    for (Entity entity : Game.getEntities()) {
      if (entity != null) {
        renderer.processEntity(entity);
      }
    }

    // Render other stuff, order is important
    renderer.render(LightMaster.getLightsToRender(), Game.getActiveCamera());
    Game.getChat().checkInputs();
    // GUI goes over everything else and then text on top of GUI
    ParticleMaster.renderParticles(Game.getActiveCamera());
    Game.getGuiRenderer().render(guis);
    TextMaster.render();
  }
}
