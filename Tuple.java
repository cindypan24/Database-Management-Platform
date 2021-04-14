import java.util.ArrayList;

/**
 * Tuple keeps track of the data in one line of the file by storing it in an
 * arraylist. One tuple represents one table entry.
 */
public class Tuple {
  ArrayList<Integer> content = new ArrayList<Integer>();

  /**
   * Constructor for the ScanOperator class.
   * 
   * @param c A line of the file
   * @return a Tuple instance
   */
  public Tuple(String c) {
    String[] s = c.split(",");
    for (String str : s) {
      content.add(Integer.parseInt(str));
    }
  }

  /**
   * Constructor for the ScanOperator class.
   * 
   * @return a Tuple instance
   */
  public Tuple() {
  }

  /**
   * Constructor for the ScanOperator class.
   * 
   * @param c An arraylist of ints, a table entry
   * @return a Tuple instance
   */
  public Tuple(ArrayList<Integer> c) {
    content = c;
  }

  /**
   * @param b A tuple to be combined
   * @return The combined tuple of b and self
   */
  public Tuple combine(Tuple b) {
    Tuple output = new Tuple();
    output.content = new ArrayList<Integer>(content);
    output.content.addAll(b.content);
    return output;
  }

  /**
   * @param b A tuple to be compared
   * @return true if b and self are equal and false otherwise
   */
  public boolean compare(Tuple b) {
    return content.equals(b.content);
  }

  /**
   * @param i The column we are looking for
   * @return The entry in column i
   */
  public int getColumn(int i) {
    return content.get(i);
  }

  /**
   * @return The string representation of the tuple
   */
  public String toString() {
    String s = "";
    for (int i = 0; i < content.size(); i++) {
      s += content.get(i) + ",";
    }
    return s.substring(0, s.length() - 1);
  }
}