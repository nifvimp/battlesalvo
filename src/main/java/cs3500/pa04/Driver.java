package cs3500.pa04;

import cs3500.pa04.controller.Controller;
import cs3500.pa04.controller.LocalController;
import cs3500.pa04.controller.ProxyController;
import cs3500.pa04.controller.UserCommunicator;
import cs3500.pa04.model.ArtificialPlayer;
import cs3500.pa04.model.BoardObserver;
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
    BoardObserver observer = new BoardObserver();
    UserCommunicator requester = new UserCommunicator(view);
    Controller controller;
    if (args.length == 0) {
      Player player = new ArtificialPlayer(observer);
      Player opponent = new ArtificialPlayer(observer);
      controller = new LocalController(player, opponent, view, requester, observer);
    } else {
      try {
        controller = new ProxyController(new Socket(), new ArtificialPlayer(observer));
      } catch (IOException e) {
        System.err.println("There was an error connecting to the server.");
        return;
      }
    }
    controller.run();
  }
}