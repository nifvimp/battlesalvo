package cs3500.pa04.model;

import java.util.List;
import java.util.Set;

public class TargetingLine {
  private final Board board;
  private Coord startingCoord;

  private Coord lastShot;

  private final int offsetX;

  private final int offsetY;

  private final int width;

  private final int height;
  private boolean finished = false;

    public TargetingLine(Coord startingCoord, int offsetX, int offsetY, Board board) {
    this.startingCoord = startingCoord;
    this.lastShot = startingCoord;
    this.board = board;
    this.width = board.getOpponentKnowledge()[0].length;
    this.height = board.getOpponentKnowledge().length;
    this.offsetX = offsetX;
    this.offsetY = offsetY;
  }

  public Coord nextShot(List<Coord> validShots) {
    Coord coord = new Coord(lastShot.x() + offsetX, lastShot.y() + offsetY);
    lastShot = coord;
    if (!validShots.contains(coord)) {
      return null;
    }
    return coord;
  }

  public void updateLine(List<Coord> misses, Set<Coord> validShots) {
    if (((lastShot.x() >= width - 1 && offsetX == 1) || (lastShot.x() <= 0 && offsetX == -1) || ((lastShot.y() >= height - 1 && offsetY == -1) || (lastShot.y() <= 0 && offsetY == 1)))) {
      finished = true;
      return;
    }
    if (misses.contains(lastShot)) {
      finished = true;
      return;
    }
    Coord coord = new Coord(lastShot.x() + offsetX, lastShot.y() + offsetY);
    if (!validShots.contains(coord)) {
      finished = true;
    }
  }

  public boolean isFinished() {
    return finished;
  }

}
