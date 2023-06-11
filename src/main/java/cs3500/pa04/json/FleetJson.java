package cs3500.pa04.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Json record containing the fleet information
 *
 * @param fleet the list of ships in the fleet
 */
public record FleetJson(
    @JsonProperty("fleet") List<ShipJson> fleet) {
}