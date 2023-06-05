package cs3500.pa04.model.board;


import cs3500.pa04.model.Coord;

/**
 * A board that represents what a player knows about their opponent's board.
 */
public class GuessBoard extends AbstractBoard {
  /**
   * Makes a new guess board.
   *
   * @param height    height of the board
   * @param width     width of the board
   * @param hitsToWin number of distinct hits required to win the game
   */
  public GuessBoard(int height, int width, int hitsToWin) {
    super(height, width, hitsToWin);
  }

  @Override
  protected char translateCoord(Coord coord) {
    if (hits.contains(coord)) {
      return 'H';
    } else if (misses.contains(coord)) {
      return 'M';
    } else {
      return '-';
    }
  }

  /**
   * Mark a coord on the board as a hit.
   *
   * @param coord position to mark
   */
  public void hit(Coord coord) {
    shotsLeft.remove(coord);
    hits.add(coord);
  }

  /**
   * Mark a coord on the board as a miss.
   *
   * @param coord position to mark
   */
  public void miss(Coord coord) {
    shotsLeft.remove(coord);
    misses.add(coord);
  }
}
