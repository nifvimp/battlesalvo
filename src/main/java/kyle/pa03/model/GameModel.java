package kyle.pa03.model;

import java.util.Map;

/**
 * Model of a battle salvo game.
 */
public interface GameModel {
  /**
   * Sets up the model to handle a battle salvo game with the passed in
   * specifications.
   *
   * @param height         height of the board
   * @param width          width of the board
   * @param specifications fleet specifications
   */
  void setup(int height, int width, Map<ShipType, Integer> specifications);

  /**
   * Gets the board representation of the user's board.
   *
   * @return representation of the user's board
   */
  char[][] getPlayerBoard();

  /**
   * Gets the board representation of what the user knows about the opponent's board
   *
   * @return board representation of what the user knows about the opponent's board
   */
  char[][] getOpponentBoard();

  /**
   * signals the two players to shoot at each other.
   */
  void volley();

  /**
   * Gets the result of the game from the user's perspective. If the game is not over yet,
   * it will return null.
   *
   * @return the result of the game, or null if the game has not been decided yet.
   */
  GameResult getGameResult();
}
