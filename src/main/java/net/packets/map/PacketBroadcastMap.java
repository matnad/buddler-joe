package net.packets.map;

import entities.blocks.BlockMaster;
import game.Game;
import game.map.ClientMap;
import game.map.ServerMap;
import net.packets.Packet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PacketBroadcastMap extends Packet {

  private String mapString;
  private String[] mapArray;

  private static final Logger logger = LoggerFactory.getLogger(PacketBroadcastMap.class);

  // Server sends
  public PacketBroadcastMap(ServerMap serverMap) {
    super(PacketTypes.FULL_MAP_BROADCAST);
    mapString = serverMap.toPacketString();
    setData(mapString);
  }

  // Client receives
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
    int len = mapArray[0].length();
    for (String s : mapArray) {
      if (s.length() != len) {
        addError("Invalid map data received.");
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

  @Override
  public void processData() {
    ClientMap map = Game.getMap();
    if (map == null) {
      map = new ClientMap(1, 1, 1); // Dummy map
    }
    if (!hasErrors()) {
      map.reloadMap(mapArray);
    } else {
      logger.error("Error trying to reload map: " + createErrorMessage());
    }
  }
}
