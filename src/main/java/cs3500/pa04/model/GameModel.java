package cs3500.pa04.model;

import java.util.List;
import java.util.Map;

/**
 * Implementation of a Battle Salvo game model.
 */
public class GameModel {
  private final BoardObserver observer;
  private final Player player;
  private final Player opponent;

  /**
   * Creates a new game model object.
   *
   * @param observer board observer for game
   * @param player   user
   * @param opponent opponent
   */
  public GameModel(BoardObserver observer, Player player, Player opponent) {
    this.player = player;
    this.opponent = opponent;
    this.observer = observer;
  }

  /**
   * Sets up the model to handle a battle salvo game with the passed in
   * specifications.
   *
   * @param height         height of the board
   * @param width          width of the board
   * @param specifications fleet specifications
   */
  public void setup(int height, int width, Map<ShipType, Integer> specifications) {
    player.setup(height, width, specifications);
    opponent.setup(height, width, specifications);
  }

  /**
   * Gets the board representation of the user's board.
   *
   * @return representation of the user's board
   */
  public char[][] getPlayerBoard() {
    return observer.getBoard(player).getPlayerBoard();
  }

  /**
   * Gets the board representation of what the user knows about the opponent's board
   *
   * @return board representation of what the user knows about the opponent's board
   */
  public char[][] getOpponentBoard() {
    return observer.getBoard(player).getOpponentKnowledge();
  }

  /**
   * signals the two players in the model to shoot at each other.
   */
  public void volley() {
    List<Coord> playerShots = player.takeShots();
    List<Coord> opponentShots = opponent.takeShots();
    List<Coord> hitsOnPlayer = player.reportDamage(opponentShots);
    List<Coord> hitsOnOpponent = opponent.reportDamage(playerShots);
    player.successfulHits(hitsOnOpponent);
    opponent.successfulHits(hitsOnPlayer);
  }

  /**
   * Gets the result of the game from the user's perspective.
   *
   * @return the result of the game.
   * @throws IllegalStateException if the game has not been decided yet
   */
  public GameResult getGameResult() {
    boolean playerShipsSunk = observer.getBoard(player).shipsLeft() <= 0;
    boolean opponentShipsSunk = observer.getBoard(opponent).shipsLeft() <= 0;
    if (playerShipsSunk && opponentShipsSunk) {
      return GameResult.DRAW;
    } else if (playerShipsSunk) {
      return GameResult.LOSE;
    } else if (opponentShipsSunk) {
      return GameResult.WIN;
    } else {
      throw new IllegalStateException("The game has not ended yet.");
    }
  }

  /**
   * Tells the players in the game that the game is over.
   *
   * @param result game result in the perspective of the first player
   * @param reason reason for the end of the game
   */
  public void endGame(GameResult result, String reason) {
    player.endGame(result, reason);
    opponent.endGame(result == GameResult.WIN ? GameResult.LOSE :
        result == GameResult.LOSE ? GameResult.WIN : GameResult.DRAW, reason);
  }
}
