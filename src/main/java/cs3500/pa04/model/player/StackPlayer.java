package cs3500.pa04.model.player;


import cs3500.pa04.model.BoardObserver;
import cs3500.pa04.model.Coord;
import cs3500.pa04.model.player.LocalPlayer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

/**
 * A local AI player that uses a stack to shoot efficiently.
 */
public class StackPlayer extends LocalPlayer {
  private final Stack<Coord> shotStack = new Stack<>();
  private final Random random;
  /**
   * Constructor for AiPlayer, with a given seed.
   *
   * @param observer board observer of the game
   * @param random the random seed to used to generate the board and get random shots
   */
  public StackPlayer(BoardObserver observer, Random random) {
    super(observer, random);
    this.random = random;
  }

  /**
   * Constructor for HumanPlayer, without a given seed.
   * Generates a new random if one isn't passed in.
   *
   * @param observer board observer of the game
   */
  public StackPlayer(BoardObserver observer) {
    super(observer);
    this.random = new Random();
  }

  @Override
  public String name() {
    return "Stack Player";
  }

  @Override
  public List<Coord> loadShots() {
    ArrayList<Coord> shots = new ArrayList<>();
    List<Coord> validShots = new ArrayList<>(board.validShots());
    int validSum = validShots.size();
    for (int i = Math.min(board.shipsLeft(), validSum); i > 0; i--) {
      Coord shot;
      do {
        if (!shotStack.empty()) {
          shot = shotStack.pop();
        } else {
          int randShot = random.nextInt(validShots.size());
          shot = validShots.get(randShot);
        }
      } while (!validShots.contains(shot));
      validShots.remove(shot);
      shots.add(shot);
    }
    return shots;
  }

  @Override
  public void successfulHits(List<Coord> shotsThatHitOpponentShips) {
    super.successfulHits(shotsThatHitOpponentShips);
    for (Coord coord : shotsThatHitOpponentShips) {
      addAdjacentShots(coord);
    }
  }

  /**
   * Adds any valid shot next to a coordinate to the shot stack.
   *
   * @param coord the coordinate to add around
   */
  private void addAdjacentShots(Coord coord) {
    List<Coord> coords = new ArrayList<>();
    coords.add(new Coord(coord.x() + 1, coord.y()));
    coords.add(new Coord(coord.x() - 1, coord.y()));
    coords.add(new Coord(coord.x(), coord.y() + 1));
    coords.add(new Coord(coord.x(), coord.y() - 1));
    for (Coord adjacentCoord : coords) {
      if ((adjacentCoord.x() < width && adjacentCoord.x() >= 0)
          && adjacentCoord.y() < height && adjacentCoord.y() >= 0) {
        if (board.validShots().contains(adjacentCoord)) {
          shotStack.push(adjacentCoord);
        }
      }
    }
  }

}

