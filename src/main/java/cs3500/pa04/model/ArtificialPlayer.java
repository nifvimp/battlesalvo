package cs3500.pa04.model;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A local player of BattleSalvo, controlled by an advanced AI.
 */
public class ArtificialPlayer extends LocalPlayer {
  private final Random random;

  private final List<TargetingLine> lines = new ArrayList<>();
  private final List<Coord> potentialRoots = new ArrayList<>();

  @Override
  public String name() {
    return "Artificial Player";
  }

  /**
   * Creates a new artificial player.
   *
   * @param observer board observer of the game
   */
  public ArtificialPlayer(BoardObserver observer) {
    super(observer);
    this.random = new Random();
  }

  /**
   * Creates a new artificial player for testing.
   *
   * @param observer board observer of the game
   * @param random   random to randomize by
   */
  public ArtificialPlayer(BoardObserver observer, Random random) {
    super(observer, random);
    this.random = random;
  }

  protected List<Coord> loadShots() {
    List<Coord> shots = new ArrayList<>();
    List<Coord> validShots = new ArrayList<>(board.validShots());
    for (int i = 0; i < board.shipsLeft() && validShots.size() > 0; i++) {
      if (i < lines.size()) {
        Coord shot = lines.get(i).nextShot(validShots);
        if (shot != null) {
          validShots.remove(shot);
          shots.add(shot);
        } else {
          randomShot(validShots, shots);
        }
      } else {
       randomShot(validShots, shots);
      }
    }

    // TODO: Improve Shooting Algorithm
    return shots;
  }
  private void randomShot(List<Coord> validShots, List<Coord> shots) {
    int randomShot = random.nextInt(validShots.size());
    Coord shot = validShots.get(randomShot);
    validShots.remove(randomShot);
    shots.add(shot);
    potentialRoots.add(shot);
  }

  @Override
  public void successfulHits(List<Coord> shotsThatHitOpponentShips) {
    for (Coord hit : shotsThatHitOpponentShips) {
      board.markOpponent(hit, true);
      if (potentialRoots.contains(hit)) {
        addLines(hit);
        potentialRoots.remove(hit);
      }
    }
    lastTurnShots.removeAll(shotsThatHitOpponentShips);
    potentialRoots.clear();
    for (TargetingLine line : lines) {
      line.updateLine(lastTurnShots, board.validShots());
    }
    lines.removeIf(TargetingLine::isFinished);
    for (Coord miss : lastTurnShots) {
      board.markOpponent(miss, false);
    }
    lastTurnShots.clear();
  }

  private void addLines(Coord coord) {
    if (coord.y() - 1 >= 0 && board.validShots().contains(new Coord(coord.x(), coord.y() - 1))) {
        lines.add(new TargetingLine(coord, 0, -1, board));
    }
    if (coord.y() + 1 <= height - 1 && board.validShots().contains(new Coord(coord.x(), coord.y() + 1))) {
      lines.add(new TargetingLine(coord, 0, 1, board));
    }
    if (coord.x() + 1 <= width - 1 && board.validShots().contains(new Coord(coord.x() + 1, coord.y()))) {
      lines.add(new TargetingLine(coord, 1, 0, board));
    }
    if (coord.x() - 1 >= 0 && board.validShots().contains(new Coord(coord.x() - 1, coord.y()))) {
      lines.add(new TargetingLine(coord, -1, 0, board));
    }
  }
}
