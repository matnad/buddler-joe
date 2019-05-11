package gui.tutorial;

import engine.render.Loader;
import entities.Player;
import entities.items.Star;
import entities.items.Steroids;
import game.Game;
import game.Settings;
import gui.GuiTexture;
import gui.text.ChangableGuiText;
import java.util.ArrayList;
import java.util.List;
import org.joml.Vector2f;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tutorial {
  public static final Logger logger = LoggerFactory.getLogger(Tutorial.class);

  private static GuiTexture box;
  private static ChangableGuiText guiText;

  private boolean enabled;

  private float boxAlpha;

  private Topics lastActiveTopic;

  /**
   * Load textures for GUI.
   *
   * @param loader main Loader
   */
  public static void init(Loader loader) {
    box =
        new GuiTexture(
            loader.loadTexture("menuStone"), new Vector2f(.5f, .6f), new Vector2f(.3f, .12f), 0);
  }

  /** Create a new Tutorial Instance with its own Topic Enum. Usually we only need 1 instance. */
  public Tutorial() {
    logger.debug("new Tutorial instance");
    if (box == null) {
      logger.error("Tutorial GUI not properly initialized! Missing Texture.");
      return;
    }

    guiText = new ChangableGuiText();
    guiText.setPosition(new Vector2f(.61f, .17f));
    guiText.setFontSize(.75f);
    guiText.setMaxLineLength(.28f);

    Settings settings = Game.getSettings();
    if (settings == null) {
      logger.error("No settings found.");
    } else {

      if (settings.getCompletedTutorials().size() == Topics.values().length) {
        // All tutorials done, turn it off for performance
        enabled = false;
      } else {
        enabled = true;
        for (Topics completedTutorial : settings.getCompletedTutorials()) {
          completedTutorial.enabled = false;
          // logger.debug("Tutorial already done: " + completedTutorial.text);
        }
      }
    }

    Topics.activeTopic = null;
    for (Topics topic : Topics.values()) {
      topic.timeShown = 0;
      topic.active = false;
    }

    Topics.setActive(Topics.MOVEMENT, true);
    Topics.setActive(Topics.DIGGING, true);

    box.setAlpha(0);
  }

  /** Reset / Restart the tutorial. */
  public void reset() {
    Settings settings = Game.getSettings();
    if (settings != null) {
      settings.resetTutorial();
    }
    enabled = true;
    Topics.activeTopic = null;
    for (Topics topic : Topics.values()) {
      topic.enabled = true;
      topic.active = false;
      topic.timeShown = 0;
    }
    Topics.setActive(Topics.MOVEMENT, true);
    Topics.setActive(Topics.DIGGING, true);
  }

  /**
   * Get all the Gui Elements for this frame for the Tutorial.
   *
   * @return List of GuiTextures for the Tutorial
   */
  public List<GuiTexture> getGuis() {
    // Check if tutorial is enabled or intro is running
    if (!enabled || Game.getActiveCamera().isIntro()) {
      return new ArrayList<>();
    }

    ArrayList<GuiTexture> guis = new ArrayList<>();
    guis.add(box);
    updateTopics();
    // Check if we have an active topic
    Topics topic = Topics.getActiveTopic();
    float boxGoalAlpha;
    if (topic == null) {
      boxGoalAlpha = 0;
      Topics.pauseTimer += Game.dt();
    } else {
      if (topic != lastActiveTopic) {
        // Store last topic so we can fade it out after it is active
        lastActiveTopic = topic;
      }
      boxGoalAlpha = .7f;
      // Check if topic has to wait before displaying a new one
      if (!topic.addTimeShown((float) Game.dt())) {
        boxGoalAlpha = 0;
      }
    }

    // Fading
    if (boxAlpha < boxGoalAlpha) {
      boxAlpha = (float) Math.min(.7f, boxAlpha + Game.dt());
      box.setAlpha(boxAlpha);
      guiText.setAlpha(boxAlpha);
      guiText.updateString();
    } else if (boxAlpha > boxGoalAlpha) {
      boxAlpha = (float) Math.max(0, boxAlpha - Game.dt());
      box.setAlpha(boxAlpha);
      guiText.setAlpha(boxAlpha);
      guiText.updateString();
    }

    return guis;
  }

  // Generic topic control
  private void updateTopics() {
    if (Topics.CAMERA.enabled) {
      long elapsed = (System.currentTimeMillis() - Game.getStartedAt()) / 1000;
      if (elapsed < 36 && elapsed >= 35 && !Topics.CAMERA.isActive()) {
        Topics.setActive(Topics.CAMERA, true);
      }
    }
  }

  /** Tutorial Topics with their description and duration. */
  public enum Topics {
    MOVEMENT(true, "Press A and D to move left and right. Press W or Space to jump.", 30),
    DIGGING(true, "Dig blocks by moving into them.", 30),
    CAMERA(true, "Hold the left mouse button or use the arrow keys to move the camera.", 6),
    STONE(
        true,
        "Stone is very hard... it takes a long time to break. Dirt is much easier to dig!",
        4),
    STAR(
        true,
        "Nice! You found a STAR. This item will freeze all other players for "
            + (int) Star.getFreezeTime()
            + " seconds.",
        5),
    STARRED(
        true,
        "Gaah! Another player found a STAR and will freeze and slow you for "
            + (int) Star.getFreezeTime()
            + " seconds.",
        5),
    ICE(
        true,
        "Oops! You found an Ice Block. This item will freeze and slow you for "
            + (int) Star.getFreezeTime()
            + " seconds.",
        5),
    DYNAMITE(
        true,
        "Careful! You found a DYNAMITE. After 3 seconds, it will explode and damage you "
            + "if you are closer than 4 blocks to it.",
        7),
    STEROIDS(
        true,
        "YEEEEAAH! You found an AMP UP. For "
            + (int) Steroids.getSteroidsTime()
            + " seconds your move- and dig speed will be greatly amped up!",
        5),
    HEART(
        true,
        "Sweet! You found a HEART. If you have less than 2 lives, "
            + "pick it up to regain one of your lives.",
        5),
    GOLD(
        true,
        "Be the first to reach 3000 Gold to win the Game! "
            + "The value of gold blocks increases the deeper you go.",
        5),
    CRUSHED(
        true,
        "Ouch! You got crushed by a falling stone block and lost a life. "
            + "A free standing stone block will fall after a short, random delay.",
        6),
    OBSIDIAN(true, "Obisdian is indestructible.", 3),
    TORCH(
        true,
        "The deeper you delve, the darker it gets. Press E to start placing a Torch every "
            + (int) Player.getTorchPlaceDelay()
            + " seconds.",
        10);

    private static final float PAUSE_TIME = 2;
    private static float pauseTimer;

    private static Topics activeTopic = null;

    private boolean enabled;
    private String text;
    private float displayDuration;
    private boolean active;
    private float timeShown;

    Topics(boolean enabled, String text, float displayDuration) {
      this.enabled = enabled;
      this.active = false;
      this.text = text;
      this.displayDuration = displayDuration;
    }

    public static Topics getActiveTopic() {
      return activeTopic;
    }

    public boolean isActive() {
      return enabled && active;
    }

    public boolean isEnabled() {
      return enabled;
    }

    /**
     * Add time to the currently shown topic or tick up the pause timer. Returns true if time was
     * added to a topic and false if time was added to the pause timer.
     *
     * @param time time to add
     * @return true if time was added to a topic. false if time was added to the pause timer.
     */
    private boolean addTimeShown(float time) {
      if (pauseTimer < PAUSE_TIME) {
        pauseTimer += time;
        return false;
      } else {
        if (timeShown <= 0) {
          guiText.setAlpha(0);
          guiText.changeText(text);
        }
        timeShown += time;
        if (timeShown >= displayDuration) {
          stopTopic();
        }
        return true;
      }
    }

    /** Stop a topic from being displayed and prevent it from being displayed in the future. */
    public void stopTopic() {
      if (enabled) {
        enabled = false;
        active = false;
        activeTopic = null;
        updateActiveTopic();
        pauseTimer = 0;
        Game.getSettings().addCompletedTutorial(this);
      }
    }

    /** Check if there is a new active topic and choose the first. */
    private static void updateActiveTopic() {
      if (activeTopic != null) {
        return;
      }
      for (Topics topic : values()) {
        if (topic.isActive()) {
          activeTopic = topic;
          return;
        }
      }
      activeTopic = null;
    }

    /**
     * Set the active status of a topic. This is used to trigger a Tutorial from somewhere in the
     * game.
     *
     * @param topic The topic to set
     * @param status The status of the topic (true means show the tutorial)
     */
    public static void setActive(Topics topic, boolean status) {
      for (Topics t : values()) {
        if (t == topic) {
          if (t.enabled) {
            t.active = status;
          }
          break;
        }
      }
      updateActiveTopic();
    }
  }
}
