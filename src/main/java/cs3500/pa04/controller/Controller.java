package cs3500.pa04.controller;

/**
 * Controls all aspects of a Battle Salvo game.
 */
public interface Controller {

  /**
   * Runs the game.
   * Stars and controls the game flow.
   */
  void run();

  /**
   * Runs the game.
   * Stars and controls the game flow.
   */
  @Deprecated
  boolean run(Object temp);
}