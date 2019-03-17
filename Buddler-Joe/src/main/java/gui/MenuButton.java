package gui;

import engine.render.Loader;
import org.joml.Vector2f;

public class MenuButton {

    private GuiTexture stateStatic, stateHover;
    private float minX, minY, maxX, maxY;

    public MenuButton(Loader loader, String fileUp, String fileDown, Vector2f position, Vector2f scale) {
        stateStatic = new GuiTexture(loader.loadTexture(fileUp), position, scale, 1);
        stateHover = new GuiTexture(loader.loadTexture(fileDown), position, scale, 1);
        minX = position.x-scale.x;
        minY = position.y-scale.y;
        maxX = position.x+scale.x;
        maxY = position.y+scale.y;
    }

    public GuiTexture getHoverTexture(double mouseX, double mouseY) {
        if(isHover(mouseX, mouseY)) {
            return stateHover;
        } else {
            return stateStatic;
        }
    }

    public boolean isHover(double mouseX, double mouseY) {
        return (mouseX > minX && mouseX < maxX) && (mouseY > minY && mouseY < maxY);
    }

}
