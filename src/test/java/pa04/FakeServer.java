package pa04;

import static org.junit.jupiter.api.Assertions.fail;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class FakeServer extends Socket {
  private final static ObjectMapper MAPPER = new ObjectMapper();
  private final TestInputStream in;
  private final TestOutputStream out;

  public FakeServer() {
    this.in = new TestInputStream();
    this.out = new TestOutputStream();
  }

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

  public List<JsonNode> getReceived() {
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
}
