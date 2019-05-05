package game.stages;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_H;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_P;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_1;

import engine.io.InputHandler;
import engine.particles.ParticleMaster;
import engine.render.Loader;
import engine.render.MasterRenderer;
import engine.render.fontrendering.TextMaster;
import entities.Entity;
import entities.blocks.BlockMaster;
import entities.blocks.debris.DebrisMaster;
import entities.items.ItemMaster;
import entities.items.Star;
import entities.light.LightMaster;
import game.Game;
import game.NetPlayerMaster;
import gui.GuiTexture;
import gui.MenuButton;
import gui.text.FloatingStrings;
import java.lang.management.MemoryUsage;
import java.util.ArrayList;
import java.util.List;

import net.packets.lists.PacketHighscore;
import net.packets.lists.PacketPlayerList;
import org.joml.Vector2f;
import terrains.TerrainFlat;
import util.MousePlacer;

/**
 * MAIN GAME LOOP specification and rendering. Contains and manages the Game Loop while the player
 * is playing the game. All the rendering and updating is done here.
 */
public class Playing {

  private static final float damageTakenScreenTotalDuration = 2f;
  //private static FloatingStrings floatingGoldStrings;
  private static GuiTexture damageOverlay;
  private static float damageTakenScreenRemaining = 0f;
  private static int redDown = 0;
  private static int redUp = 0;
  private static int fase = 0;
  private static boolean firstloop = true;
  private static MenuButton whisper;
  private static MenuButton all;
  private static GuiTexture damageCorner;
  private static float freezeRemaining = 0f;
  private static int freezeFramesRemaining;
  private static int freezeFramesTotal;
  private static GuiTexture iceCracks;
  private static GuiTexture iceGradient;
  private static GuiTexture iceTotal;
  private static MenuButton resetWhisperAll;

  /**
   * * Initialize Game Menu. Will load the texture files and other GUI elements needed for this
   * stage. This needs to be called once before using the stage.
   *
   * @param loader main loader
   */
  public static void init(Loader loader) {
    LoadingScreen.progess();
    damageOverlay =
        new GuiTexture(loader.loadTexture("HurtRed"), new Vector2f(0, 0), new Vector2f(1, 1), 1);
    LoadingScreen.progess();
    damageCorner =
        new GuiTexture(loader.loadTexture("RedGrad4K"), new Vector2f(0, 0), new Vector2f(1, 1), 1);
    LoadingScreen.progess();
    iceCracks =
        new GuiTexture(loader.loadTexture("EisRisse"), new Vector2f(0, 0), new Vector2f(1, 1), 1);
    LoadingScreen.progess();
    iceGradient =
        new GuiTexture(
            loader.loadTexture("whitegradient"), new Vector2f(0, 0), new Vector2f(1, 1), 1);
    LoadingScreen.progess();
    iceTotal =
        new GuiTexture(loader.loadTexture("whiteOut"), new Vector2f(0, 0), new Vector2f(1, 1), 1);
    LoadingScreen.progess();
    // floatingGoldStrings = new FloatingStrings(Game.getActivePlayer().getBbox(), 3f);
    LoadingScreen.progess();
    whisper =
        new MenuButton(
            loader,
            "smallW_norm",
            "smallW_hover",
            new Vector2f(-0.836458f, -0.322296f),
            new Vector2f(.057691f, .025f));
    whisper.setActivationMinAlpha(0.8f);
    LoadingScreen.progess();
    all =
        new MenuButton(
            loader,
            "smallA_norm",
            "smallA_hover",
            new Vector2f(-0.747917f, -0.322296f),
            new Vector2f(.026799f, .025f));
    all.setActivationMinAlpha(0.8f);

    resetWhisperAll =
        new MenuButton(
            loader,
            "changeAR_norm",
            "changeAR_hover",
            new Vector2f(-0.701042f, -0.322296f),
            new Vector2f(.012169f, 0.01875f));
    resetWhisperAll.setActivationMinAlpha(0.8f);
  }

