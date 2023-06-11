package cs3500.pa04.json;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Json record with the properties used when replying to the server.
 *
 * @param name the name of the method
 * @param arguments the arguments for that method
 */
public record MessageJson(
    @JsonProperty("method-name") String name,
    @JsonProperty("arguments") JsonNode arguments) {
}
