package cs3500.pa04.controller;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cs3500.pa04.Mocket;
import cs3500.pa04.TestInputStream;
import cs3500.pa04.TestOutputStream;
import cs3500.pa04.json.FleetJson;
import cs3500.pa04.json.GameType;
import cs3500.pa04.json.MessageJson;
import cs3500.pa04.json.VolleyJson;
import cs3500.pa04.model.BoardObserver;
import cs3500.pa04.model.Coord;
import cs3500.pa04.model.GameResult;
import cs3500.pa04.model.player.ManualPlayer;
import cs3500.pa04.model.Orientation;
import cs3500.pa04.model.Player;
import cs3500.pa04.model.Ship;
import cs3500.pa04.model.ShipType;
import cs3500.pa04.view.GameView;
import cs3500.pa04.view.TerminalView;
import java.io.IOException;
import java.io.PrintStream;
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
  public void runHandlesClosedSocket() {
    PrintStream stdErr = System.err;
    System.setErr(viewOutput.toPrintStream());
    try {
      server.close();
    } catch (IOException ignored) {
      // An empty catch block
    }
    assertDoesNotThrow(() -> controller.run());
    assertEquals(viewOutput.toString(), "testing: server closed" + System.lineSeparator());
    System.setErr(stdErr);
  }

  @Test
  public void testJoin() {
    server.input(emptyRequest("join"));
    controller.run();
    assertEquals(
        serializeRecord(new MessageJson(
            "join", MAPPER.createObjectNode()
            .put("name", "nifvimp")
            .put("game-type", GameType.SINGLE.name())
        )), server.getFirstReceived());
  }

  @Test
  public void testSetup() {
    server.input(setupRequest(6, 6, 1, 2, 2, 1));
    controller.run();
    assertEquals(
        setupResponse(List.of(
            new Ship(ShipType.CARRIER, new Coord(4, 0), Orientation.VERTICAL),
            new Ship(ShipType.BATTLESHIP, new Coord(5, 0), Orientation.VERTICAL),
            new Ship(ShipType.BATTLESHIP, new Coord(0, 0), Orientation.VERTICAL),
            new Ship(ShipType.DESTROYER, new Coord(3, 0), Orientation.VERTICAL),
            new Ship(ShipType.DESTROYER, new Coord(2, 1), Orientation.VERTICAL),
            new Ship(ShipType.SUBMARINE, new Coord(1, 2), Orientation.VERTICAL)
        )), server.getLastReceived());
  }

  @Test
  public void testTakeShots() {
    server.input(setupRequest(6, 6, 1, 1, 1, 1));
    server.input(emptyRequest("take-shots"));
    viewInput.input("""
        0 0
        0 1
        0 2
        0 3
        """);
    controller.run();
    assertEquals(volleyMessage("take-shots", List.of(
        new Coord(0, 0), new Coord(0, 1), new Coord(0, 2), new Coord(0, 3)
    )), server.getLastReceived());
  }

  @Test
  public void testReportDamage() {
    server.input(setupRequest(6, 6, 1, 1, 1, 1));
    server.input(volleyMessage("report-damage", List.of(
        new Coord(0, 0), new Coord(0, 1), new Coord(0, 2), new Coord(0, 3)
    )));
    controller.run();
    assertEquals(volleyMessage("report-damage", List.of(
        new Coord(0, 2)
    )), server.getLastReceived());
  }

  @Test
  public void testSuccessfulHits() {
    server.input(setupRequest(6, 6, 1, 1, 1, 1));
    server.input(volleyMessage("successful-hits", List.of(
        new Coord(0, 0), new Coord(0, 1)
    )));
    controller.run();
    assertEquals(emptyRequest("successful-hits"), server.getLastReceived());
  }

  @Test
  public void testEndGame() {
    server.input(setupRequest(6, 6, 1, 1, 1, 1));
    server.input(endGameMessage(GameResult.WIN, "You Won!"));
    server.input(endGameMessage(GameResult.LOSE, "You Lost."));
    server.input(endGameMessage(GameResult.DRAW, "You Tied."));
    controller.run();
    assertEquals(2, server.getAllReceived().size());
    assertEquals(emptyRequest("end-game"), server.getLastReceived());
    assertTrue(server.isClosed());

  }

  private static JsonNode emptyRequest(String methodName) {
    return serializeRecord(new MessageJson(methodName, MAPPER.createObjectNode()));
  }

  private static JsonNode setupRequest(int width, int height, int carriers, int battleships,
                                       int destroyers, int submarines) {
    return serializeRecord(
        new MessageJson("setup",
            MAPPER.createObjectNode()
                .put("width", width)
                .put("height", height)
                .set("fleet-spec", MAPPER.createObjectNode()
                    .put("CARRIER", carriers)
                    .put("BATTLESHIP", battleships)
                    .put("DESTROYER", destroyers)
                    .put("SUBMARINE", submarines)
                )
        ));
  }

  private static JsonNode setupResponse(List<Ship> ships) {
    return serializeRecord(
        new MessageJson("setup",
            serializeRecord(new FleetJson(ships.stream().map(Ship::toJson).toList()))
        ));
  }

  private static JsonNode volleyMessage(String methodName, List<Coord> coordinates) {
    VolleyJson coords = new VolleyJson(coordinates.stream().map(Coord::toJson).toList());
    return serializeRecord(new MessageJson(methodName, serializeRecord(coords)));
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