  /**
   * Game Loop. This runs every frame as long as the payer is playing the game. Include all
   * rendering and input handling here.
   *
   * @param renderer master renderer from game loop
   */
  public static void update(MasterRenderer renderer) {
    if (firstloop) {

      Game.getChat().setGameChatSettings();
      TextMaster.removeAll();
      firstloop = false;
    }

    double x = 2 * (InputHandler.getMouseX() / Game.window.getWidth()) - 1;
    double y = 1 - 2 * (InputHandler.getMouseY() / Game.window.getHeight());

    List<GuiTexture> guis = new ArrayList<>();
    guis.add(Game.getChat().getChatGui());

    whisper.setAlpha(Game.getChat().getAlpha());
    guis.add(whisper.getHoverTexture(x, y));
    all.setAlpha(Game.getChat().getAlpha());
    guis.add(all.getHoverTexture(x, y));
    resetWhisperAll.setAlpha(Game.getChat().getAlpha());
    if (!Game.getChat().getWisperName().equals("")) {
      guis.add(resetWhisperAll.getHoverTexture(x, y));
    } else {
      resetWhisperAll.setAlpha(0);
    }

    // ESC = Game Menu
    if (InputHandler.isKeyPressed(GLFW_KEY_ESCAPE)
        && !Game.getActiveStages().contains(Game.Stage.PLAYERLIST)) {
      Game.addActiveStage(Game.Stage.GAMEMENU);
    }

    // H = Highscore
    if (InputHandler.isKeyPressed(GLFW_KEY_H)
        && !Game.getChat().isEnabled()
        && !Game.getActiveStages().contains(Game.Stage.GAMEMENU)
        && !Game.getActiveStages().contains(Game.Stage.HIGHSCORE)) {
      new PacketHighscore().sendToServer();
      Highscore.setInGame(true);
      Game.addActiveStage(Game.Stage.HIGHSCORE);
      Game.getChat().hide();
    }
    if (InputHandler.isKeyPressed(GLFW_KEY_P)
            && !Game.getChat().isEnabled()
            && !Game.getActiveStages().contains(Game.Stage.GAMEMENU)
            && !Game.getActiveStages().contains(Game.Stage.PLAYERLIST)
        || (whisper.isHover(x, y) && InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1))
            && !Game.getActiveStages().contains(Game.Stage.GAMEMENU)
            && !Game.getActiveStages().contains(Game.Stage.PLAYERLIST)) {
      PacketPlayerList playerList = new PacketPlayerList();
      playerList.sendToServer();
      Game.addActiveStage(Game.Stage.PLAYERLIST);
      Game.getChat().hide();
    }

