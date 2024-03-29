package engine.particles;

import engine.particles.systems.Amped;
import engine.particles.systems.Cosmic;
import engine.particles.systems.Explosion;
import engine.particles.systems.Fire;
import engine.particles.systems.Frozen;
import engine.particles.systems.Magic;
import engine.particles.systems.Shockwave;
import engine.particles.systems.Smoke;
import engine.particles.systems.Snow;
import engine.particles.systems.Sparkle;
import engine.render.Loader;
import entities.Camera;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.joml.Matrix4f;

/**
 * Manages a Hash Map of particle lists, sorted by Texture (one list for each type of particle).
 * Will remove expired particles from the list, sort the active ones by camera distance and call the
 * renderer with the lists.
 *
 * <p>Initializes particle systems (load textures)
 */
public class ParticleMaster {
  private static Map<ParticleTexture, List<Particle>> particles = new HashMap<>();
  private static ParticleRenderer renderer;

  /**
   * Initialize the renderer and load all the particle effect systems with textures.
   *
   * @param loader Pass the main loader from the Game class. There is no reason to have more than
   *     one loader.
   * @param projectionMatrix Usually just pass the matrix from the master renderer.
   */
  public static void init(Loader loader, Matrix4f projectionMatrix) {
    renderer = new ParticleRenderer(loader, projectionMatrix);

    // Load the textures of all the particle effects
    Fire.init(loader);
    Explosion.init(loader);
    Smoke.init(loader);
    Cosmic.init(loader);
    Sparkle.init(loader);
    Frozen.init(loader);
    Snow.init(loader);
    Magic.init(loader);
    Amped.init(loader);
    Shockwave.init(loader);
  }

  /**
   * Update all the lists of particles (one list per type of particle) and remove expired ones. Sort
   * particles within list by distance from the camera so the near particles are rendered on top of
   * the far ones.
   *
   * @param camera the camera the particles are facing
   */
  public static void update(Camera camera) {
    Iterator<Map.Entry<ParticleTexture, List<Particle>>> mapIterator =
        particles.entrySet().iterator();
    while (mapIterator.hasNext()) {
      List<Particle> list = mapIterator.next().getValue();
      Iterator<Particle> iterator = list.iterator();
      while (iterator.hasNext()) {
        Particle p = iterator.next();
        boolean stillAlive = p.update(camera);
        if (!stillAlive) {
          iterator.remove();
          if (list.isEmpty()) {
            mapIterator.remove();
          }
        }
      }
      InsertionSort.sortHighToLow(list);
    }
  }

  public static void renderParticles(Camera camera) {
    renderer.render(particles, camera);
  }

  public static void cleanUp() {
    renderer.cleanUp();
  }

  /**
   * Add particle to list of its type or create a list if it is the first of its type.
   *
   * @param particle particle to add to a list
   */
  static void addParticle(Particle particle) {
    List<Particle> list = particles.computeIfAbsent(particle.getTexture(), k -> new ArrayList<>());
    list.add(particle);
  }

  public static void reset() {
    particles.clear();
  }
}
