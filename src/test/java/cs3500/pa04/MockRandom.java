package cs3500.pa04;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Random;
import java.util.concurrent.Callable;

/**
 * Creates a mock Random object that uses the values that has been injected into it
 * instead of actual pseudorandom values.
 */
public class MockRandom extends Random {
  private static final long DEFAULT_SEED = 0;
  private final Deque<Object> buf = new ArrayDeque<>();
  private boolean useInjected = true;

  /**
   * Creates a new mock random.
   */
  public MockRandom() {
    super(DEFAULT_SEED);
  }

  /**
   * Creates a new mock random with the given seed.
   *
   * @param seed the initial seed
   */
  public MockRandom(long seed) {
    super(seed);
  }

  /**
   * Injects the mock random buffer with the given values to be read later.
   *
   * @param values values to inject
   * @return a reference to this object
   */
  public MockRandom inject(Object... values) {
    for (Object value : values) {
      buf.addLast(value);
    }
    return this;
  }

  /**
   * Injects the mock random buffer with the given values to be read later.
   *
   * @param values values to inject
   * @return a reference to this object
   */
  public MockRandom inject(Collection<Object> values) {
    for (Object value : values) {
      buf.addLast(value);
    }
    return this;
  }

  /**
   * Injects a double value between 0.0d (inclusive) to 1.0d (exclusive) that will result
   * in the expected integer output after being multiplied by the bound and cast to an
   * integer.
   *
   * @param expected expected integer result
   * @param bound    upperbound / multiplier
   * @return a reference to this object
   */
  public MockRandom injectDouble(int expected, int bound) {
    buf.addLast((double) expected / bound);
    return this;
  }

  /**
   * Tells the mock random to return the values from its buffer.
   *
   * @return a reference to this object
   */
  public MockRandom useInjected() {
    useInjected = true;
    return this;
  }

  /**
   * Tells the mock random to return pseudorandom values based on the normal java Random
   * implementation.
   */
  public MockRandom useRandom() {
    useInjected = false;
    return this;
  }

  @Override
  public int nextInt() {
    return getNext(Integer.class, super::nextInt);
  }

  @Override
  public int nextInt(int bound) {
    int next = getNext(Integer.class, () -> super.nextInt(bound));
    if (next < 0 || next >= bound) {
      throw new IllegalStateException("Injected value is out of bounds.");
    }
    return next;
  }

  @Override
  public int nextInt(int lower, int higher) {
    int next = getNext(Integer.class, () -> super.nextInt(lower, higher));
    if (next < lower || next >= higher) {
      throw new IllegalStateException("Injected value is out of bounds.");
    }
    return next;
  }

  @Override
  public long nextLong() {
    return getNext(Long.class, super::nextLong);
  }

  @Override
  public boolean nextBoolean() {
    return getNext(Boolean.class, super::nextBoolean);
  }

  @Override
  public float nextFloat() {
    float next = getNext(Float.class, super::nextFloat);
    if (next < 0f || next >= 1.0f) {
      throw new IllegalStateException("Injected value is out of bounds.");
    }
    return next;
  }

  @Override
  public double nextDouble() {
    double next = getNext(Double.class, super::nextDouble);
    if (next < 0.0d || next > 1.0d) {
      System.out.println(next);
      throw new IllegalStateException("Injected value is out of bounds.");
    }
    return next;
  }

  /**
   * Attempt to read the next value in the buffer as an object of the expected type.
   * If the buffer is empty, this method return the given default method.
   *
   * @param expected      expected type of next value in the buffer
   * @param defaultMethod default method to call if the buffer is empty
   * @param <T>           type of expected value
   * @return the next value in the buffer
   * @throws IllegalArgumentException if the value queued into the mock random buffer
   *                                  does not match the type requested.
   */
  private <T> T getNext(Class<T> expected, Callable<T> defaultMethod) {
    if (buf.isEmpty() || !useInjected) {
      try {
        return defaultMethod.call();
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    T next;
    try {
      next = expected.cast(buf.pollFirst());
    } catch (ClassCastException e) {
      e.printStackTrace();
      throw new IllegalStateException("Injected value is not the expected type.");
    }
    return next;
  }
}