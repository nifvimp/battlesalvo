package cs3500.pa04.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import cs3500.pa04.model.Orientation;

public record ShipJson (
  @JsonProperty CoordJson coord,
  @JsonProperty int size,
  @JsonProperty Orientation orientation) {
}
