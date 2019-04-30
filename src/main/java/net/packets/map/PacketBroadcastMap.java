package net.packets.map;

import entities.blocks.BlockMaster;
import game.Game;
import game.map.ClientMap;
import game.map.ServerMap;
import java.util.Arrays;
import net.packets.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Packet to send the full map from server to client. */
public class PacketBroadcastMap extends Packet {

  private static final Logger logger = LoggerFactory.getLogger(PacketBroadcastMap.class);
  private long seed;
  private String mapString;
  private String[] mapArray;

  /**
   * The server prepares to send a serverMap to the client.
   *
   * @param serverMap map to send
   */
  public PacketBroadcastMap(ServerMap serverMap) {
    super(PacketTypes.FULL_MAP_BROADCAST);
    mapString = serverMap.toPacketString();
    seed = serverMap.getSeed();
    System.out.println(seed);
    setData(seed + "║" + mapString);
  }

  /**
   * The client receives the full map from the server and prepares to process it.
   *
   * @param data id for every block on the map
   */
  public PacketBroadcastMap(String data) {
    super(PacketTypes.FULL_MAP_BROADCAST);
    setData(data);
    mapString = getData();
    if (data == null) {
      addError("No data received.");
      return;
    }
    mapArray = data.split("║");
    validate();
  }

  @Override
  public void validate() {

    if (mapArray.length < 2) {
      addError("Map data is incomplete.");
      return;
    }

    // Store seed and remove it from the map array
    try {
      seed = Integer.parseInt(mapArray[0]);
    } catch (NumberFormatException e) {
      addError("Invalid map seed.");
    }
    mapArray = Arrays.copyOfRange(mapArray, 1, mapArray.length);

    int len = mapArray[0].length();
    for (String s : mapArray) {
      if (s.length() != len) {
        addError("Invalid map data received: " + s);
        return;
      }
      // Each line can only contain numbers
      for (int i = 0; i < s.length(); i++) {
        int val = Character.getNumericValue(s.charAt(i));
        if (val > BlockMaster.BlockTypes.values().length - 1) {
          addError("Wrong map format: " + val);
          addError(s);
          return;
        }
      }
    }
  }

  /**
   * The client will try to reload the map to the map he just received. Logic is in {@link
   * ClientMap}.
   *
   * @see ClientMap
   */
  @Override
  public void processData() {
    ClientMap map = Game.getMap();
    if (map == null) {
      map = new ClientMap(1, 1, -1); // Dummy map with right seed
      Game.setMap(map);
    }
    if (!hasErrors()) {
      map.setLobbyMap(mapArray);
      map.setSeed(seed);
    } else {
      logger.error(
          "Error trying to reload map: " + createErrorMessage() + "\n" + Arrays.toString(mapArray));
    }
  }
}
