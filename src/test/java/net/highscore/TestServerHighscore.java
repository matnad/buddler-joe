package net.highscore;

import org.junit.Assert;
import org.junit.Test;

public class TestServerHighscore {

  @Test
  public void checkAddFirstPlayerToHighscore() {
    ServerHighscore highscore = new ServerHighscore();
    long time = 123456789L;
    String username = "TestPlayer";
    highscore.addPlayer(time, username);
    Assert.assertEquals(
        username + "║" + util.Util.milisToString(time),
        highscore.getHighscore().get(highscore.getHighscore().size() - 1).toString());
  }

  @Test
  public void checkAdd11Players() {
    ServerHighscore highscore = new ServerHighscore();
    ServerHighscore.Standing standing1 =
        new ServerHighscore.Standing(123456789L, "testPlayer1"); // Eight
    highscore.addPlayer(standing1.time, standing1.username);
    ServerHighscore.Standing standing2 =
        new ServerHighscore.Standing(123456788L, "testPlayer2"); // Seventh
    highscore.addPlayer(standing2.time, standing2.username);
    ServerHighscore.Standing standing3 =
        new ServerHighscore.Standing(123456787L, "testPlayer3"); // Sixth
    highscore.addPlayer(standing3.time, standing3.username);
    ServerHighscore.Standing standing4 =
        new ServerHighscore.Standing(123456786L, "testPlayer4"); // Fifth
    highscore.addPlayer(standing4.time, standing4.username);
    ServerHighscore.Standing standing5 =
        new ServerHighscore.Standing(123456785L, "testPlayer1"); // Fourth
    highscore.addPlayer(standing5.time, standing5.username);
    ServerHighscore.Standing standing6 =
        new ServerHighscore.Standing(123456790L, "testPlayer3"); // Ninth
    highscore.addPlayer(standing6.time, standing6.username);
    ServerHighscore.Standing standing7 =
        new ServerHighscore.Standing(123456783L, "testPlayer4"); // Third
    highscore.addPlayer(standing7.time, standing7.username);
    ServerHighscore.Standing standing8 =
        new ServerHighscore.Standing(123456791L, "testPlayer1"); // Tenth
    highscore.addPlayer(standing8.time, standing8.username);
    ServerHighscore.Standing standing9 =
        new ServerHighscore.Standing(123456781L, "testPlayer2"); // Second
    highscore.addPlayer(standing9.time, standing9.username);
    ServerHighscore.Standing standing10 =
        new ServerHighscore.Standing(123456795L, "testPlayer3"); // Eleventh
    highscore.addPlayer(standing10.time, standing10.username);
    ServerHighscore.Standing standing11 =
        new ServerHighscore.Standing(123456712L, "testPlayer1"); // First
    highscore.addPlayer(standing11.time, standing11.username);

    String currHigh =
        standing11.username
            + "║"
            + util.Util.milisToString(standing11.time)
            + "║"
            + standing9.username
            + "║"
            + util.Util.milisToString(standing9.time)
            + "║"
            + standing7.username
            + "║"
            + util.Util.milisToString(standing7.time)
            + "║"
            + standing5.username
            + "║"
            + util.Util.milisToString(standing5.time)
            + "║"
            + standing4.username
            + "║"
            + util.Util.milisToString(standing4.time)
            + "║"
            + standing3.username
            + "║"
            + util.Util.milisToString(standing3.time)
            + "║"
            + standing2.username
            + "║"
            + util.Util.milisToString(standing2.time)
            + "║"
            + standing1.username
            + "║"
            + util.Util.milisToString(standing1.time)
            + "║"
            + standing6.username
            + "║"
            + util.Util.milisToString(standing6.time)
            + "║"
            + standing8.username
            + "║"
            + util.Util.milisToString(standing8.time);
    Assert.assertEquals(currHigh, highscore.toString());
  }
}
