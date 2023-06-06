package cs3500.pa04.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public record MessageJson(
    @JsonProperty String name,
    @JsonProperty JsonNode arguments) {
}
