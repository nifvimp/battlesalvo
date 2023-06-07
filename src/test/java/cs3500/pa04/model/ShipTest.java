package cs3500.pa04.model;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Set;
import org.junit.jupiter.api.Test;

/**
 * Tests the Ship class.
 */
class ShipTest {
  Ship ship1 = new Ship(ShipType.DESTROYER, new Coord(0, 2), Orientation.HORIZONTAL);
  Ship ship2 = new Ship(ShipType.SUBMARINE, new Coord(4, 1), Orientation.VERTICAL);

  /**
   * Tests if returns the correct coords the ship would occupy.
   */
  @Test
  void getOccupied() {
    assertEquals(Set.of(
        new Coord(0, 2), new Coord(1, 2), new Coord(2, 2), new Coord(3, 2)),
        ship1.getOccupied());
    assertEquals(Set.of(
        new Coord(4, 1), new Coord(4, 2), new Coord(4, 3)), ship2.getOccupied());
  }
}