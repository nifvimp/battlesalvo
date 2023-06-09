package cs3500.pa04.model;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import cs3500.pa04.json.CoordJson;
import cs3500.pa04.json.ShipJson;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests the Ship class.
 */
public class ShipTest {
  private Ship ship1;
  private Ship ship2;

  /**
   * Creates new ships before each test.
   */
  @BeforeEach
  public void setup() {
    ship1 = new Ship(ShipType.DESTROYER, new Coord(0, 2), Orientation.HORIZONTAL);
    ship2 = new Ship(ShipType.SUBMARINE, new Coord(4, 1), Orientation.VERTICAL);
  }

  /**
   * Tests creating a ship and reading its type back.
   */
  @Test
  public void testGetType() {
    assertEquals(ship1.getType(), ShipType.DESTROYER);
    assertEquals(ship2.getType(), ShipType.SUBMARINE);
  }

  /**
   * Tests if a ship can correctly get damaged and sunk.
   */
  @Test
  public void testTakeDamageAndIsSunk() {
    assertFalse(ship1.isSunk());
    ship1.takeDamage(new Coord(0, 2));
    assertFalse(ship1.isSunk());
    ship1.takeDamage(new Coord(1, 2));
    assertFalse(ship1.isSunk());
    ship1.takeDamage(new Coord(1, 2));
    assertFalse(ship1.isSunk());
    ship1.takeDamage(new Coord(2, 2));
    assertFalse(ship1.isSunk());
    ship1.takeDamage(new Coord(3, 2));
    assertTrue(ship1.isSunk());
    ship1.takeDamage(new Coord(2, 2));
    assertTrue(ship1.isSunk());

    assertFalse(ship2.isSunk());
    ship2.takeDamage(new Coord(4, 2));
    assertFalse(ship2.isSunk());
    ship2.takeDamage(new Coord(4, 3));
    assertFalse(ship2.isSunk());
    ship2.takeDamage(new Coord(4, 2));
    assertFalse(ship2.isSunk());
    ship2.takeDamage(new Coord(4, 1));
    assertTrue(ship2.isSunk());
  }

  /**
   * Checks if accessing a segment not on the ship throws an exception.
   */
  @Test
  public void invalidSegmentThrows() {
    assertThrows(IllegalArgumentException.class, () -> ship1.takeDamage(new Coord(5, 8)));
    assertThrows(IllegalArgumentException.class, () -> ship1.takeDamage(new Coord(0, 3)));
    assertThrows(IllegalArgumentException.class, () -> ship2.takeDamage(new Coord(0, 3)));
    assertThrows(IllegalArgumentException.class, () -> ship2.takeDamage(new Coord(10, 0)));
    assertThrows(IllegalArgumentException.class, () -> ship2.takeDamage(new Coord(0, 10)));
  }

  /**
   * Tests if returns the correct coords the ship would occupy.
   */
  @Test
  public void testGetOccupied() {
    assertEquals(Set.of(
        new Coord(0, 2), new Coord(1, 2), new Coord(2, 2), new Coord(3, 2)),
        ship1.getOccupied());
    assertEquals(Set.of(
        new Coord(4, 1), new Coord(4, 2), new Coord(4, 3)), ship2.getOccupied());
  }

  /**
   * Tests if to json makes the json representation of a ship correctly.
   */
  @Test
  public void testToJson() {
    assertEquals(
        new ShipJson(new CoordJson(0, 2), 4, Orientation.HORIZONTAL),
        ship1.toJson());
  }

  /**
   * Tests if equals is correct.
   */
  @Test
  public void testEquals() {
    assertFalse(ship1.equals(ship2));
    assertFalse(ship1.equals(10));
    assertTrue(ship1.equals(ship1));
    assertTrue(ship1.equals(
        new Ship(ShipType.DESTROYER, new Coord(0, 2), Orientation.HORIZONTAL))
    );
    assertFalse(ship1.equals(
        new Ship(ShipType.DESTROYER, new Coord(0, 3), Orientation.HORIZONTAL))
    );
    assertFalse(ship1.equals(
        new Ship(ShipType.SUBMARINE, new Coord(0, 2), Orientation.HORIZONTAL))
    );
    assertFalse(ship1.equals(
        new Ship(ShipType.DESTROYER, new Coord(0, 2), Orientation.VERTICAL))
    );
  }

  /**
   * Tests if hash code gives same hash for the same ship.
   */
  @Test
  public void testHashCode() {
    assertNotEquals(ship1.hashCode(), ship2.hashCode());
    assertEquals(ship1.hashCode(), ship1.hashCode());
    assertEquals(ship1.hashCode(),
        new Ship(ShipType.DESTROYER,
            new Coord(0, 2),
            Orientation.HORIZONTAL)
            .hashCode()
        );
  }
}