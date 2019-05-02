package util;

import java.text.SimpleDateFormat;
import java.util.Date;

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

  /**
   * Get the current date formatted for chat.
   *
   * @return string of the current timestamp
   */
  public static String getFormattedTimestamp() {
    SimpleDateFormat simpleFormat = new SimpleDateFormat("HH:mm");
    Date date = new Date();
    return simpleFormat.format(date);
  }
}
