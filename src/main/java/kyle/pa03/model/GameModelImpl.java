package kyle.pa03.model;

import java.util.List;
import java.util.Map;

/**
 * Implementation of a Battle Salvo game model.
 */
public class GameModelImpl implements GameModel {
  private static final Map<GameResult, String> RESULT_REASONS = Map.of(
      GameResult.WIN, "Both your and opponent ships sunk.",
      GameResult.LOSE, "All your ships sunk.",
      GameResult.TIE, "All opponent ships sunk."
  );
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
    return observer.getPlayerBoard(player.name()).getBoardScene();
  }

  @Override
  public char[][] getOpponentBoard() {
    return observer.getOpponentBoard(player.name()).getBoardScene();
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
    boolean playerShipsSunk = observer.getPlayerBoard(player.name()).allShipsSunk();
    boolean opponentShipsSunk = observer.getPlayerBoard(opponent.name()).allShipsSunk();
    if (playerShipsSunk && opponentShipsSunk) {
      player.endGame(GameResult.TIE, RESULT_REASONS.get(GameResult.TIE));
      opponent.endGame(GameResult.TIE, RESULT_REASONS.get(GameResult.TIE));
      result = GameResult.TIE;
    } else if (playerShipsSunk) {
      player.endGame(GameResult.LOSE, RESULT_REASONS.get(GameResult.LOSE));
      opponent.endGame(GameResult.WIN, RESULT_REASONS.get(GameResult.WIN));
      result = GameResult.LOSE;
    } else if (opponentShipsSunk) {
      player.endGame(GameResult.WIN, RESULT_REASONS.get(GameResult.WIN));
      opponent.endGame(GameResult.LOSE, RESULT_REASONS.get(GameResult.LOSE));
      result = GameResult.WIN;
    }
    return result;
  }
}
