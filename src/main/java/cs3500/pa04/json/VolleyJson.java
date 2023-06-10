package cs3500.pa04.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * Json record containing shots for a volley
 *
 * @param coordinates the list of shots
 */
public record VolleyJson(
    @JsonProperty("coordinates") List<CoordJson> coordinates) {
}
