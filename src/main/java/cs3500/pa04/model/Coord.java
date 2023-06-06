package cs3500.pa04.model;

import cs3500.pa04.json.CoordJson;

/**
 * Record class representing one coordinate for the game.
 *
 * @param x the x position
 * @param y the y position
 */
public record Coord(int x, int y) {

  /**
   * Provides a string representation for a coordinate.
   *
   * @return the string representation
   */
  @Override
  public String toString() {
    return String.format("(%s, %s)", x, y);
  }

  public CoordJson toJson() {
    return new CoordJson(x, y);
  }
}