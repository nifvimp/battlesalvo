package cs3500.pa04;

import static org.junit.jupiter.api.Assertions.fail;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cs3500.pa04.json.MessageJson;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * A mock socket.
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
   * Gets a list of json messages the mock server has received.
   *
   * @return list of json messages
   */
  public List<MessageJson> getReceived() {
    String[] received = out.toString().split(System.lineSeparator());
    List<MessageJson> parsed = new ArrayList<>();
    for (String entry : received) {
      try {
        JsonNode node = MAPPER.readValue(entry, JsonNode.class);
        parsed.add(MAPPER.convertValue(node, MessageJson.class));
      } catch (JsonProcessingException e) {
        fail(String.format("failed parsing the received input '%s'.", entry));
      }
    }
    return parsed;
  }
}