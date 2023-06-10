package cs3500.pa04.model;


import cs3500.pa04.json.ShipJson;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Represents one ship on the board.
 * Includes state such as what sections are damaged and if it is sunk.
 */
public class Ship {
  private final ShipType type;
  private final boolean[] segmentDamage;
  private final Coord startingCoord;
  private final Orientation orientation;
  private boolean isSunk;

  /**
   * Constructor a Ship.
   *
   * @param type          the type of ship
   * @param startingCoord the coordinate the ship starts at and calculates segments based on
   * @param orientation   the orientation of ship
   */
  public Ship(ShipType type, Coord startingCoord, Orientation orientation) {
    segmentDamage = new boolean[type.getSize()];
    this.startingCoord = startingCoord;
    this.orientation = orientation;
    this.isSunk = false;
    this.type = type;
  }

  /**
   * Gets the type of ship.
   *
   * @return the ShipType
   */
  public ShipType getType() {
    return type;
  }

  /**
   * Checks the state of the ship.
   *
   * @return whether the ship is sunk or not
   */
  public boolean isSunk() {
    return isSunk;
  }

  /**
   * Marks a segment of the ship as damaged, and the ship as sunk if all segments are damaged.
   *
   * @param coord the coordinate of the ship to damage
   * @throws IllegalArgumentException if the given coord is not a coord this ship occupies
   */
  public void takeDamage(Coord coord) {
    segmentDamage[getSegmentNum(coord)] = true;
    for (boolean damaged : segmentDamage) {
      if (!damaged) {
        return;
      }
    }
    isSunk = true;
  }

  /**
   * Gets the segment number of a ship based on the starting coordinate and coordinate specified.
   *
   * @param coord the coordinate of the ship to get
   * @return the number corresponding to the segment
   */
  private int getSegmentNum(Coord coord) {
    int xdiff = coord.x() - startingCoord.x();
    int ydiff = coord.y() - startingCoord.y();
    if (xdiff > type.getSize() - 1 || ydiff > type.getSize() - 1) {
      throw new IllegalArgumentException("segment not on ship.");
    }
    if (orientation == Orientation.VERTICAL && xdiff == 0) {
      return ydiff;
    } else if (orientation == Orientation.HORIZONTAL && ydiff == 0) {
      return xdiff;
    } else {
      throw new IllegalArgumentException("segment not on ship");
    }
  }

  /**
   * Gets the coords this ship occupies.
   *
   * @return set of coords this ship occupies
   */
  public Set<Coord> getOccupied() {
    Set<Coord> occupied = new HashSet<>();
    for (int i = 0; i < type.getSize(); i++) {
      int x = startingCoord.x() + ((orientation == Orientation.VERTICAL) ? 0 : i);
      int y = startingCoord.y() + ((orientation == Orientation.HORIZONTAL) ? 0 : i);
      occupied.add(new Coord(x, y));
    }
    return occupied;
  }

  /**
   * Converts this ship to a ShipJson.
   *
   * @return ShipJson of thisship
   */
  public ShipJson toJson() {
    return new ShipJson(startingCoord.toJson(), type.getSize(), orientation);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Ship ship)) {
      return false;
    }
    return type == ship.type && startingCoord.equals(ship.startingCoord)
        && orientation == ship.orientation;
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, startingCoord, orientation);
  }
}