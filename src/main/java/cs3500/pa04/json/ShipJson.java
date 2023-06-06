package cs3500.pa04.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import cs3500.pa04.model.Orientation;

@JsonPropertyOrder({"coord", "length", "direction"})
public record ShipJson(
    @JsonProperty CoordJson coord,
    @JsonProperty("length") int size,
    @JsonProperty("direction") Orientation orientation) {
}
