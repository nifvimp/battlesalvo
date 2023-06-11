package cs3500.pa04.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import cs3500.pa04.model.Orientation;

/**
 * Json record representing one Ship.
 *
 * @param coord the starting coordinate of the ship
 * @param size the length of the ship
 * @param orientation which direction the ship is facing
 */
@JsonPropertyOrder({"coord", "length", "direction"})
public record ShipJson(
    @JsonProperty("coord") CoordJson coord,
    @JsonProperty("length") int size,
    @JsonProperty("direction") Orientation orientation) {
}