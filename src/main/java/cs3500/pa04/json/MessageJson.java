package cs3500.pa04.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public record MessageJson(
    @JsonProperty("method-name") String name,
    @JsonProperty("arguments") JsonNode arguments) {
}
