package cs3500.pa04.controller;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
  private final BoardObserver observer;
  private final PrintStream out;
  private final InputStream in;
  private final GameView view;
  private final Socket server;
  private final Player player;


  // TODO: don't throw

  /**
   * Construct an proxy controller.
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

  @Override
  public void run() {
    try {
      JsonParser parser = MAPPER.getFactory().createParser(this.in);
      while (!this.server.isClosed()) {
        JsonNode message = parser.readValueAs(JsonNode.class);
        delegateMessage(message);
      }
      System.out.println("testing: server closed");
    } catch (IOException e) {
      e.printStackTrace();
      // Disconnected from server or parsing exception
    }
  }

  /**
   * Determines the type of request the server has sent and delegates to the
   * corresponding helper method with the message arguments.
   *
   * @param message the MessageJSON used to determine what the server has sent
   */
  private void delegateMessage(JsonNode message) {
    String name = message.get("method-name").textValue();
    JsonNode arguments = message.get("arguments");
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

  private void handleJoin() {
    sendMessage("join", MAPPER.createObjectNode()
        .put("name", "nifvimp")
        .put("game-type", GameType.SINGLE.name())
    );
    view.greet();
  }

  private void handleSetup(JsonNode arguments) {
    int width = arguments.get("width").asInt();
    int height = arguments.get("height").asInt();
    JsonNode fleetSpec = arguments.get("fleet-spec");
    Map<ShipType, Integer> specifications = new HashMap<>();
    for (ShipType shipType : ShipType.values()) {
      specifications.put(shipType, fleetSpec.get(shipType.name()).asInt(0));
    }
    List<Ship> placements = player.setup(height, width, specifications);
    sendMessage("setup", MAPPER.createObjectNode()
        .set("fleet", MAPPER.convertValue(placements, JsonNode.class))
    );
  }

  private void handleTakeShots() {
    this.view.displayOpponentBoard(observer.getBoard(player.name()).getOpponentKnowledge());
    this.view.displayPlayerBoard(observer.getBoard(player.name()).getPlayerBoard());
    List<Coord> shots = player.takeShots();
    sendMessage("take-shots", MAPPER.createObjectNode()
        .set("coordinates", MAPPER.convertValue(shots, JsonNode.class))
    );
  }

  private void handleReportDamage(JsonNode arguments) {
    List<Coord> shots = new ArrayList<>();
    for (JsonNode node : arguments.get("coordinates")) {
      shots.add(MAPPER.convertValue(node, Coord.class));
    }
    List<Coord> successful = player.reportDamage(shots);
    sendMessage("report-damage", MAPPER.createObjectNode()
        .set("coordinates", MAPPER.convertValue(successful, JsonNode.class))
    );
  }

  private void handleSuccessfulHits(JsonNode arguments) {
    List<Coord> shots = new ArrayList<>();
    for (JsonNode node : arguments.get("coordinates")) {
      shots.add(MAPPER.convertValue(node, Coord.class));
    }
    player.successfulHits(shots);
    sendMessage("successful-hits", MAPPER.createObjectNode());
  }

  private void endGame(JsonNode arguments) {
    this.view.displayOpponentBoard(observer.getBoard(player.name()).getOpponentKnowledge());
    this.view.displayPlayerBoard(observer.getBoard(player.name()).getPlayerBoard());
    GameResult result = GameResult.valueOf(arguments.get("result").textValue());
    String reason = arguments.get("reason").textValue();
    view.showResults(result, reason);
    player.endGame(result, reason);
    sendMessage("end-game", MAPPER.createObjectNode());
    try {
      server.close();
    } catch (IOException e) {
      System.err.println(e.getMessage());
      // An empty catch block
    }
  }

  /**
   * Sends a response back to the server.
   *
   * @param methodName method-name
   * @param arguments arguments
   */
  private void sendMessage(String methodName, JsonNode arguments) {
    this.out.println(MAPPER.createObjectNode()
        .put("method-name", methodName)
        .set("arguments", arguments)
    );
  }
}
