package cs3500.pa04.model.player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import kyle.pa03.model.BoardObserver;
import kyle.pa03.model.Coord;
import kyle.pa03.model.GameResult;
import kyle.pa03.model.GuessBoard;
import kyle.pa03.model.Player;
import kyle.pa03.model.PlayerBoard;
import kyle.pa03.model.Ship;
import kyle.pa03.model.ShipType;

/**
 * A abstract local player.
 */
public abstract class LocalPlayer1 implements Player {
  protected final BoardObserver observer;
  protected PlayerBoard playerBoard;
  protected GuessBoard opponentBoard;
  protected List<Coord> salvo;
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
    this.random = random;
    this.observer = observer;
    this.salvo = new ArrayList<>();
  }

  @Override
  public List<Ship> setup(int height, int width, Map<ShipType, Integer> specifications) {
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
    int hitsToWin = ships.stream().mapToInt(ship -> ship.shipType().getSize()).sum();
    playerBoard = new PlayerBoard(height, width, hitsToWin, ships);
    opponentBoard = new GuessBoard(height, width, hitsToWin);
    observer.registerPlayerBoard(name(), playerBoard);
    observer.registerOpponentBoard(name(), opponentBoard);
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
    while (tries < 20) {
      boolean vertical = random.nextBoolean();
      int x = (int) (random.nextDouble() * (width - ((vertical) ? 0 : shipType.getSize())));
      int y = (int) (random.nextDouble() * (height - ((vertical) ? shipType.getSize() : 0)));
      Ship newShip = new Ship(shipType, vertical, new Coord(x, y));
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
    loadShots();
    return new ArrayList<>(salvo);
  }

  /**
   * Loads salvo with shots to shoot when takeShots is called.
   */
  protected abstract void loadShots();

  @Override
  public List<Coord> reportDamage(List<Coord> opponentShotsOnBoard) {
    List<Coord> hits = new ArrayList<>();
    for (Coord shot : opponentShotsOnBoard) {
      if (playerBoard.hit(shot)) {
        hits.add(shot);
      }
    }
    return hits;
  }

  @Override
  public void successfulHits(List<Coord> shotsThatHitOpponentShips) {
    for (Coord hit : shotsThatHitOpponentShips) {
      opponentBoard.hit(hit);
    }
    salvo.removeAll(shotsThatHitOpponentShips);
    for (Coord miss : salvo) {
      opponentBoard.miss(miss);
    }
    salvo.clear();
  }

  @Override
  public void endGame(GameResult result, String reason) {
  }
}
