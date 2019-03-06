package engine.particles.systems;

import engine.particles.ParticleSystem;
import engine.particles.ParticleTexture;
import engine.render.Loader;

public class Explosion extends ParticleSystem {


    private static ParticleTexture particleTexture;


    /**
     * ParticleSystem with texture for explosion pre-loaded. See ParticleSystem.java for more details.
     */
    public Explosion(float pps, float speed, float gravityComplient, float lifeLength, float scale) {
        super(particleTexture, pps, speed, gravityComplient, lifeLength, scale);
    }

    public static void init(Loader loader) {
        particleTexture = new ParticleTexture(loader.loadTexture("particleAtlas"), 4, true);
    }
}
