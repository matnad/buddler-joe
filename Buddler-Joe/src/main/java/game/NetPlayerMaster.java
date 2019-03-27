package game;

import engine.models.TexturedModel;
import engine.render.Loader;
import engine.render.MasterRenderer;
import engine.render.objconverter.ObjFileLoader;
import engine.textures.ModelTexture;
import entities.NetPlayer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import org.joml.Vector3f;

/**
 * Represents the current lobby on the client side. Gets updated via Packets and is fully static.
 */
public class NetPlayerMaster {

  private static String lobbyname;
  private static Map<Integer, NetPlayer> netPlayers;

  private static TexturedModel defaultSkin;
  private static float defaultSize;

  static {
    netPlayers = new HashMap<>();
    defaultSize = 0.4f;
  }

  public static void init(Loader loader) {
    defaultSkin =
        new TexturedModel(
            loader.loadToVao(ObjFileLoader.loadObj("person")),
            new ModelTexture(loader.loadTexture("person")));
  }

  /**
   * Adds net players to render list. Call before rendering
   *
   * @param renderer master renderer instance
   */
  public static void update(MasterRenderer renderer) {
    for (NetPlayer netPlayer : netPlayers.values()) {
      renderer.processEntity(netPlayer);
    }
  }

  public static void addPlayer(int clientId, String username) {
    if (!netPlayers.containsKey(clientId)) {
      NetPlayer newPlayer =
          new NetPlayer(
              clientId, username, defaultSkin, new Vector3f(1000, 1000, 3), 0, 0, 0, defaultSize);
      netPlayers.put(clientId, newPlayer);
      System.out.println(NetPlayerMaster.staticToString());
    }
  }

  public static void removeMissing(ArrayList<Integer> presentIds) {
    getIds().removeIf(netId -> !presentIds.contains(netId));
    System.out.println(NetPlayerMaster.staticToString());
  }

  public static void removePlayer(int clientId) {
    netPlayers.remove(clientId);
    System.out.println(NetPlayerMaster.staticToString());
  }

  public static Set<Integer> getIds() {
    return netPlayers.keySet();
  }

  public static String getLobbyname() {
    return lobbyname;
  }

  public static void setLobbyname(String lobbyname) {
    NetPlayerMaster.lobbyname = lobbyname;
  }

  public static String staticToString() {
    StringJoiner sj = new StringJoiner(", ", "Players in my Lobby: ", ".");
    for (NetPlayer netPlayer : netPlayers.values()) {
      sj.add(netPlayer.getUsername());
    }
    return sj.toString();
  }

  public static void updatePosition(int clientId, float posX, float posY) {
    NetPlayer netPlayer = netPlayers.get(clientId);
    if (netPlayer != null) {
      netPlayer.setPosition(new Vector3f(posX, posY, netPlayer.getPosition().z));
    }
  }
}
