package cs3500.pa04.controller;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cs3500.pa04.json.CoordJson;
import cs3500.pa04.json.FleetJson;
import cs3500.pa04.json.GameType;
import cs3500.pa04.json.MessageJson;
import cs3500.pa04.json.VolleyJson;
import cs3500.pa04.model.BoardObserver;
import cs3500.pa04.model.Coord;
import cs3500.pa04.model.GameResult;
import cs3500.pa04.model.Player;
import cs3500.pa04.model.Ship;
import cs3500.pa04.model.ShipType;
import cs3500.pa04.view.GameView;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The controller for a Battle Salvo game that communicates with a server to
 * running the program.
 */
public class ProxyController implements Controller {
  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static final GameType GAME_TYPE = GameType.SINGLE;
  private static final String USERNAME = "nifvimp";
  private final BoardObserver observer;
  private final PrintStream out;
  private final InputStream in;
  private final GameView view;
  private final Socket server;
  private final Player player;

  /**
   * Construct a proxy controller.
   *
   * @param server the socket connection to the server
   * @param player the instance of the player
   * @throws IOException if IOException occurs
   */
  public ProxyController(Socket server, Player player, GameView view, BoardObserver observer)
      throws IOException {
    this.server = server;
    this.out = new PrintStream(server.getOutputStream());
    this.in = server.getInputStream();
    this.observer = observer;
    this.player = player;
    this.view = view;
  }

  /**
   * Runs the game.
   * Starts and controls the game flow.
   */
  @Override
  public void run() {
    try {
      JsonParser parser = MAPPER.getFactory().createParser(this.in);
      while (!this.server.isClosed()) {
        MessageJson message = parser.readValueAs(MessageJson.class);
        delegateMessage(message);
      }
      System.err.println("testing: server closed");
    } catch (IOException ignored) {
      // Disconnected from server or parsing exception
    }
  }

  /**
   * Determines the type of request the server has sent and delegates to the
   * corresponding helper method with the message arguments.
   *
   * @param message the MessageJSON used to determine what the server has sent
   */
  private void delegateMessage(MessageJson message) {
    String name = message.name();
    JsonNode arguments = message.arguments();
    switch (name) {
      case "join" -> handleJoin();
      case "setup" -> handleSetup(arguments);
      case "take-shots" -> handleTakeShots();
      case "report-damage" -> handleReportDamage(arguments);
      case "successful-hits" -> handleSuccessfulHits(arguments);
      case "end-game" -> endGame(arguments);
      default -> throw new IllegalStateException("Invalid message name");
    }
  }

  /**
   * Handles joining the server.
   */
  private void handleJoin() {
    MessageJson response = new MessageJson(
        "join", MAPPER.createObjectNode()
        .put("name", USERNAME)
        .put("game-type", GAME_TYPE.name())
    );
    JsonNode jsonResponse = serializeRecord(response);
    view.greet();
    this.out.println(jsonResponse);
  }

  /**
   * Handles setting up the game.
   *
   * @param arguments the json containing game information
   */
  private void handleSetup(JsonNode arguments) {
    int width = arguments.get("width").asInt();
    int height = arguments.get("height").asInt();
    JsonNode fleetSpec = arguments.get("fleet-spec");
    Map<ShipType, Integer> specifications = new HashMap<>();
    for (ShipType shipType : ShipType.values()) {
      specifications.put(shipType, fleetSpec.get(shipType.name()).asInt(0));
    }
    List<Ship> placements = player.setup(height, width, specifications);
    FleetJson response = new FleetJson(placements.stream().map(Ship::toJson).toList());
    JsonNode jsonResponse = serializeRecord(
        new MessageJson("setup",
            serializeRecord(response))
    );
    this.out.println(jsonResponse);
  }

  /**
   * Gets shots from the user.
   */
  private void handleTakeShots() {
    this.view.displayOpponentBoard(observer.getBoard(player).getOpponentKnowledge());
    this.view.displayPlayerBoard(observer.getBoard(player).getPlayerBoard());
    List<Coord> shots = player.takeShots();
    VolleyJson response = new VolleyJson(shots.stream().map(Coord::toJson).toList());
    JsonNode jsonResponse = serializeRecord(
        new MessageJson("take-shots",
            serializeRecord(response))
    );
    this.out.println(jsonResponse);
  }

  /**
   * Reports which of the opponent's shots hit.
   *
   * @param arguments Json containing the shots
   */
  private void handleReportDamage(JsonNode arguments) {
    List<Coord> shots = new ArrayList<>();
    for (JsonNode node : arguments.get("coordinates")) {
      shots.add(MAPPER.convertValue(node, CoordJson.class).toCoord());
    }
    List<Coord> successful = player.reportDamage(shots);
    VolleyJson response = new VolleyJson(successful.stream().map(Coord::toJson).toList());
    JsonNode jsonResponse = serializeRecord(
        new MessageJson("report-damage",
            serializeRecord(response))
    );
    this.out.println(jsonResponse);
  }

  /**
   * Tells the player which of their shots hit.
   *
   * @param arguments Json containing the shots
   */
  private void handleSuccessfulHits(JsonNode arguments) {
    List<Coord> shots = new ArrayList<>();
    for (JsonNode node : arguments.get("coordinates")) {
      shots.add(MAPPER.convertValue(node, CoordJson.class).toCoord());
    }
    player.successfulHits(shots);
    JsonNode jsonResponse = serializeRecord(
        new MessageJson("successful-hits", MAPPER.createObjectNode())
    );
    this.out.println(jsonResponse);
  }

  /**
   * Tells the player to end the game and why.
   *
   * @param arguments Json containing who won and the reason.
   */
  private void endGame(JsonNode arguments) {
    this.view.displayOpponentBoard(observer.getBoard(player).getOpponentKnowledge());
    this.view.displayPlayerBoard(observer.getBoard(player).getPlayerBoard());
    GameResult result = GameResult.valueOf(arguments.get("result").textValue());
    String reason = arguments.get("reason").textValue();
    view.showResults(result, reason);
    player.endGame(result, reason);
    JsonNode jsonResponse = serializeRecord(
        new MessageJson("end-game", MAPPER.createObjectNode())
    );
    this.out.println(jsonResponse);
    try {
      server.close();
    } catch (IOException e) {
      System.err.println(e.getMessage());
      // An empty catch block
    }
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
