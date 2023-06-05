package kyle.pa03.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a ship.
 *
 * @param shipType type of the ship
 * @param vertical if the ship is position vertically
 * @param start    position of the top left occupied cell of the ship
 */
public record Ship(ShipType shipType, boolean vertical, Coord start) {
  /**
   * Gets the coords this ship occupies.
   *
   * @return set of coords this ship occupies
   */
  public Set<Coord> getOccupied() {
    Set<Coord> occupied = new HashSet<>();
    for (int i = 0; i < shipType.getSize(); i++) {
      int x = start.x() + ((vertical) ? 0 : i);
      int y = start.y() + ((vertical) ? i : 0);
      occupied.add(new Coord(x, y));
    }
    return occupied;
  }
}