package cs3500.pa04.controller;


import cs3500.pa04.model.BoardObserver;
import cs3500.pa04.model.Coord;
import cs3500.pa04.model.GameModel;
import cs3500.pa04.model.GameResult;
import cs3500.pa04.model.Player;
import cs3500.pa04.model.ShipType;
import cs3500.pa04.view.GameView;
import java.util.Map;

/**
 * The controller for a Battle Salvo game that hosted locally on the computer that is
 * running the program.
 */
public class LocalController implements Controller {
  private final UserCommunicator communicator;
  private final BoardObserver observer;
  private final GameView view;
  private final GameModel model;

  /**
   * Makes a new local battle salvo controller.
   *
   * @param player       user
   * @param opponent     user's opponent
   * @param view         view of the game
   * @param communicator prompter that gets user input
   * @param observer     board observer of all players
   */
  public LocalController(Player player, Player opponent, GameView view,
                         UserCommunicator communicator,
                         BoardObserver observer) {
    this.model = new GameModel(observer, player, opponent);
    this.communicator = communicator;
    this.observer = observer;
    this.view = view;
  }

  @Override
  public void run() {
    view.greet();
    Coord boardSize = communicator.requestBoardDimensions();
    int maxShips = Math.min(boardSize.x(), boardSize.y());
    Map<ShipType, Integer> specification = communicator.requestFleet(maxShips);
    model.setup(boardSize.x(), boardSize.y(), specification);
    while (!observer.isGameOver()) {
      view.displayOpponentBoard(model.getOpponentBoard());
      view.displayPlayerBoard(model.getPlayerBoard());
      model.volley();
    }
    view.displayOpponentBoard(model.getOpponentBoard());
    view.displayPlayerBoard(model.getPlayerBoard());
    endGame(model.getGameResult());
  }

  /**
   * Chooses a reason for why the game ended based on the result
   * and runs end game logic.
   *
   * @param result result of the game
   */
  private void endGame(GameResult result) {
    String reason;
    switch (result) {
      case WIN -> reason = "All opponent ships sunk.";
      case LOSE -> reason = "All your ships sunk.";
      case DRAW -> reason = "Both your and opponent ships sunk.";
      default -> reason = "an unexpected error has occurred.";
    }
    model.endGame(result, reason);
    view.showResults(result, reason);
  }
}
