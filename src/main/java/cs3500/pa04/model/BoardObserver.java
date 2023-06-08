package cs3500.pa04.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Observer of the boards in a game.
 */
public class BoardObserver {
  private final Map<Player, Board> gameBoards;

  public BoardObserver() {
    gameBoards = new HashMap<>();
  }

  /**
   * Registers a board to a player
   *
   * @param player      name to register the board under
   * @param playerBoard the board to register
   */
  public void registerBoard(Player player, Board playerBoard) {
    gameBoards.put(player, playerBoard);
  }

  // TODO: make return from observer view only stuff.

  /**
   * Gets the player board of this player.
   *
   * @param player name of the player
   * @return player board of player
   */
  public Board getBoard(Player player) {
    return gameBoards.get(player);
  }

  /**
   * Checks if the game is over, which is when all ships have sunk.
   *
   * @return true if the game is over
   */
  public boolean isGameOver() {
    for (Board board : gameBoards.values()) {
      if (board.shipsLeft() == 0) {
        return true;
      }
    }
    return false;
  }
}
