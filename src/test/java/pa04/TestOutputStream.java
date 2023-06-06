package pa04;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * An output stream for testing.
 *
 * @implNote It can also act as an Appendable and PrintStream.
 */
public class TestOutputStream extends OutputStream implements Appendable {
  /**
   * Contents of this output stream.
   */
  private final StringBuilder output;

  /**
   * Creates a new empty test output stream.
   */
  public TestOutputStream() {
    output = new StringBuilder();
  }

  @Override
  public synchronized void write(int b) {
    output.append((char) b);
  }

  @Override
  public Appendable append(CharSequence csq) {
    output.append(csq);
    return this;
  }

  @Override
  public Appendable append(CharSequence csq, int start, int end) {
    output.append(csq, start, end);
    return this;
  }

  @Override
  public Appendable append(char c) {
    output.append(c);
    return this;
  }

  @Override
  public String toString() {
    return output.toString();
  }

  /**
   * Transforms this output stream into a print stream.
   *
   * @return print stream of this output stream
   */
  public PrintStream toPrintStream() {
    return new PrintStream(this);
  }
}
