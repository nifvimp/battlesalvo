package cs3500.pa04.controller;

import cs3500.pa03.model.Coord;
import cs3500.pa03.model.ShipType;
import cs3500.pa03.view.View;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Collects input from the user when requested.
 */
public class InputCollector {
  private final View view;

  /**
   * Constructor for InputCollector.
   *
   * @param view the view to fetch input from
   */
  public InputCollector(View view) {
    this.view = view;
  }

  /**
   * Retrieves the board dimensions from the user within the specified range.
   *
   * @param minDimensions the minimum allowed dimensions for the board
   * @param maxDimensions the maximum allowed dimensions for the board
   * @return the board dimensions as a Coord
   */
  public Coord getParameters(int minDimensions, int maxDimensions) {
    int width = 0;
    int height = 0;
    boolean retry = false;
    while (!((width >= minDimensions && width <= maxDimensions)
        && ((height >= minDimensions && height <= maxDimensions)))) {
      try {
        String input = view.getParameters(retry);
        retry = true;
        int[] nums = numExtractor(2, input);
        width = nums[0];
        height = nums[1];
      } catch (Exception ignored) {
        // Empty catch block: intentionally left blank
      }
    }
    return new Coord(width, height);
  }

  /**
   * Extracts the specified number of integers from the given text.
   *
   * @param numsToExtract the number of integers to extract
   * @param text the text containing the integers
   * @return an array containing the specified number of extracted integers
   * @throws IllegalArgumentException if there are more inputs than expected
   *        or if there is a problem extracting
   */
  private int[] numExtractor(int numsToExtract, String text) {
    String[] split = text.split(" ");
    if (split.length > numsToExtract) {
      throw new IllegalArgumentException("more inputs than expected");
    }
    int[] nums = new int[numsToExtract];
    for (int i = 0; i < nums.length; i++) {
      int num = Integer.parseInt(split[i]);
      nums[i] = num;
    }
    return nums;
  }

  /**
   * Fetches the fleet configuration from the user.
   *
   * @param maxFleetSize the total number of ships allowed.
   * @return a map of ShipType to number of ships.
   */
  public Map<ShipType, Integer> getFleet(int maxFleetSize) {
    int[] ships = new int[4];
    boolean retry = false;
    while (!(Arrays.stream(ships).sum() <= maxFleetSize
        && Arrays.stream(ships).allMatch(i -> i > 0))) {
      try {
        String input = view.getFleet(retry, maxFleetSize);
        retry = true;
        ships = numExtractor(4, input);
      } catch (Exception ignored) {
        // Empty catch block: intentionally left blank
      }
    }
    Map<ShipType, Integer> shipMap = new HashMap<>();
    shipMap.put(ShipType.CARRIER, ships[0]);
    shipMap.put(ShipType.BATTLESHIP, ships[1]);
    shipMap.put(ShipType.DESTROYER, ships[2]);
    shipMap.put(ShipType.SUBMARINE, ships[3]);
    return shipMap;
  }

  /**
   * Fetches the user's shots on the opponent's board.
   * If an invalid shot is entered starts over from scratch.
   *
   * @param numShots the number of shots to get
   * @param width the width of the board
   * @param height the height of the board
   * @return a list of the coordinates being shot at
   */
  public List<Coord> getShots(int numShots, int width, int height) {
    List<Coord> shots = null;
    boolean retry = false;
    boolean shotsCollected = false;
    while (!shotsCollected) {
      shotsCollected = true;
      shots = new ArrayList<>();
      view.promptShots(numShots, retry);
      retry = true;
      for (int i = 0; i < numShots; i++) {
        try {
          String input = view.getShot();
          int[] potentialShot = numExtractor(2, input);
          int x = potentialShot[0];
          int y = potentialShot[1];
          if (x >= 0 && x < width && y >= 0 && y < height) {
            shots.add(new Coord(x, y));
          } else {
            shotsCollected = false;
            break;
          }
        } catch (Exception ignored) {
          shotsCollected = false;
          break;
        }
      }

    }
    return shots;
  }

}
