package cs3500.pa04.model;

import java.util.List;
import java.util.Map;

/**
 * Implementation of a Battle Salvo game model.
 */
public class GameModelImpl implements GameModel {
  private final BoardObserver observer;
  private final Player player;
  private final Player opponent;
  private GameResult result = null;

  /**
   * Creates a new game model object.
   *
   * @param observer board observer for game
   * @param player   user
   * @param opponent opponent
   */
  public GameModelImpl(BoardObserver observer, Player player, Player opponent) {
    this.player = player;
    this.opponent = opponent;
    this.observer = observer;
  }

  @Override
  public void setup(int height, int width, Map<ShipType, Integer> specifications) {
    player.setup(height, width, specifications);
    opponent.setup(height, width, specifications);
  }

  @Override
  public char[][] getPlayerBoard() {
    return observer.getBoard(player).getPlayerBoard();
  }

  @Override
  public char[][] getOpponentBoard() {
    return observer.getBoard(player).getOpponentKnowledge();
  }

  @Override
  public void volley() {
    List<Coord> playerShots = player.takeShots();
    List<Coord> opponentShots = opponent.takeShots();
    List<Coord> hitsOnPlayer = player.reportDamage(opponentShots);
    List<Coord> hitsOnOpponent = opponent.reportDamage(playerShots);
    player.successfulHits(hitsOnOpponent);
    opponent.successfulHits(hitsOnPlayer);
  }

  @Override
  public GameResult getGameResult() {
    boolean playerShipsSunk = observer.getBoard(player).shipsLeft() <= 0;
    boolean opponentShipsSunk = observer.getBoard(opponent).shipsLeft() <= 0;
    if (playerShipsSunk && opponentShipsSunk) {
      result = GameResult.DRAW;
    } else if (playerShipsSunk) {
      result = GameResult.LOSE;
    } else if (opponentShipsSunk) {
      result = GameResult.WIN;
    }
    return result;
  }

  @Override
  public void endGame(GameResult result, String reason) {
    player.endGame(result, reason);
    opponent.endGame(result == GameResult.WIN ? GameResult.LOSE :
        result == GameResult.LOSE ? GameResult.WIN : GameResult.DRAW, reason);
  }
}
