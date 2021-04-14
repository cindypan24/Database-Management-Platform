import java.util.*;
import java.io.*;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.schema.*;
import java.io.BufferedWriter;

/**
 * JoinOperator handles queries that require the joining of tuples. This works
 * for joining two tables as well as self joins.
 */
public class JoinOperator extends Operator {

  Operator left, right;
  Expression express;
  Tuple leftTuple;
  ArrayList<String> leftSchema, rightSchema, newSchema = new ArrayList<String>();
  ExpVisitor vis;

  String leftTable = "", rightTable = "";

  /**
   * Constructor for the JoinOperator class.
   * 
   * @param l The left child operator of JoinOperator
   * @param r The right child operator of JoinOperator
   * @return a JoinOperator instance
   */
  public JoinOperator(Operator l, Operator r, Expression e) {

    // Initalize class fields
    left = l;
    left.reset();
    leftTuple = left.getNextTuple();

    right = r;
    right.reset();
    express = e;

    leftSchema = l.getSchema();
    rightSchema = r.getSchema();
    vis = new ExpVisitor();

    // Format columns so that they appear as, for example, "Sailors.A" in
    // the new schema instead of just "A"
    if (l instanceof ScanOperator) {
      ScanOperator nl = (ScanOperator) left;
      leftTable = nl.getTableName();
      leftTable = leftTable + ".";
    } else {
      if (l instanceof SelectOperator) {
        SelectOperator nl = (SelectOperator) left;
        leftTable = nl.getTableName();
        leftTable = leftTable + ".";
      }
    }

    if (r instanceof ScanOperator) {
      ScanOperator nl = (ScanOperator) right;
      rightTable = nl.getTableName();
      rightTable = rightTable + ".";
    } else {
      if (r instanceof SelectOperator) {
        SelectOperator nl = (SelectOperator) right;
        rightTable = nl.getTableName();
        rightTable = rightTable + ".";
      }
    }
    join();
    vis.setSchema(newSchema);
  }

  /**
   * Create the new schema of the joined tuples by combining the left schema and
   * the right schema.
   */
  private void join() {
    for (int i = 0; i < leftSchema.size(); i++) {
      String col = leftSchema.get(i);
      newSchema.add(leftTable + col);
    }
    for (int i = 0; i < rightSchema.size(); i++) {
      String col = rightSchema.get(i);
      newSchema.add(rightTable + col);
    }
  }

  /**
   * @return The next tuple that satisfies the expression. For every tuple in the
   *         left table, iterate through all of the tuples in the right table
   *         before going to the next tuple in the left table. Combine each pair
   *         of tuples and check to if the expression evaluates to true.
   */
  public Tuple getNextTuple() {
    Tuple rightTuple = right.getNextTuple();
    if (rightTuple == null) {
      leftTuple = left.getNextTuple();
      right.reset();
      rightTuple = right.getNextTuple();
    }

    while (leftTuple != null) {
      while (rightTuple != null) {
        Tuple newOne = new Tuple();
        newOne = leftTuple.combine(rightTuple);

        vis.setTuple(newOne);
        express.accept(vis);
        if (vis.getOutcome()) {
          return newOne;
        } else {
          rightTuple = right.getNextTuple();
        }
      }
      leftTuple = left.getNextTuple();
      right.reset();
      rightTuple = right.getNextTuple();
    }
    return leftTuple;
  }

  /** Reset to the first tuples in the right and left table */
  public void reset() {
    left.reset();
    leftTuple = left.getNextTuple();
    right.reset();
  }

  /**
   * @return an ArrayList<String>, each of which are the names of the Columns,
   *         ordered to show their place in a row of the table. The schema is from
   *         the schema.txt in the constructor.
   */
  public ArrayList<String> getSchema() {
    return newSchema;
  }

  /** @return The BufferedWriter used by the JoinOperator. */
  public BufferedWriter getWriter() {
    return left.getWriter();
  }

  /** Gets the next tuple and writes it to the file until there are none left */
  public void dump() {
    Tuple t = getNextTuple();
    while (t != null) {
      try {
        BufferedWriter writer = left.getWriter();
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