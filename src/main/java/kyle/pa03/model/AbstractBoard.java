package kyle.pa03.model;

import java.util.HashSet;
import java.util.Set;

/**
 * An abstract Battle Salvo game board.
 */
public abstract class AbstractBoard {
  protected final Set<Coord> hits;
  protected final Set<Coord> misses;
  protected final Set<Coord> shotsLeft;
  private final int hitsToWin;
  private final Coord[][] board;

  /**
   * Creates an abstract battle salvo game board.
   *
   * @param height    height of the board
   * @param width     width of the board
   * @param hitsToWin number of distinct hits required to win the game
   */
  protected AbstractBoard(int height, int width, int hitsToWin) {
    this.board = new Coord[height][width];
    this.hitsToWin = hitsToWin;
    this.shotsLeft = new HashSet<>();
    this.hits = new HashSet<>();
    this.misses = new HashSet<>();
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        board[i][j] = new Coord(j, i);
        shotsLeft.add(board[i][j]);
      }
    }
  }

  /**
   * Gets a representation of this board.
   *
   * @return representation of this board.
   */
  public char[][] getBoardScene() {
    int height = board.length;
    int width = board[0].length;
    char[][] boardScene = new char[height][width];
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        boardScene[i][j] = translateCoord(board[i][j]);
      }
    }
    return boardScene;
  }

  /**
   * Translates a coord to a character based state of the board.
   *
   * @param coord coord to translate
   * @return character representation of coord on the board
   */
  protected abstract char translateCoord(Coord coord);

  /**
   * Gets the available shots left on the board.
   *
   * @return valid shots left
   */
  public Set<Coord> validShots() {
    return new HashSet<>(shotsLeft);
  }

  /**
   * Returns true if all ships of the board has sunk.
   *
   * @return true if all ships of the board has sunk
   */
  public boolean allShipsSunk() {
    return hits.size() >= hitsToWin;
  }
}