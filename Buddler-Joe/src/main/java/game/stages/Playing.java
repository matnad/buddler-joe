package game.stages;

import engine.io.InputHandler;
import engine.particles.ParticleMaster;
import engine.render.MasterRenderer;
import engine.render.fontRendering.TextMaster;
import entities.Entity;
import entities.NetPlayer;
import entities.blocks.BlockMaster;
import entities.blocks.debris.DebrisMaster;
import entities.items.ItemMaster;
import entities.light.LightMaster;
import game.Game;
import util.MousePlacer;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_ESCAPE;

public class Playing {

    public static void update(MasterRenderer renderer) {
        //ESC = Exit... we will add a menu later
        if(InputHandler.isKeyPressed(GLFW_KEY_ESCAPE)) {
            Game.setStage(Game.Stage.GAMEMENU);
        }

        /*InputHandler needs to be BEFORE polling (window.update()) so we still have access to the events of last Frame.
          Everythine else should be after polling.*/
        InputHandler.update();
        Game.window.update();

        //Update positions of camera, player and 3D Mouse Pointer
        Game.getActiveCamera().move();
        Game.getActivePlayer().move();
        MousePlacer.update(Game.getActiveCamera());

        //Masters check their slaves
        ItemMaster.update();
        BlockMaster.update();
        DebrisMaster.update();
        ParticleMaster.update(Game.getActiveCamera());
        LightMaster.update(Game.getActiveCamera(), Game.getActivePlayer());

        //Prepare and render the entities
        renderer.processEntity(Game.getActivePlayer());
        renderer.processTerrain(Game.getAboveGround());
        renderer.processTerrain(Game.getBelowGround());
        for (Entity entity : Game.getEntities()) {
            if (entity != null) {
                //All the NetPlayer stuff will need to move to a different class and update it from there
                if(entity instanceof NetPlayer) {
                    ((NetPlayer) entity).getDirectionalUsername().updateString();
                }
                renderer.processEntity(entity);
            }
        }

        //Render other stuff, order is important
        renderer.render(LightMaster.getLightsToRender(), Game.getActiveCamera());
        Game.getChat().checkInputs();
        //GUI goes over everything else and then text on top of GUI
        ParticleMaster.renderParticles(Game.getActiveCamera());
//                guiRenderer.render(guis);
        TextMaster.render();
    }

}
