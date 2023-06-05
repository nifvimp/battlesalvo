package cs3500.pa04.model.board;

import cs3500.pa04.model.Coord;
import cs3500.pa04.model.ShipType;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a ship.
 *
 * @param shipType type of the ship
 * @param vertical if the ship is position vertically
 * @param start    position of the top left occupied cell of the ship
 */
public record Ship1(ShipType shipType, boolean vertical, Coord start) {
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