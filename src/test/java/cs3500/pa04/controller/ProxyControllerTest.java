package cs3500.pa04.controller;

import static org.junit.jupiter.api.Assertions.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cs3500.pa04.Mocket;
import cs3500.pa04.TestInputStream;
import cs3500.pa04.TestOutputStream;
import cs3500.pa04.json.GameType;
import cs3500.pa04.json.MessageJson;
import cs3500.pa04.json.VolleyJson;
import cs3500.pa04.model.BoardObserver;
import cs3500.pa04.model.Coord;
import cs3500.pa04.model.GameResult;
import cs3500.pa04.model.ManualPlayer;
import cs3500.pa04.model.Player;
import cs3500.pa04.view.GameView;
import cs3500.pa04.view.TerminalView;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ProxyControllerTest {
  private static final ObjectMapper MAPPER = new ObjectMapper();
  private Mocket server;
  private TestInputStream viewInput;
  private TestOutputStream viewOutput;
  private ProxyController controller;

  @BeforeEach
  public void setup() {
    BoardObserver observer = new BoardObserver();
    viewInput = new TestInputStream();
    viewOutput = new TestOutputStream();
    GameView view = new TerminalView(viewOutput.toPrintStream(), viewInput.toReadable());
    UserCommunicator communicator = new UserCommunicator(view);
    Player player = new ManualPlayer(observer, communicator, new Random(0));
    server = new Mocket();
    try {
      controller = new ProxyController(server, player, view, observer);
    } catch (IOException e) {
      fail("Failed to connect to socket.");
    }
  }

  @Test
  public void testJoin() {
    JsonNode join = MAPPER.convertValue(
        new MessageJson("join",
            MAPPER.createObjectNode()), JsonNode.class
    );
    server.input(join);
    controller.run();
    assertEquals(
        List.of(new MessageJson(
            "join", MAPPER.createObjectNode()
            .put("name", "nifvimp")
            .put("game-type", GameType.SINGLE.name())
        )),
        server.getReceived()
    );
  }

  @Test
  public void testSetup() {
    setupMessage(1, 1, 1, 1, 1, 1);
  }

  private static JsonNode joinMessage() {
    return serializeRecord(new MessageJson("join", MAPPER.createObjectNode()));
  }

  private static JsonNode setupMessage(int width, int height, int carriers, int battleships,
                                       int destroyers, int submarines) {
    return serializeRecord(
        new MessageJson("setup",
            MAPPER.createObjectNode()
                .put("width", 10)
                .put("height", 10)
                .set("fleet-spec", MAPPER.createObjectNode()
                    .put("CARRIER", 2)
                    .put("BATTLESHIP", 2)
                    .put("DESTROYER", 2)
                    .put("SUBMARINE", 2)
                )
        ));
  }

  private static JsonNode takeShotsMessage() {
    return serializeRecord(new MessageJson("take-shots", MAPPER.createObjectNode()));
  }

  private static JsonNode reportDamageMessage(List<Coord> opponentShots) {
    VolleyJson shots = new VolleyJson(opponentShots.stream().map(Coord::toJson).toList());
    return serializeRecord(new MessageJson("report-damage", serializeRecord(shots)));
  }

  private static JsonNode successfulHitsMessage(List<Coord> successfulHits) {
    VolleyJson hits = new VolleyJson(successfulHits.stream().map(Coord::toJson).toList());
    return serializeRecord(new MessageJson("successful-hits", serializeRecord(hits)));
  }

  private static JsonNode endGameMessage(GameResult result, String reason) {
    return serializeRecord(
        new MessageJson("end-game",
        MAPPER.createObjectNode()
            .put("result", result.name())
            .put("reason", reason)
        ));
  }

  /**
   * Converts a given record object to a JsonNode.
   *
   * @param record the record to convert
   * @return the JsonNode representation of the given record
   * @throws IllegalArgumentException if the record could not be converted correctly
   */
  private static JsonNode serializeRecord(Record record) throws IllegalArgumentException {
    try {
      return MAPPER.convertValue(record, JsonNode.class);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Given record cannot be serialized");
    }
  }
}