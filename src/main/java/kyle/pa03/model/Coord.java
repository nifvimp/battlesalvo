package kyle.pa03.model;

/**
 * Represents a position on a board.
 *
 * @param x x coordinate
 * @param y y coordinate
 */
public record Coord(int x, int y) {
  @Override
  public String toString() {
    return String.format("(%s, %s)", x, y);
  }
}
