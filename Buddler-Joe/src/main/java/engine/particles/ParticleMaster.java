package engine.particles;

import engine.particles.systems.Explosion;
import engine.particles.systems.Fire;
import engine.particles.systems.Smoke;
import engine.render.Loader;
import entities.Camera;
import org.joml.Matrix4f;

import java.util.*;

public class ParticleMaster {
    private static Map<ParticleTexture, List<Particle>> particles = new HashMap<>();
    private static ParticleRenderer renderer;

    public static void init(Loader loader, Matrix4f projectionMatrix) {
        renderer = new ParticleRenderer(loader, projectionMatrix);

        //Load the textures of all the particle effects
        Fire.init(loader);
        Explosion.init(loader);
        Smoke.init(loader);
    }

    public static void update(Camera camera) {
        //Update all the Lists of Particles, remove any if necessary
        Iterator<Map.Entry<ParticleTexture, List<Particle>>> mapIterator = particles.entrySet().iterator();
        while (mapIterator.hasNext()) {
            List<Particle> list = mapIterator.next().getValue();
            Iterator<Particle> iterator = list.iterator();
//            System.out.println(""+list.size());
            while (iterator.hasNext()) {
                Particle p = iterator.next();
                boolean stillAlive = p.update(camera);
                if (!stillAlive) {
                    iterator.remove();
                    if(list.isEmpty()) {
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
    public static void addParticle(Particle particle) {
        List<Particle> list = particles.get(particle.getTexture());
        if(list == null) {
            list = new ArrayList<>();
            particles.put(particle.getTexture(), list);
        }
        list.add(particle);
    }
}
