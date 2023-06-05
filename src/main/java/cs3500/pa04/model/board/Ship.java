package cs3500.pa04.model.board;


import cs3500.pa04.model.Coord;
import cs3500.pa04.model.ShipType;

/**
 * Represents one ship on the board.
 * Includes state such as what sections are damaged and if it is sunk.
 */
public class Ship {
  private final ShipType type;
  private final boolean[] segmentDamage;
  private final Coord startingCoord;
  private boolean isSunk = false;

  /**
   * Constructor for Ship.
   *
   * @param type the type of ship
   * @param startingCoord the coordinate the ship starts at and calculates segments based on
   */
  public Ship(ShipType type, Coord startingCoord) {
    this.type = type;
    this.startingCoord = startingCoord;
    segmentDamage = new boolean[type.getSize()];
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
   * Checks if the segment of the ship is damaged or not.
   *
   * @param coord the coordinate of the ship to check
   * @return whether the section is damaged
   */
  public boolean isSegmentDamaged(Coord coord) {
    return segmentDamage[getSegmentNum(coord)];
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
      throw new IllegalArgumentException("segment not on ship");
    }
    if (xdiff == 0) {
      return ydiff;
    } else if (ydiff == 0) {
      return xdiff;
    } else {
      throw new IllegalArgumentException("segment not on ship");
    }
  }

}
