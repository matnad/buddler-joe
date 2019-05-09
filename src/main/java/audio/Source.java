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

  public Source() {
    sourceId = alGenSources();
    alSourcef(sourceId, AL_GAIN, 1); // Volume
    rnd = new Random();
  }

  public Source(AudioMaster.SoundCategory category) {
    this();
    soundCat = category;
  }

  public void play(int buffer) {
    alSourcei(sourceId, AL_BUFFER, buffer);
    alSourcePlay(sourceId);
  }

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

  public void playIndex(int index) {
    if (soundCat != null) {
      playIndex(soundCat, index);
    } else {
      logger.warn("Source initialized without category.");
    }
    }

  public void playRandom(AudioMaster.SoundCategory category) {
    ArrayList<Integer> catBuffers = AudioMaster.getCategoryBuffers(category);
    if (catBuffers != null && catBuffers.size() > 0) {
      play(catBuffers.get(rnd.nextInt(catBuffers.size())));
    } else {
      logger.warn("Could not find sound category " + category + " or it is empty.");
    }
  }

  public void playRandom() {
    if (soundCat != null) {
      playRandom(soundCat);
    } else {
      logger.warn("Source initialized without category.");
    }
  }

  public boolean isPlaying() {
    return alGetSourcei(sourceId, AL_SOURCE_STATE) == AL_PLAYING;
  }

  public void stop() {
    alSourceStop(sourceId);
  }

  public void delete() {
    alDeleteSources(sourceId);
  }
}
