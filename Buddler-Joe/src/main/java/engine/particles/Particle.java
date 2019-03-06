package engine.particles;


import bin.Game;
import entities.Camera;
import entities.Player;
import org.joml.Vector2f;
import org.joml.Vector3f;

/**
 * Represents one particle to generate particle effects.
 *
 * Is a rendered quad that is always facing the camera and displaying a blending texture from a texture atlas or
 * just a fixed texture. The blending goes through keyframes and is over the lifeLength of the particle.
 *
 */
public class Particle {

    private Vector3f position;
    private Vector3f velocity;
    private float gravityEffect;
    private float lifeLength;
    private float rotation;
    private float scale;

    private ParticleTexture texture;

    private Vector2f texOffset1 = new Vector2f();
    private Vector2f texOffset2 = new Vector2f();
    private float blend;


    private float elapsedTime = 0;
    private float distance;

    /**
     * @param texture ParticleTexture Object.
     * @param position Initial 3D world position of the particle.
     * @param velocity Distance traveled per second.
     * @param gravityEffect Effect of the gravity constant. 0 means no gravity, negative numbers mean negative gravity.
     * @param lifeLength Duration before the particle is removed in seconds.
     * @param rotation Rotation, perpendicular to the camera ray. In degrees from 0 to 360.
     * @param scale Size of the particle.
     */
    public Particle(ParticleTexture texture, Vector3f position, Vector3f velocity, float gravityEffect, float lifeLength, float rotation, float scale) {
        this.texture = texture;
        this.position = position;
        this.velocity = velocity;
        this.gravityEffect = gravityEffect;
        this.lifeLength = lifeLength;
        this.rotation = rotation;
        this.scale = scale;
        ParticleMaster.addParticle(this);
    }

    /**
     * Moves the particle along its direction and applies gravity to update the position for one frame.
     * Keeps track of the lifetime.
     *
     * Currently uses Player Gravity as constant. TODO: Move gravitiy to a settings class
     *
     * @param camera The camera the particels should be facing
     * @return true if the particle is still alive after the update
     */
    protected boolean update(Camera camera) {
        velocity.y += Player.GRAVITY * gravityEffect * Game.window.getFrameTimeSeconds();
        Vector3f change = new Vector3f(velocity)
                .mul((float) Game.window.getFrameTimeSeconds());
        position.add(change);

        /*We use distance squared since it is faster and makes no difference. It is used to measure which particle is
          closer to the camera.*/
        distance = new Vector3f().set(camera.getPosition()).sub(position).lengthSquared();

        updateTextureCoordInfo();
        elapsedTime += Game.window.getFrameTimeSeconds();
        return elapsedTime < lifeLength;
    }

    /**
     * Determine position on the texture atlas that the particle will display.
     *
     * index 1 / index 2: The two "frames" on the atlas where the particle is between
     * blend = weight of the first index. (1-blend = weight of the second index)
     *
     * The numbers will be passed to the shader and calculated there.     *
     */
    private void updateTextureCoordInfo() {
        float lifeFactor = elapsedTime / lifeLength;
        int stageCount = texture.getNumberOfRows() * texture.getNumberOfRows();
        float atlasProgression = lifeFactor * stageCount;
        int index1 = (int) Math.floor(atlasProgression);
        int index2 = index1 < stageCount -1 ? index1 + 1 : index1;
        this.blend = atlasProgression % 1;
        setTextureOffset(texOffset1, index1);
        setTextureOffset(texOffset2, index2);
    }

    private void setTextureOffset(Vector2f offset, int index) {
        int column = index % texture.getNumberOfRows();
        int row = index / texture.getNumberOfRows();
        offset.x = (float) column / texture.getNumberOfRows();
        offset.y = (float) row / texture.getNumberOfRows();
    }

    public void kill () {
        elapsedTime = lifeLength;
    }

    public Vector3f getPosition() {
        return position;
    }

    float getRotation() {
        return rotation;
    }

    public float getScale() {
        return scale;
    }

    public ParticleTexture getTexture() {
        return texture;
    }

    Vector2f getTexOffset1() {
        return texOffset1;
    }

    Vector2f getTexOffset2() {
        return texOffset2;
    }

    public float getBlend() {
        return blend;
    }

    public float getDistance() {
        return distance;
    }
}
