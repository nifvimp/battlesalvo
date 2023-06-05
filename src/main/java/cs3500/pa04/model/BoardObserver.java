package cs3500.pa04.model;

import java.util.HashMap;
import java.util.Map;
import kyle.pa03.model.AbstractBoard;

/**
 * Observer of the boards in a game.
 */
public class BoardObserver {
  private final Map<String, Map<String, AbstractBoard>> gameBoards;

  public BoardObserver() {
    gameBoards = new HashMap<>();
  }

  /**
   * Registers a player board to a player
   *
   * @param player name to register the board under
   * @param playerBoard the board to register
   */
  public void registerPlayerBoard(String player, AbstractBoard playerBoard) {
    Map<String, AbstractBoard> entry = gameBoards.getOrDefault(player, new HashMap<>());
    entry.putIfAbsent("player", playerBoard);
    gameBoards.put(player, entry);
  }

  /**
   * Registers a guess board of the opponent of the player.
   *
   * @param player name to register the board under
   * @param opponentBoard the board to register
   */
  public void registerOpponentBoard(String player, AbstractBoard opponentBoard) {
    Map<String, AbstractBoard> entry = gameBoards.getOrDefault(player, new HashMap<>());
    entry.putIfAbsent("opponent", opponentBoard);
    gameBoards.put(player, entry);
  }

  /**
   * Gets the player board of this player.
   *
   * @param player name of the player
   * @return player board of player
   */
  public AbstractBoard getPlayerBoard(String player) {
    Map<String, AbstractBoard> entry = gameBoards.get(player);
    return (entry != null) ? entry.get("player") : null;
  }

  /**
   * Gets the guess board of the player's opponent.
   *
   * @param player name of the player
   * @return guess board of player's opponent
   */
  public AbstractBoard getOpponentBoard(String player) {
    Map<String, AbstractBoard> entry = gameBoards.get(player);
    return (entry != null) ? entry.get("opponent") : null;
  }
}
