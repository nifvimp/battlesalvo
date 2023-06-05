package kyle.pa03;


import cs3500.pa03.controller.Controller;
import cs3500.pa03.controller.LocalController;
import cs3500.pa03.controller.UserRequester;
import cs3500.pa03.model.ArtificialPlayer;
import cs3500.pa03.model.BoardObserver;
import cs3500.pa03.model.ManualPlayer;
import cs3500.pa03.model.Player;
import cs3500.pa03.view.GameView;
import cs3500.pa03.view.TerminalView;

/**
 * This is the main driver of this project.
 */
public class Driver {
  /**
   * Project entry point.
   *
   * @param args - no command line args required
   */
  public static void main(String[] args) {
    GameView view = new TerminalView();
    UserRequester requester = new UserRequester(view);
    BoardObserver observer = new BoardObserver();
    Player player = new ManualPlayer(observer, requester);
    Player opponent = new ArtificialPlayer(observer);
    Controller controller = new LocalController(player, opponent, view, requester, observer);
    controller.run();
  }
}