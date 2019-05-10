package audio;

import static org.lwjgl.openal.AL10.AL_BUFFER;
import static org.lwjgl.openal.AL10.AL_GAIN;
import static org.lwjgl.openal.AL10.AL_PLAYING;
import static org.lwjgl.openal.AL10.AL_SOURCE_STATE;
import static org.lwjgl.openal.AL10.alDeleteSources;
import static org.lwjgl.openal.AL10.alGenSources;
import static org.lwjgl.openal.AL10.alGetSourcei;
import static org.lwjgl.openal.AL10.alSourcePlay;
import static org.lwjgl.openal.AL10.alSourceStop;
import static org.lwjgl.openal.AL10.alSourcef;
import static org.lwjgl.openal.AL10.alSourcei;

import java.util.ArrayList;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Source {

  public static final Logger logger = LoggerFactory.getLogger(Source.class);

  private int sourceId;
  private AudioMaster.SoundCategory soundCat;
  private Random rnd;

  /** Create an openAL source object. */
  public Source() {
    sourceId = alGenSources();
    alSourcef(sourceId, AL_GAIN, 1); // Volume
    rnd = new Random();
  }

  /**
   * Create and openAL source object tied to a category.
   *
   * @see AudioMaster.SoundCategory
   * @param category SoundCategory
   */
  public Source(AudioMaster.SoundCategory category) {
    this();
    soundCat = category;
  }

  /**
   * Set the volume of the source.
   *
   * @param volume Volume [0, 1]
   */
  public void setVolume(float volume) {
    alSourcef(sourceId, AL_GAIN, volume);
  }

  /**
   * Play a certain bufferId. This is category independent, but you need to know the buffer id
   * returned by {@link AudioMaster#loadSound(AudioMaster.SoundCategory, String)}. This is currently
   * not used directly since we use categories.
   *
   * @param buffer bufferId to play
   */
  public void play(int buffer) {
    alSourcei(sourceId, AL_BUFFER, buffer);
    alSourcePlay(sourceId);
  }

  /**
   * Play a specific sound in a category by index. Index is in the order the sounds were added.
   *
   * @param category SoundCategory
   * @param index Index of the sound in that category
   */
  public void playIndex(AudioMaster.SoundCategory category, int index) {
    ArrayList<Integer> catBuffers = AudioMaster.getCategoryBuffers(category);
    if (catBuffers != null && catBuffers.size() >= index) {
      play(catBuffers.get(index));
    } else {
      logger.warn(
          "Could not find sound category "
              + category
              + " or index "
              + index
              + " in this category.");
    }
  }

  /**
   * Play a specific sound by index if this source has a category associated. Must have category
   * associated, otherwise you get a warning and should use {@link
   * #playIndex(AudioMaster.SoundCategory, int)}.
   *
   * @param index Index of the sound in source sound category
   */
  public void playIndex(int index) {
    if (soundCat != null) {
      playIndex(soundCat, index);
    } else {
      logger.warn("Source initialized without category.");
    }
  }

  /**
   * Play a random sound from the specified category.
   *
   * @param category category to play from
   */
  public void playRandom(AudioMaster.SoundCategory category) {
    ArrayList<Integer> catBuffers = AudioMaster.getCategoryBuffers(category);
    if (catBuffers != null && catBuffers.size() > 0) {
      play(catBuffers.get(rnd.nextInt(catBuffers.size())));
    } else {
      logger.warn("Could not find sound category " + category + " or it is empty.");
    }
  }

  /**
   * Play a random sound form a source with a specified category. Must have category associated,
   * otherwise you get a warning and should use {@link #playRandom(AudioMaster.SoundCategory)}.
   */
  public void playRandom() {
    if (soundCat != null) {
      playRandom(soundCat);
    } else {
      logger.warn("Source initialized without category.");
    }
  }

  /**
   * Check if this source is already playing a sound.
   *
   * @return true if the source is playing a sound currently
   */
  public boolean isPlaying() {
    return alGetSourcei(sourceId, AL_SOURCE_STATE) == AL_PLAYING;
  }

  /** Stop playing a sound. Can be called even if no sound is playing. */
  public void stop() {
    alSourceStop(sourceId);
  }

  /** Clean up source. */
  public void delete() {
    alDeleteSources(sourceId);
  }
}
