package cs3500.pa04;

import static org.junit.jupiter.api.Assertions.fail;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * A mock socket. Only deals with Jsons.
 */
public class Mocket extends Socket {
  private final static ObjectMapper MAPPER = new ObjectMapper();
  private final TestInputStream in;
  private final TestOutputStream out;

  /**
   * Makes a new mock socket.
   */
  public Mocket() {
    this.in = new TestInputStream();
    this.out = new TestOutputStream();
  }

  /**
   * Adds the given json to the input stream.
   *
   * @param json json to add
   */
  public void input(JsonNode json) {
    in.input(json.toString());
  }

  @Override
  public InputStream getInputStream() {
    return this.in;
  }

  @Override
  public OutputStream getOutputStream() {
    return this.out;
  }

  /**
   * Gets a list of all messages the mock server has received as jsons.
   *
   * @return list of json messages
   */
  public List<JsonNode> getAllReceived() {
    String[] received = out.toString().split(System.lineSeparator());
    List<JsonNode> parsed = new ArrayList<>();
    for (String entry : received) {
      try {
        parsed.add(MAPPER.readValue(entry, JsonNode.class));
      } catch (JsonProcessingException e) {
        fail(String.format("failed parsing the received input '%s'.", entry));
      }
    }
    return parsed;
  }

  /**
   * Gets the latest message the mock server has received as a json.
   *
   * @return latest message as a json
   */
  public JsonNode getLastReceived() {
    String output = out.toString().stripTrailing();
    int start = output.lastIndexOf(System.lineSeparator());
    String lastReceived = (start <= 0) ? output : output.substring(start + 2);
    JsonNode json = null;
    try {
      json = MAPPER.readValue(lastReceived, JsonNode.class);
    } catch (JsonProcessingException e) {
      e.printStackTrace();
      fail(String.format("failed parsing the received input '%s'.", lastReceived));
    }
    return json;
  }

  /**
   * Gets the earliest message the mock server has received as a json.
   *
   * @return earliest message as a json
   */
  public JsonNode getFirstReceived() {
    String output = out.toString();
    String firstReceived = output.substring(0, output.indexOf(System.lineSeparator()));
    JsonNode json = null;
    try {
      json = MAPPER.readValue(firstReceived, JsonNode.class);
    } catch (JsonProcessingException e) {
      fail(String.format("failed parsing the received input '%s'.", firstReceived));
    }
    return json;
  }
}