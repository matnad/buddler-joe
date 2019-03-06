package engine.particles.systems;

import engine.particles.ParticleSystem;
import engine.particles.ParticleTexture;
import engine.render.Loader;

public class Fire extends ParticleSystem {

    private static ParticleTexture particleTexture;

    /**
     * ParticleSystem with texture for fire pre-loaded. See ParticleSystem.java for more details.
     */
    public Fire(float pps, float speed, float gravityComplient, float lifeLength, float scale) {
        super(particleTexture, pps, speed, gravityComplient, lifeLength, scale);
    }

    public static void init(Loader loader) {
        particleTexture = new ParticleTexture(loader.loadTexture("fire"), 8, true);
    }
}
