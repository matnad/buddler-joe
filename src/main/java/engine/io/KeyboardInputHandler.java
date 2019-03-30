package engine.io;

import static org.lwjgl.glfw.GLFW.GLFW_KEY_0;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_1;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_2;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_3;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_4;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_5;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_6;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_7;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_A;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_B;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_BACKSPACE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_C;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_CAPS_LOCK;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_D;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_DELETE;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_E;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_F;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_G;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_H;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_I;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_J;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_K;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_L;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_LEFT_SHIFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_M;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_N;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_O;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_P;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Q;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_R;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_RIGHT_SHIFT;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_S;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_T;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_U;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_V;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_W;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_X;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Y;
import static org.lwjgl.glfw.GLFW.GLFW_KEY_Z;

public class KeyboardInputHandler {

  public KeyboardInputHandler() {}

  public String getText(String newChatText) {

    char newChar;

    if (InputHandler.isKeyPressed(GLFW_KEY_A)) {
      newChar = 'a';
      newChar = checkUppercase(newChar);
      newChatText += newChar;

    } else if (InputHandler.isKeyPressed(GLFW_KEY_B)) {
      newChar = 'b';
      newChar = checkUppercase(newChar);
      newChatText += newChar;

    } else if (InputHandler.isKeyPressed(GLFW_KEY_C)) {
      newChar = 'c';
      newChar = checkUppercase(newChar);
      newChatText += newChar;

    } else if (InputHandler.isKeyPressed(GLFW_KEY_D)) {
      newChar = 'd';
      newChar = checkUppercase(newChar);
      newChatText += newChar;

    } else if (InputHandler.isKeyPressed(GLFW_KEY_E)) {
      newChar = 'e';
      newChar = checkUppercase(newChar);
      newChatText += newChar;

    } else if (InputHandler.isKeyPressed(GLFW_KEY_F)) {
      newChar = 'f';
      newChar = checkUppercase(newChar);
      newChatText += newChar;

    } else if (InputHandler.isKeyPressed(GLFW_KEY_G)) {
      newChar = 'g';
      newChar = checkUppercase(newChar);
      newChatText += newChar;

    } else if (InputHandler.isKeyPressed(GLFW_KEY_H)) {
      newChar = 'h';
      newChar = checkUppercase(newChar);
      newChatText += newChar;

    } else if (InputHandler.isKeyPressed(GLFW_KEY_I)) {
      newChar = 'i';
      newChar = checkUppercase(newChar);
      newChatText += newChar;

    } else if (InputHandler.isKeyPressed(GLFW_KEY_J)) {
      newChar = 'j';
      newChar = checkUppercase(newChar);
      newChatText += newChar;

    } else if (InputHandler.isKeyPressed(GLFW_KEY_K)) {
      newChar = 'k';
      newChar = checkUppercase(newChar);
      newChatText += newChar;

    } else if (InputHandler.isKeyPressed(GLFW_KEY_L)) {
      newChar = 'l';
      newChar = checkUppercase(newChar);
      newChatText += newChar;

    } else if (InputHandler.isKeyPressed(GLFW_KEY_M)) {
      newChar = 'm';
      newChar = checkUppercase(newChar);
      newChatText += newChar;

    } else if (InputHandler.isKeyPressed(GLFW_KEY_N)) {
      newChar = 'n';
      newChar = checkUppercase(newChar);
      newChatText += newChar;

    } else if (InputHandler.isKeyPressed(GLFW_KEY_O)) {
      newChar = 'o';
      newChar = checkUppercase(newChar);
      newChatText += newChar;

    } else if (InputHandler.isKeyPressed(GLFW_KEY_P)) {
      newChar = 'p';
      newChar = checkUppercase(newChar);
      newChatText += newChar;

    } else if (InputHandler.isKeyPressed(GLFW_KEY_Q)) {
      newChar = 'q';
      newChar = checkUppercase(newChar);
      newChatText += newChar;

    } else if (InputHandler.isKeyPressed(GLFW_KEY_R)) {
      newChar = 'r';
      newChar = checkUppercase(newChar);
      newChatText += newChar;

    } else if (InputHandler.isKeyPressed(GLFW_KEY_S)) {
      newChar = 's';
      newChar = checkUppercase(newChar);
      newChatText += newChar;

    } else if (InputHandler.isKeyPressed(GLFW_KEY_T)) {
      newChar = 't';
      newChar = checkUppercase(newChar);
      newChatText += newChar;

    } else if (InputHandler.isKeyPressed(GLFW_KEY_U)) {
      newChar = 'u';
      newChar = checkUppercase(newChar);
      newChatText += newChar;

    } else if (InputHandler.isKeyPressed(GLFW_KEY_V)) {
      newChar = 'v';
      newChar = checkUppercase(newChar);
      newChatText += newChar;

    } else if (InputHandler.isKeyPressed(GLFW_KEY_W)) {
      newChar = 'w';
      newChar = checkUppercase(newChar);
      newChatText += newChar;

    } else if (InputHandler.isKeyPressed(GLFW_KEY_X)) {
      newChar = 'x';
      newChar = checkUppercase(newChar);
      newChatText += newChar;

    } else if (InputHandler.isKeyPressed(GLFW_KEY_Y)) {
      newChar = 'y';
      newChar = checkUppercase(newChar);
      newChatText += newChar;

    } else if (InputHandler.isKeyPressed(GLFW_KEY_Z)) {
      newChar = 'z';
      newChar = checkUppercase(newChar);
      newChatText += newChar;

    } else if (InputHandler.isKeyPressed(GLFW_KEY_0)) {
      newChar = '0';
      newChatText += newChar;
    } else if (InputHandler.isKeyPressed(GLFW_KEY_1)) {
      newChar = '1';
      newChatText += newChar;
    } else if (InputHandler.isKeyPressed(GLFW_KEY_2)) {
      newChar = '2';
      newChatText += newChar;
    } else if (InputHandler.isKeyPressed(GLFW_KEY_3)) {
      newChar = '3';
      newChatText += newChar;
    } else if (InputHandler.isKeyPressed(GLFW_KEY_4)) {
      newChar = '4';
      newChatText += newChar;
    } else if (InputHandler.isKeyPressed(GLFW_KEY_5)) {
      newChar = '5';
      newChatText += newChar;
    } else if (InputHandler.isKeyPressed(GLFW_KEY_6)) {
      newChar = '6';
      newChatText += newChar;
    } else if (InputHandler.isKeyPressed(GLFW_KEY_7)) {
      newChar = '7';
      newChatText += newChar;
    }

    // TODO: (Moritz) When @ typed, then display the users for whisper

    else if (InputHandler.isKeyPressed(GLFW_KEY_BACKSPACE)) {
      if (newChatText.length() > 0) {
        newChatText = newChatText.substring(0, newChatText.length() - 1);
      }
    } else if (InputHandler.isKeyPressed(GLFW_KEY_DELETE)) {
      newChatText = "";
    }
    return newChatText;
  }

  private char checkUppercase(char c) {
    if (InputHandler.isKeyDown(GLFW_KEY_LEFT_SHIFT)
        || InputHandler.isKeyDown(GLFW_KEY_RIGHT_SHIFT)
        || InputHandler.isKeyDown(GLFW_KEY_CAPS_LOCK)) {
      return c = Character.toUpperCase(c);
    }
    return c;
  }
}
