package engine.particles.systems;

import engine.particles.ParticleSystem;
import engine.particles.ParticleTexture;
import engine.render.Loader;

public class Smoke extends ParticleSystem {

  private static ParticleTexture particleTexture;

  /** ParticleSystem with texture for smoke pre-loaded. See ParticleSystem.java for more details.
   * @param pps Particles per second. Will be probabilistically rounded each frame.
   * @param speed Distance travelled per second.
   * @param gravityComplient Effect of the gravity constant. 0 means no gravity, negative numbers
   *     mean negative gravity.
   * @param lifeLength Duration before the particle is removed in seconds.
   * @param scale Size of the particle.
   * */
  public Smoke(float pps, float speed, float gravityComplient, float lifeLength, float scale) {
    super(particleTexture, pps, speed, gravityComplient, lifeLength, scale);
  }

  public static void init(Loader loader) {
    particleTexture = new ParticleTexture(loader.loadTexture("smoke"), 8, false);
  }
}
