package engine.particles.systems;

import engine.particles.ParticleSystem;
import engine.particles.ParticleTexture;
import engine.render.Loader;

public class Debris extends ParticleSystem {


    private static ParticleTexture particleTexture;

    /**
     * ParticleSystem with texture for dirt debris pre-loaded. See ParticleSystem.java for more details.
     */
    public Debris(float pps, float speed, float gravityComplient, float lifeLength, float scale) {
        super(particleTexture, pps, speed, gravityComplient, lifeLength, scale);
    }

    public static void init(Loader loader) {
        particleTexture = new ParticleTexture(loader.loadTexture("dirt_debris2"), 3, false);
    }

    public static ParticleTexture getParticleTexture() {
        return particleTexture;
    }
}
