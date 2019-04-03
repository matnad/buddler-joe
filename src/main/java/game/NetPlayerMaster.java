package game;

import engine.models.TexturedModel;
import engine.render.Loader;
import engine.render.MasterRenderer;
import engine.render.objconverter.ObjFileLoader;
import engine.textures.ModelTexture;
import entities.NetPlayer;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;
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
    lobbyname = "";
    netPlayers = new ConcurrentHashMap<>();
    defaultSize = 0.4f;
  }

  /**
   * Initialize once at the start to load textures.
   *
   * @param loader main loader
   */
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
      float pctBrightness = Game.getMap().getLightLevel(netPlayer.getPosition().y);
      if (pctBrightness > .7f) {
        netPlayer.turnHeadlightOff();
      } else {
        netPlayer.turnHeadlightOn();
      }
      netPlayer.updateNameplate(); // Kinda expensive?
      renderer.processEntity(netPlayer);
    }
  }

  /**
   * Adds a new player object that is linked to a client on the server. Will mainly be used by the
   * lobby overview packet that sends updates for new players.
   *
   * @param clientId server-side client id
   * @param username username of the player
   */
  public static void addPlayer(int clientId, String username) {
    System.out.println("adding " + username);
    if (!netPlayers.containsKey(clientId)) {
      NetPlayer newPlayer =
          new NetPlayer(
              clientId, username, defaultSkin, new Vector3f(1000, 1000, 3), 0, 0, 0, defaultSize);
      netPlayers.put(clientId, newPlayer);
      System.out.println(NetPlayerMaster.staticToString());
    }
  }

  /**
   * Checks if a player left the lobby and removes the player from the game. Mainly called by the
   * lobby overview packet that will send a full list of all players.
   *
   * @param presentIds list of all currently connected (to the lobby) players by their client id
   */
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

  /**
   * String representation of the Game Lobby.
   *
   * @return string with all players in the lobby excluding the player
   */
  public static String staticToString() {
    StringJoiner sj = new StringJoiner(", ", "Players in my Lobby: ", "");
    for (NetPlayer netPlayer : netPlayers.values()) {
      sj.add(netPlayer.getUsername());
    }
    return sj.toString();
  }

  /**
   * Updates the position of a connected player. Called by {@link net.packets.playerprop.PacketPos}
   * to propagate player movement.
   *
   * @param clientId player to update
   * @param posX new X position
   * @param posY new Y position
   * @param rotY new Y rotation
   */
  public static void updatePosition(int clientId, float posX, float posY, float rotY) {
    NetPlayer netPlayer = netPlayers.get(clientId);
    if (netPlayer != null) {
      netPlayer.setPosition(new Vector3f(posX, posY, netPlayer.getPosition().z));
      netPlayer.setRotY(rotY);
    }
  }

  public static NetPlayer getNetPlayerById(int clientId) {
    if (clientId == Game.getActivePlayer().getClientId()) {
      return Game.getActivePlayer();
    } else {
      return netPlayers.get(clientId);
    }
  }
}
