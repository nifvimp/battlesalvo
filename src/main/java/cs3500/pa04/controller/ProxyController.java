package cs3500.pa04.controller;

import cs3500.pa04.model.Player;
import java.net.Socket;

public class ProxyController implements Controller {
  private final Socket socket;
  private final Player player;

  public ProxyController(Socket socket, Player player) {
    this.socket = socket;
    this.player = player;
  }

  @Override
  public void run() {

  }
}
