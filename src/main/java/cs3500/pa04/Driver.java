package cs3500.pa04;

import cs3500.pa04.controller.Controller;
import cs3500.pa04.controller.LocalController;
import cs3500.pa04.controller.InputCollector;
import cs3500.pa04.model.BoardObserver;
import cs3500.pa04.model.ArtificialPlayer;
import cs3500.pa04.model.Player;
import cs3500.pa04.view.GameView;
import cs3500.pa04.view.TerminalView;
import java.io.IOException;
import java.net.Socket;

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
    InputCollector requester = new InputCollector(view);
    BoardObserver observer = new BoardObserver();
    Player player = new ArtificialPlayer(observer);
    Player opponent = new ArtificialPlayer(observer);
    Controller controller = new LocalController(player, opponent, view, requester, observer);
    controller.run();
  }
}