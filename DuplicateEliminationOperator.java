import java.util.*;
import java.io.*;

/**
 * DuplicateEliminationOperator handles queries that contain the clause
 * DISTINCT. It filters through tuples so that only unique values are outputted.
 */
public class DuplicateEliminationOperator extends Operator {
  Tuple old_tuple = new Tuple();
  Tuple cur_tuple = new Tuple();
  Operator sorter;
  ArrayList<String> schema;

  /**
   * Constructor for the DuplicateEliminationOperator class.
   * 
   * @param o The child operator of DuplicateEliminationOperator
   * @return a DuplicateEliminationOperator instance
   */
  public DuplicateEliminationOperator(Operator o) {
    sorter = o;
    sorter.reset();
    schema = sorter.getSchema();
  }

  /**
   * @return The next tuple that is unique from the previous tuple
   */
  public Tuple getNextTuple() {
    cur_tuple = sorter.getNextTuple();
    // Compare cur_tuple to the previous tuple until a unique tuple is found
    while (cur_tuple != null && cur_tuple.compare(old_tuple)) {
      cur_tuple = sorter.getNextTuple();
    }
    old_tuple = cur_tuple;
    return cur_tuple;
  }

  /**
   * @return an ArrayList<String>, each of which are the names of the Columns,
   *         ordered to show their place in a row of the table. The schema is from
   *         the schema.txt in the constructor.
   */
  public ArrayList<String> getSchema() {
    return schema;
  }

  /** Reset to the beginning of the tuples by resetting the sorter */
  public void reset() {
    sorter.reset();
  }

  /** @return The BufferedWriter used by the DuplicateEliminationOperator. */
  public BufferedWriter getWriter() {
    return sorter.getWriter();
  }

  /** Gets the next tuple and writes it to the file until there are none left */
  public void dump() {
    Tuple t = getNextTuple();
    while (t != null) {
      try {
        BufferedWriter writer = sorter.getWriter();
        String out = t.toString();
        writer.write(out, 0, out.length());
        writer.newLine();
      } catch (IOException e) {
        System.out.println(e);
      }
      t = getNextTuple();
    }
  }

}
