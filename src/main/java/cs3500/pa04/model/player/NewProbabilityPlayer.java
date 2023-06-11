package cs3500.pa04.model.player;

import cs3500.pa04.model.BoardObserver;
import cs3500.pa04.model.Coord;
import cs3500.pa04.model.Orientation;
import cs3500.pa04.model.Ship;
import cs3500.pa04.model.ShipType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Set;

/**
 * Probability player that only considers the possible configurations given the
 * misses on the opponent board. Accounts for hits in a scuffed way, just multiplying
 * the weight the ship has if it overlaps a hit.
 */
// 67.93 for 100
// 71.084 for 10
// 74.871 for 5
// 73.032 for 1
// 78.346 for 2
// 77.797 for 3
// 76.093 for 4
// 78.462 for 2.5
public class NewProbabilityPlayer extends LocalPlayer {
  private static final double WEIGHT = 2.5;
  private final Set<Coord> misses;
  private final Set<Coord> hits;
  private int[][] possibleConfigurations;
  private Set<Ship> shipLocations;
  private Random random;

  public NewProbabilityPlayer(BoardObserver observer) {
    super(observer);
    this.hits = new HashSet<>();
    this.misses = new HashSet<>();
    this.random = new Random();
  }

  @Override
  public String name() {
    return "Probability Player";
  }

  @Override
  public List<Ship> setup(int height, int width, Map<ShipType, Integer> specifications) {
    List<Ship> result = super.setup(height, width, specifications);
    this.possibleConfigurations = new int[height][width];
    this.shipLocations = getPossibleShipLocations();
    this.random = new Random();
    return result;
  }

  /**
   * Computes all the possible ship placements that can be made on the opponent board.
   */
  private Set<Ship> getPossibleShipLocations() {
    Set<Ship> shipLocations = new HashSet<>();
    for (ShipType shipType : ShipType.values()) {
      int size = shipType.getSize();
      for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
          Coord startingCoord = new Coord(j, i);
          if (i + size <= height) {
            Ship ship = new Ship(shipType, startingCoord, Orientation.VERTICAL);
            shipLocations.add(ship);
            for (Coord c : ship.getOccupied()) {
              possibleConfigurations[c.y()][c.x()]++;
            }
          }
          if (j + size <= width) {
            Ship ship = new Ship(shipType, startingCoord, Orientation.HORIZONTAL);
            shipLocations.add(ship);
            for (Coord c : ship.getOccupied()) {
              possibleConfigurations[c.y()][c.x()]++;
            }
          }
        }
      }
    }
    return shipLocations;
  }

  /**
   * Weighs hits into the probability distribution
   *
   * @param weighted array of weights
   */
  private void weighInHits(double[][] weighted) {
    for (Ship ship : shipLocations) {
      Set<Coord> occupied = ship.getOccupied();
      if (occupied.stream().anyMatch(hits::contains)) {
        for (Coord c : occupied) {
          weighted[c.y()][c.x()] += WEIGHT;
        }
      }
    }
  }

  /**
   * Removes ships that overlap misses from the possible ship locations.
   */
  private void removePossibleShipLocations() {
    List<Ship> toRemove = new ArrayList<>();
    for (Ship ship : shipLocations) {
      Set<Coord> occupied = ship.getOccupied();
      if (occupied.stream().anyMatch(misses::contains)) {
        toRemove.add(ship);
        for (Coord c : occupied) {
          possibleConfigurations[c.y()][c.x()]--;
        }
      }
    }
    toRemove.forEach(shipLocations::remove);
  }

  @Override
  public void successfulHits(List<Coord> shotsThatHitOpponentShips) {
    for (Coord hit : shotsThatHitOpponentShips) {
      board.markOpponent(hit, true);
      hits.add(hit);
    }
    lastTurnShots.removeAll(shotsThatHitOpponentShips);
    for (Coord miss : lastTurnShots) {
      board.markOpponent(miss, false);
      misses.add(miss);
    }
    lastTurnShots.clear();
  }

  @Override
  protected List<Coord> loadShots() {
    double[][] probabilityDistribution = new double[height][width];
    weighInHits(probabilityDistribution);
    removePossibleShipLocations();
    // makes a copy of the count array
    int size = shipLocations.size();
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        probabilityDistribution[i][j] =
            (probabilityDistribution[i][j] + possibleConfigurations[i][j]) / size;
      }
    }
    // printing out probability board
//    for (int[] row : possibleConfigurations) {
//      double[] probabilities =
//          Arrays.stream(row).mapToDouble(num -> ((double) num / shipLocations.size()) * 100)
//              .toArray();
//      System.out.println(Arrays.stream(probabilities)
//          .mapToObj(num -> String.format("%3.2f", num))
//          .toList());
//    }
    // shot logic
    // TODO: start with random checkerboard shots

    // TODO: implement more distributed shooting

    List<Coord> shots = new ArrayList<>();
    Set<Coord> validShots = board.validShots();
    Map<Coord, Double> possibleShots = new HashMap<>();
    PriorityQueue<Coord> queue =
        new PriorityQueue<>(Comparator.comparingDouble(possibleShots::get).reversed());
    for (Coord coord : validShots) {
      Double probability = probabilityDistribution[coord.y()][coord.x()];
      possibleShots.put(coord, probability);
      queue.add(coord);
    }
    for (int i = Math.min(board.shipsLeft(), validShots.size()); i > 0; i--) {
      shots.add(queue.poll());
    }
    return shots;
  }
}