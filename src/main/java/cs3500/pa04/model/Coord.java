package cs3500.pa04.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Record class representing one coordinate for the game.
 *
 * @param x the x position
 * @param y the y position
 */
public record Coord(
    @JsonProperty("x") int x,
    @JsonProperty("y") int y) {

  /**
   * Provides a string representation for a coordinate.
   *
   * @return the string representation
   */
  @Override
  public String toString() {
    return String.format("(%s, %s)", x, y);
  }
}