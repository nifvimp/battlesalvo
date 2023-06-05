package cs3500.pa04.model.model;

import java.util.List;
import java.util.Map;
import tucker.pa03.model.Coord;
import tucker.pa03.model.GameResult;
import tucker.pa03.model.LocalPlayer;
import tucker.pa03.model.Player;
import tucker.pa03.model.ShipType;

/**
 * Represents the game model for a game of Battleship.
 * Contains the user player, opponent player, and communicates between them.
 */
public class Model {
  private final LocalPlayer userPlayer;
  private final Player opponentPlayer;

  /**
   * Constructor for Model.
   *
   * @param userPlayer     the user player, a LocalPlayer since they need to have their board shown
   * @param opponentPlayer the opponent player, which can be any Player
   * @param width          the width of the game board
   * @param height         the height of the game board
   * @param specifications the ship specifications, a map of ShipType to int
   */
  public Model(LocalPlayer userPlayer, Player opponentPlayer, int width, int height,
               Map<ShipType, Integer> specifications) {
    this.userPlayer = userPlayer;
    this.opponentPlayer = opponentPlayer;
    userPlayer.setup(height, width, specifications);
    opponentPlayer.setup(height, width, specifications);
  }

  /**
   * Fires one round of shots between players.
   */
  public void salvo() {
    List<Coord> userShots = userPlayer.takeShots();
    List<Coord> opponentShots = opponentPlayer.takeShots();
    opponentPlayer.successfulHits(userPlayer.reportDamage(opponentShots));
    userPlayer.successfulHits(opponentPlayer.reportDamage(userShots));
  }

  /**
   * Gets the representation of the user player's board.
   *
   * @return the user player's board representation
   */
  public char[][] getUserBoard() {
    return userPlayer.getBoardRepresentation();
  }

  /**
   * Returns the result of shots fired by the user player.
   *
   * @return the shots fired by the user player, a Boolean array
   */
  public Boolean[][] getUserShotsFired() {
    return userPlayer.getShotsResults();
  }

  /**
   * Notifies the player that the game is over.
   *
   * @param gameResult if the player has won, lost, or forced a draw
   * @param reason the reason for the game ending
   */
  public void endGame(GameResult gameResult, String reason) {
    userPlayer.endGame(gameResult, reason);
    opponentPlayer.endGame(gameResult == GameResult.WIN ? GameResult.LOSS :
        gameResult == GameResult.LOSS ? GameResult.WIN : GameResult.TIE, reason);
  }
}
