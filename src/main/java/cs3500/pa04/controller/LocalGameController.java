package cs3500.pa04.controller;

import cs3500.pa04.model.Coord;
import cs3500.pa04.model.GameResult;
import cs3500.pa04.model.model.Model;
import cs3500.pa04.model.Player;
import cs3500.pa04.model.player.AiPlayer;
import cs3500.pa04.model.player.HumanPlayer;
import cs3500.pa04.model.player.LocalPlayer;
import cs3500.pa04.view.GameView;
import cs3500.pa04.view.TerminalView;
import java.io.PrintStream;
import java.util.Random;

/**
 * Manages a single local game of Battleship.
 */
public class LocalGameController implements Controller {
  private final Model model;
  private final GameView view;

  private static final int MIN_DIMENSIONS = 6;
  private static final int MAX_DIMENSIONS = 15;

  private final boolean[] didUserLost = { false };

  private final boolean[] didOpponentLose = { false };

  private boolean gameOver = false;

  /**
   * Constructor for LocalGameController without a given seed.
   */
  public LocalGameController(Readable input, PrintStream output) {
    this(input, output, new Random(), new Random());
  }

  /**
   * Constructor for LocalGameController with a given seed.
   *
   * @param input a Readable object to take user input from
   * @param output a PrintStream object to print the output to
   * @param random1 a Random object to use as a player seed
   * @param random2 a Random object to use as a player seed
   */

  public LocalGameController(Readable input, PrintStream output, Random random1, Random random2) {
    view = new TerminalView(output, input);
    InputCollector inputCollector = new InputCollector(view);
    LocalPlayer userPlayer = new HumanPlayer(didUserLost, random1, inputCollector);
    Player opponentPlayer = new AiPlayer(didOpponentLose, random2);
    //LocalPlayer userPlayer = new AiPlayer(didUserLost, random1);
    view.greet();
    Coord boardParameters = inputCollector.getParameters(MIN_DIMENSIONS, MAX_DIMENSIONS);
    model = new Model(userPlayer, opponentPlayer, boardParameters.x(), boardParameters.y(),
        inputCollector.getFleet(Math.min(boardParameters.x(), boardParameters.y())));
  }

  /**
   * Runs the game loop.
   * Displays the game state, passes the shots between both players, checks if the game is over.
   */
  public void run() {
    view.displayOpponentBoard(model.getUserShotsFired());
    view.displayPlayerBoard(model.getUserBoard());
    do {
      model.salvo();
      view.displayOpponentBoard(model.getUserShotsFired());
      view.displayPlayerBoard(model.getUserBoard());
      checkGameOver();
    } while (!gameOver);
  }

  /**
   * Ends the game with the specified result and reason.
   *
   * @param gameResult the result of the game
   * @param reason the reason for the game ending
   */
  private void endGame(GameResult gameResult, String reason) {
    gameOver = true;
    model.endGame(gameResult, reason);
    view.showResults(gameResult, reason);
  }

  /**
   * Checks if either player has lost and ends the game if they have.
   */
  private void checkGameOver() {
    GameResult gameResult;
    String reason;
    if (didUserLost[0] || didOpponentLose[0]) {
      if (didUserLost[0] && didOpponentLose[0]) {
        gameResult = GameResult.TIE;
        reason = "both players ran out ships";
      } else if (didOpponentLose[0]) {
        gameResult = GameResult.WIN;
        reason = "the opponent ran out of ships";
      } else {
        gameResult = GameResult.LOSE;
        reason = "you ran out of ships";
      }
      endGame(gameResult, reason);
    }

  }

}
