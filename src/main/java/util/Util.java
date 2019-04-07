package util;

public class Util {

  /**
   * Convert a number of miliseconds to the minutes:seconds:milis format.
   *
   * @param time a number of miliseconds
   * @return a formatted time string
   */
  public static String milisToString(long time) {
    long millis = time % 1000;
    long second = (time / 1000) % 60;
    long minute = (time / (1000 * 60)) % 60;
    return String.format("%02d:%02d.%d", minute, second, millis);
  }
}
