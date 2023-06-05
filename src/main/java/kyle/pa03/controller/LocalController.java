package kyle.pa03.controller;

import cs3500.pa03.model.BoardObserver;
import cs3500.pa03.model.Coord;
import cs3500.pa03.model.GameModel;
import cs3500.pa03.model.GameModelImpl;
import cs3500.pa03.model.GameResult;
import cs3500.pa03.model.Player;
import cs3500.pa03.model.ShipType;
import cs3500.pa03.view.GameView;
import java.util.Map;

/**
 * The controller for a Battle Salvo game that hosted locally on the computer that is
 * running the program.
 */
public class LocalController implements Controller {
  private final GameView view;
  private final GameModel model;
  private final UserRequester requester;

  /**
   * Makes a new local battle salvo controller.
   *
   * @param player    user
   * @param opponent  user's opponent
   * @param view      view of the game
   * @param requester prompter that gets user input
   * @param observer  board observer of all players
   */
  public LocalController(Player player, Player opponent, GameView view, UserRequester requester,
                         BoardObserver observer) {
    this.model = new GameModelImpl(observer, player, opponent);
    this.view = view;
    this.requester = requester;
  }

  @Override
  public void run() {
    Coord boardSize = requester.requestBoardSize();
    int maxShips = Math.min(boardSize.x(), boardSize.y());
    Map<ShipType, Integer> specification = requester.requestFleet(maxShips);
    model.setup(boardSize.x(), boardSize.y(), specification);
    while (model.getGameResult() == null) {
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
      case TIE -> reason = "Both your and opponent ships sunk.";
      default -> reason = "an unexpected error has occurred.";
    }
    view.showResults(result, reason);
  }
}
