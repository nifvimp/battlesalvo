package cs3500.pa04.model;

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
import java.util.concurrent.TimeUnit;

// TODO: the main issue with the player's logic is that it randomly places ships
//  blindly, so it wastes a lot of attempts trying to place down ships that have
//  most likely have already been sunk.
//  the current best way to improve the AI is to make something that will eliminate
//  ships from the specifications (subtracting from it), but the challenge is taking
//  out the right ship. We can tell if we sunk something only after 2 turns of shooting
//  via the number of shots received. We also somehow have to remove the places where
//  we think the ship that sunk was from the hits list to keep functionality. probably
//  should just add them to the miss list.
public class ProbabilityPlayer extends LocalPlayer {
  private static final int MILLISECONDS_THREAD_INTERRUPT = 1000; // make sure to leave time
  private static final int THREADS = 50; // the more threads you have, the slower the response is
  private double[][] probabilityDistribution;
  private Map<ShipType, Integer> specifications;
  private Map<ShipType, List<Ship>> shipLocations;
  private Map<Ship, Set<Ship>> incompatible;
  private final Set<Coord> misses;
  private final Set<Coord> hits;
  private Random random;
  private long count;
  private int runs;

  public ProbabilityPlayer(BoardObserver observer) {
    super(observer);
    this.hits = new HashSet<>();
    this.misses = new HashSet<>();
  }

  @Override
  public String name() {
    return "Probability Player";
  }

  @Override
  public List<Ship> setup(int height, int width, Map<ShipType, Integer> specifications) {
    List<Ship> result = super.setup(height, width, specifications);
    this.specifications = specifications;
    this.random = new Random();
    return result;
  }

  private void calculateDistribution() {
    // this pre-logic computation is important for decent multi-threading time
    this.probabilityDistribution = new double[height][width];
    this.shipLocations = new HashMap<>();
    this.incompatible = new HashMap<>();
    this.count = 0;
    getPossibleShipLocations();
    getIncompatibleLocations();

    // where the multi-threading starts
    List<Thread> threads = new ArrayList<>();
    for (int i = 0; i < THREADS; i++) {
      Thread thread = new Simulation();
      thread.start();
      threads.add(thread);
    }
    // waits a certain amount of time before stopping all threads
    // it takes a while to actually stop all the threads, so leave sufficient leftover time
    try {
      TimeUnit.MILLISECONDS.sleep(MILLISECONDS_THREAD_INTERRUPT);
    } catch (InterruptedException ignored) {
      // An empty catch block
    }
    for (Thread thread : threads) {
      thread.interrupt();
    }
    System.out.println("Attempts: " + runs);
    System.out.println("Sims Complete: " + count);

    // prints out probability distribution
    for (double[] row : probabilityDistribution) {
      row = Arrays.stream(row).map(num -> (num / count) * 100).toArray();
      System.out.println(Arrays.stream(row)
          .mapToObj(num -> String.format("%3.2f", num))
          .toList());
    }
  }

  /**
   * Computes all the possible ship placements that can be made on the opponent board given
   * the current information available.
   * Removes all ship placements that overhang the board or overlap with a miss.
   */
  private void getPossibleShipLocations() {
    for (ShipType shipType : specifications.keySet()) {
      List<Ship> possible = new ArrayList<>();
      int size = shipType.getSize();
      // could make cleaner
      for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
          Coord startingCoord = new Coord(j, i);
          // TODO: could separate out into helper
          if (i + size <= height) {
            Ship potential = new Ship(shipType, startingCoord, Orientation.VERTICAL);
            if (potential.getOccupied().stream().noneMatch(misses::contains)) {
              possible.add(potential);
            }
          }
          if (j + size <= width) {
            Ship potential = new Ship(shipType, startingCoord, Orientation.HORIZONTAL);
            if (potential.getOccupied().stream().noneMatch(misses::contains)) {
              possible.add(potential);
            }
          }
        }
      }
      shipLocations.put(shipType, possible);
    }
  }

  /**
   * Computes the list of conflicting ship placements for each valid ship placement.
   */
  private void getIncompatibleLocations() {
    List<Ship> ships = new ArrayList<>();
    for (List<Ship> possible : shipLocations.values()) {
      ships.addAll(possible);
    }
    for (Ship ship : ships) {
      Set<Ship> conflicts = new HashSet<>();
      Set<Coord> occupied = ship.getOccupied();
      for (Ship other : ships) {
        if (other.getOccupied().stream().anyMatch(occupied::contains)) {
          conflicts.add(other);
        }
      }
      conflicts.remove(ship);
      incompatible.put(ship, conflicts);
    }
  }

  // TODO: extract inner class outside
  private class Simulation extends Thread {
    @Override
    public void run() {
      while(!this.isInterrupted()) {
        runs++;
        simulateShipPlacement();
      }
    }
  }

  /**
   * Simulates the random placement of ships on a board and logs the placements if the randomly
   * made placements match a possibility of what the opponent player's board could be.
   */
  private void simulateShipPlacement() {
    List<Ship> placed = new ArrayList<>();
    for (ShipType shipType : ShipType.values()) {
      for (int i = 0; i < specifications.get(shipType); i++) {
        List<Ship> possible = shipLocations.get(shipType);
        Ship toPlace = possible.get(random.nextInt(0, possible.size()));
        if (placed.contains(toPlace)) {
          return;
        }
        Set<Ship> conflict = incompatible.get(toPlace);
        if (placed.stream().anyMatch(conflict::contains)) {
          return;
        }
        placed.add(toPlace);
      }
    }
    Set<Coord> occupied = new HashSet<>();
    for (Ship ship : placed) {
      occupied.addAll(ship.getOccupied());
    }
    if (!hits.isEmpty() && !occupied.containsAll(hits)) {
      return;
    }
    for (Coord coord : occupied) {
      probabilityDistribution[coord.y()][coord.x()]++;
    }
    count++;
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
  public List<Coord> takeShots() {
    calculateDistribution();
    return super.takeShots();
  }

  // TODO: should shoot randomly if probabilities are insignificant
  //  (when complete sims are in the like 0 - 10 range).
  @Override
  protected List<Coord> loadShots() {
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
