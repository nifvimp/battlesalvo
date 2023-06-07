package pa04;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

/**
 * Mutable input stream for testing.
 *
 * @implNote Can be accessed as a Readable using the toReadable method.
 */
public class TestInputStream extends ByteArrayInputStream {
  /**
   * Creates a new test input stream with the given input.
   *
   * @param input input
   */
  public TestInputStream(byte[] input) {
    super(input);
  }

  /**
   * Creates a new empty test input stream.
   */
  public TestInputStream() {
    this(new byte[0]);
  }

  /**
   * Adds the given character sequence to the input stream succeeded by a line break character
   * (to make the scanner's nextLine function work correctly).
   *
   * @param input character sequence to add to this input stream.
   */
  public void input(CharSequence input) {
    addBytes((input.toString() + '\n').getBytes());
  }

  /**
   * Adds the given bytes to this input stream's buffer.
   *
   * @param bytes bytes to add
   */
  private void addBytes(byte[] bytes) {
    if (count == 0) {
      count += bytes.length;
      buf = bytes;
    } else {
      int originalLen = buf.length;
      int appendLen = bytes.length;
      byte[] result = Arrays.copyOf(buf, originalLen + appendLen);
      System.arraycopy(bytes, 0, result, originalLen, appendLen);
      count += appendLen;
      buf = result;
    }
  }

  /**
   * Returns a Readable object representing this input stream.
   *
   * @return readable representation of this input stream.
   */
  public Readable toReadable() {
    return new InputStreamReader(this);
  }
}
