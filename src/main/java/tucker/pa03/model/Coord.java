package tucker.pa03.model;

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
    return "(" + x + ", " + y + ")";
  }
}
