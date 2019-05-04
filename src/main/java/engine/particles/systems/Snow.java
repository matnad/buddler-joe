package engine.particles.systems;

import engine.particles.ParticleSystem;
import engine.particles.ParticleTexture;
import engine.render.Loader;
import entities.NetPlayer;
import org.joml.Random;
import org.joml.Vector3f;

public class Snow extends ParticleSystem {

  private static ParticleTexture particleTexture;

  /**
   * ParticleSystem with texture for ice pre-loaded. See ParticleSystem.java for more details.
   *
   * @param pps Particles per second. Will be probabilistically rounded each frame.
   * @param speed Distance travelled per second.
   * @param gravityComplient Effect of the gravity constant. 0 means no gravity, negative numbers
   *     mean negative gravity.
   * @param lifeLength Duration before the particle is removed in seconds.
   * @param scale Size of the particle.
   */
  public Snow(float pps, float speed, float gravityComplient, float lifeLength, float scale) {
    super(particleTexture, pps, speed, gravityComplient, lifeLength, scale);


  }

  public static void init(Loader loader) {
    particleTexture = new ParticleTexture(loader.loadTexture("snowflake"), 1, true);
  }

  public void makeItSnow(NetPlayer owner, int count) {
    Vector3f pos = new Vector3f(owner.getPosition());
    Random rng = new Random(System.currentTimeMillis());
    float basePosY = pos.y + owner.getBbox().getDimY() + 1.5f;
    float basePosX = pos.x;
    float basePosZ = pos.z;
    for (int i = 0; i < count; i++) {
      pos.x = basePosX + (rng.nextFloat() - .5f) * 6;
      pos.y = basePosY + (rng.nextFloat() - .5f) * 2;
      pos.z = basePosZ + (rng.nextFloat() - .5f) * 2;
      generateParticles(pos);
    }
  }
}