    if (all.isHover(x, y)
        && InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1)
        && !Game.getActiveStages().contains(Game.Stage.GAMEMENU)
        && !Game.getActiveStages().contains(Game.Stage.PLAYERLIST)) {
      Game.getChat().setWisperName("all");
    }

    if (resetWhisperAll.isHover(x, y)
        && InputHandler.isMousePressed(GLFW_MOUSE_BUTTON_1)
        && !Game.getActiveStages().contains(Game.Stage.GAMEMENU)
        && !Game.getActiveStages().contains(Game.Stage.PLAYERLIST)) {
      Game.getChat().deleteWisperName();
    }

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

    // Prepare and render the terrains
    TerrainFlat[][] terrainChunks = Game.getTerrainChunks();
    for (int i = 0; i < Game.getMap().getTerrainRows(); i++) {
      for (int j = 0; j < Game.getMap().getTerrainCols(); j++) {
        renderer.processTerrain(terrainChunks[j][i]);
      }
    }

    // renderer.processTerrain(Game.getTerrainChunks()[0][0]);

    // Prepare and Render the entities
    renderer.processEntity(Game.getActivePlayer());
    NetPlayerMaster.update(renderer);
    for (Entity entity : Game.getEntities()) {
      if (entity != null) {
        renderer.processEntity(entity);
      }
    }

    // Render other stuff, order is important
    renderer.render(LightMaster.getLightsToRender(), Game.getActiveCamera());
    Game.getChat().checkInputs();
    // GUI goes over everything else and then text on top of GUI
    Game.getGoldGuiText().update();

    // Update Gui Life Status
    if (Game.getLifeStatus().checkLifeStatus() > 0) {
      guis.add(Game.getLifeStatus().getLifeStatusGui()[0]);
      if (Game.getLifeStatus().checkLifeStatus() > 1) {
        guis.add(Game.getLifeStatus().getLifeStatusGui()[1]);
      }
    }

    //floatingGoldStrings.update();
    ParticleMaster.renderParticles(Game.getActiveCamera());

    guis = applyDamage(guis);
    guis = applyFreeze(guis);

    Game.getGuiRenderer().render(guis);
    TextMaster.render();
  }

  //public static void addFloatingGoldText(int goldValue) {
  //  floatingGoldStrings.addString("+ " + goldValue);
  //}

  /** Prepares variables, so that the DamageTakenOverlay can be displayed. */
  public static void showDamageTakenOverlay() {
    damageTakenScreenRemaining = damageTakenScreenTotalDuration;
    damageOverlay.setAlpha(1f);
    damageCorner.setAlpha(1f);
    redDown = 30;
    redUp = 15;
    fase = 1;
  }

  /** Prepares variables, so that the FreezeOverlay can be displayed. */
  public static void showFreezeOverlay() {
    freezeRemaining = Star.getFreezeTime();
    freezeFramesRemaining = (int) freezeRemaining * 60;
    freezeFramesTotal = freezeFramesRemaining;
    iceTotal.setAlpha(0.75f);
    iceCracks.setAlpha(0.75f);
    iceGradient.setAlpha(0.75f);
  }

  /**
   * Calculates and applies alpha values for freeze GuiTextures.
   *
   * @param guis the current gui List to which the Freeze-GuiTextures should be added.
   * @return a list that equals the parameter guis with added Freeze-GuiTextures if necessary.
   */
  public static List<GuiTexture> applyFreeze(List<GuiTexture> guis) {
    if (freezeRemaining > 0) {
      freezeRemaining -= Game.dt();

      if (freezeFramesRemaining > (int) (0.75f * freezeFramesTotal)) {
        float stepsize = 0.75f / (0.25f * (float) freezeFramesTotal);
        if (iceTotal.getAlpha() - stepsize > 0) {
          iceTotal.setAlpha(iceTotal.getAlpha() - stepsize);
        }
      } else if (freezeFramesRemaining <= (int) (0.75f * freezeFramesTotal)
          && freezeFramesRemaining > (int) (0.5 * freezeFramesTotal)) {
        float stepsize = 0.75f / (0.5f * (float) freezeFramesTotal);
        if (iceGradient.getAlpha() - stepsize > 0) {
          iceGradient.setAlpha(iceGradient.getAlpha() - stepsize);
        }
      } else if (freezeFramesRemaining <= (int) (0.5f * freezeFramesTotal)) {
        float stepsize = 0.75f / (0.5f * (float) freezeFramesTotal);
        if (iceGradient.getAlpha() - stepsize > 0) {
          iceGradient.setAlpha(iceGradient.getAlpha() - stepsize);
        }
        if (iceCracks.getAlpha() - stepsize > 0) {
          iceCracks.setAlpha(iceCracks.getAlpha() - stepsize);
        }
      }
      guis.add(iceTotal);
      guis.add(iceGradient);
      guis.add(iceCracks);
      freezeFramesRemaining--;
    } else {
      iceTotal.setAlpha(0);
      iceCracks.setAlpha(0);
      iceGradient.setAlpha(0);
    }
    return guis;
  }

  /**
   * Calculates and applies alpha values for Damage GuiTextures.
   *
   * @param guis the current gui List to which the Damage-GuiTextures should be added.
   * @return a list that equals the parameter guis with added Damage-GuiTextures if necessary.
   */
  public static List<GuiTexture> applyDamage(List<GuiTexture> guis) {
    if (damageTakenScreenRemaining > 0) {
      damageTakenScreenRemaining -= Game.dt();
      float alpha = damageOverlay.getAlpha();
      if (fase == 1) {
        if (alpha - 0.033333f > 0) {
          damageOverlay.setAlpha(alpha - 0.033333f);
        }
        redDown--;
      }
      if (fase == 2) {
        if (alpha + 0.033333f < 1) {
          damageOverlay.setAlpha(alpha + 0.033333f);
        }
        redUp--;
      }
      if (fase == 3) {
        if (alpha - 0.022222f > 0) {
          damageOverlay.setAlpha(alpha - 0.022222f);
        }
        if (damageCorner.getAlpha() - 0.033333f > 0) {
          damageCorner.setAlpha(damageCorner.getAlpha() - 0.022222f - 0.011111f);
        }
      }
      if (redDown == 0 && redUp == 15) {
        fase = 2;
      } else if (redDown == 0 && redUp == 0) {
        fase = 3;
      }
      guis.add(damageOverlay);
      guis.add(damageCorner);
    }
    return guis;
  }

  /** Delete all text objects from this stage. */
  public static void done() {
    firstloop = true;
    //floatingGoldStrings.done();
    Game.getGoldGuiText().done();
  }
}
