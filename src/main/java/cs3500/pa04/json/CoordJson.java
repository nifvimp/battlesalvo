package cs3500.pa04.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import cs3500.pa04.model.Coord;

public record CoordJson(
    @JsonProperty int x,
    @JsonProperty int y) {
  /**
   * Converts this CoordJson to a coord object
   *
   * @return coord of this coordJson
   */
  public Coord toCoord() {
    return new Coord(x, y);
  }
}
