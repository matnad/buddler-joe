package engine.particles.systems;

import engine.particles.ParticleSystem;
import engine.particles.ParticleTexture;
import engine.render.Loader;

public class Cosmic extends ParticleSystem {

  private static ParticleTexture particleTexture;

  /**
   * ParticleSystem with texture for cosmic pre-loaded. See ParticleSystem.java for more details.
   */
  public Cosmic(float pps, float speed, float gravityComplient, float lifeLength, float scale) {
    super(particleTexture, pps, speed, gravityComplient, lifeLength, scale);
  }

  public static void init(Loader loader) {
    particleTexture = new ParticleTexture(loader.loadTexture("cosmic"), 4, true);
  }
}
