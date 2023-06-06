package cs3500.pa04.model;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

/**
 * An abstract local player.
 */
public abstract class LocalPlayer implements Player {
  protected Board board;
  private final BoardObserver observer;
  private final List<Coord> lastTurnShots;
  private final Random random;

  /**
   * Creates a local player.
   *
   * @param observer observer of the game
   */
  public LocalPlayer(BoardObserver observer) {
    this(observer, new Random());
  }

  /**
   * Testing constructor.
   *
   * @param observer observer of the game
   * @param random   random to randomize by
   */
  public LocalPlayer(BoardObserver observer, Random random) {
    this.lastTurnShots = new ArrayList<>();
    this.observer = observer;
    this.random = random;
  }

  /**
   * @param height         the height of the board, range: [6, 15] inclusive
   * @param width          the width of the board, range: [6, 15] inclusive
   * @param specifications a map of ship type to the number of occurrences each ship should
   *                       appear on the board
   * @return ships placed on board
   */
  @Override
  public List<Ship> setup(int height, int width, Map<ShipType, Integer> specifications) {
    List<Ship> ships = placeShips(height, width, specifications);
    board = new Board(height, width, ships);
    observer.registerBoard(name(), board);
    return ships;
  }

  /**
   * Places ships randomly.
   *
   * @param height         height of the board
   * @param width          width of the board
   * @param specifications a map of ship type to the number of occurrences each ship should
   *                       appear on the board
   * @return ships placed on board
   */
  private List<Ship> placeShips(int height, int width, Map<ShipType, Integer> specifications) {
    List<Ship> ships = new ArrayList<>();
    List<ShipType> shipsToPlace = new ArrayList<>();
    for (ShipType shipType : specifications.keySet()) {
      for (int i = 0; i < specifications.get(shipType); i++) {
        shipsToPlace.add(shipType);
      }
    }
    shipsToPlace.sort(Comparator.comparingInt(ShipType::getSize).reversed());
    int shipsPlaced = 0;
    int len = shipsToPlace.size();
    int attempts = 0;
    try {
      while (shipsPlaced < len) {
        attempts++;
        if (attempts > 10000) {
          throw new IllegalArgumentException(
              "It is impossible to setup with the given specifications."
          );
        }
        if (placeShip(shipsToPlace.get(shipsPlaced), height, width, ships)) {
          shipsPlaced++;
        } else {
          ships.remove(ships.size() - 1);
          shipsPlaced--;
        }
      }
    } catch (IndexOutOfBoundsException e) {
      throw new IllegalArgumentException(
          "It is impossible to setup with the given specifications."
      );
    }
    return ships;
  }


  /**
   * Randomly places a ship.
   *
   * @param shipType type of ship to place
   * @param height   height of the board
   * @param width    width of the board
   * @param ships    list of ships to add to
   * @return true if the random ship placement was successful
   */
  private boolean placeShip(ShipType shipType, int height, int width, List<Ship> ships) {
    Set<Coord> illegal = new HashSet<>();
    for (Ship ship : ships) {
      illegal.addAll(ship.getOccupied());
    }
    int tries = 0;
    boolean placed = false;
    while (tries < 250) {
      Orientation orientation =
          (random.nextBoolean()) ? Orientation.VERTICAL : Orientation.HORIZONTAL;
      int x = (int) (random.nextDouble()
          * (width - ((orientation == Orientation.VERTICAL) ? 0 : shipType.getSize())));
      int y = (int) (random.nextDouble()
          * (height - ((orientation == Orientation.HORIZONTAL) ? 0 : shipType.getSize())));
      Ship newShip = new Ship(shipType, new Coord(x, y), orientation);
      if (newShip.getOccupied().stream().noneMatch(illegal::contains)) {
        ships.add(newShip);
        placed = true;
        break;
      }
      tries++;
    }
    return placed;
  }

  @Override
  public List<Coord> takeShots() {
    lastTurnShots.addAll(loadShots());
    return lastTurnShots;
  }

  /**
   * Loads salvo with shots to shoot when takeShots is called.
   */
  protected abstract List<Coord> loadShots();

  @Override
  public List<Coord> reportDamage(List<Coord> opponentShotsOnBoard) {
    List<Coord> hits = new ArrayList<>();
    for (Coord shot : opponentShotsOnBoard) {
      if (board.takeDamage(shot)) {
        hits.add(shot);
      }
    }
    return hits;
  }

  @Override
  public void successfulHits(List<Coord> shotsThatHitOpponentShips) {
    for (Coord hit : shotsThatHitOpponentShips) {
      board.markOpponent(hit, true);
    }
    lastTurnShots.removeAll(shotsThatHitOpponentShips);
    for (Coord miss : lastTurnShots) {
      board.markOpponent(miss, false);
    }
    lastTurnShots.clear();
  }

  @Override
  public void endGame(GameResult result, String reason) {
    successfulHits(Collections.emptyList());
  }
}
