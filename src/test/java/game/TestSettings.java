package game;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

public class TestSettings {

  @Test
  public void checkIsFullscreenTrue() {
    Settings settings = new Settings();
    Assert.assertFalse(settings.isFullscreen());
  }

  @Test
  public void checkChangeFullscreen() {
    Mockito.mock(Game.class);
    Settings settings = new Settings();
    boolean orig = settings.isFullscreen();
    settings.setFullscreen(true);
    Assert.assertTrue(settings.isFullscreen());
    // Revert Settings
    settings.setFullscreen(orig);
  }

  //    @Test
  //    public void checkIsFullscreenTrue() {
  //        Settings settings = new Settings();
  //        Assert.assertFalse(settings.isFullscreen());
  //    }
}
