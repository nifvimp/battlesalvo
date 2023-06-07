package cs3500.pa04.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record FleetJson(
    @JsonProperty("fleet") List<ShipJson> fleet) {
}